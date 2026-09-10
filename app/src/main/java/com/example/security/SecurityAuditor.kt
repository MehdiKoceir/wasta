package com.example.security

import com.example.data.model.AuditLogEntity
import com.example.data.model.InvoiceEntity
import com.example.data.model.JobEntity
import com.example.data.model.UserEntity
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

sealed class SecurityResult<out T> {
    data class Success<T>(val data: T) : SecurityResult<T>()
    data class Denied(val reason: String, val statusCode: Int = 403) : SecurityResult<Nothing>()
}

object SecurityAuditor {

    // Rate limiter tracker: userId -> timestamp count
    private val aiRateLimiterMap = ConcurrentHashMap<String, MutableList<Long>>()
    // Concurrency lock for job acceptance: jobId -> technicianId that holds lock
    private val jobAcceptanceLocks = ConcurrentHashMap<String, String>()

    /**
     * Scenario 1: Customer authorization check (IDOR protection)
     */
    fun checkCustomerJobAccess(job: JobEntity, currentUser: UserEntity): SecurityResult<Unit> {
        if (currentUser.role == "ADMIN") return SecurityResult.Success(Unit)
        if (job.customerId == currentUser.id) return SecurityResult.Success(Unit)
        return SecurityResult.Denied(
            reason = "Access Denied: IDOR Protection. You do not own customer job #${job.id}.",
            statusCode = 403
        )
    }

    /**
     * Scenario 2: Technician authorization check
     */
    fun checkTechnicianJobModify(job: JobEntity, currentUser: UserEntity, technicianId: String): SecurityResult<Unit> {
        if (currentUser.role == "ADMIN") return SecurityResult.Success(Unit)
        if (job.professionalId == technicianId && currentUser.id == job.professionalId) {
            return SecurityResult.Success(Unit)
        }
        return SecurityResult.Denied(
            reason = "Access Denied: You are not assigned to job #${job.id}. Modifications prohibited.",
            statusCode = 403
        )
    }

    /**
     * Scenario 3: Atomic Concurrency Lock for Job Acceptance (Prevents double booking / race conditions)
     */
    @Synchronized
    fun attemptAtomicJobAcceptance(jobId: String, technicianId: String): SecurityResult<Unit> {
        val existingHolder = jobAcceptanceLocks.putIfAbsent(jobId, technicianId)
        return if (existingHolder == null || existingHolder == technicianId) {
            SecurityResult.Success(Unit)
        } else {
            SecurityResult.Denied(
                reason = "Concurrency Conflict: Job #$jobId was already accepted by technician #$existingHolder simultaneously.",
                statusCode = 409
            )
        }
    }

    /**
     * Scenario 4: Authoritative price validation
     */
    fun validateInvoicePayment(invoice: InvoiceEntity, clientSubmittedAmountDA: Int): SecurityResult<Unit> {
        if (clientSubmittedAmountDA != invoice.totalDA) {
            return SecurityResult.Denied(
                reason = "Payment Tampering Detected: Client requested $clientSubmittedAmountDA DA, but authoritative total is ${invoice.totalDA} DA.",
                statusCode = 400
            )
        }
        return SecurityResult.Success(Unit)
    }

    /**
     * Scenario 5: Review eligibility validation
     */
    fun validateReviewEligibility(job: JobEntity, invoice: InvoiceEntity?, customerId: String): SecurityResult<Unit> {
        if (job.customerId != customerId) {
            return SecurityResult.Denied("Only the verified customer who booked this job can review it.")
        }
        if (job.status != "COMPLETED") {
            return SecurityResult.Denied("Job must be marked COMPLETED before submitting a verified review.")
        }
        if (invoice == null || invoice.status != "PAID") {
            return SecurityResult.Denied("Job invoice must be confirmed as PAID to generate a verified review.")
        }
        return SecurityResult.Success(Unit)
    }

    /**
     * Scenario 6: Job completion requirements
     */
    fun validateJobCompletionPrerequisites(job: JobEntity, hasProofOfWork: Boolean): SecurityResult<Unit> {
        if (!hasProofOfWork) {
            return SecurityResult.Denied("Technician must upload required work proof before marking job as COMPLETED.")
        }
        if (job.status != "IN_PROGRESS" && job.status != "SCHEDULED") {
            return SecurityResult.Denied("Invalid state transition. Job is currently ${job.status}.")
        }
        return SecurityResult.Success(Unit)
    }

    /**
     * Scenario 7: Malicious file upload filter
     */
    fun validateProofUpload(fileName: String, mimeType: String, sizeBytes: Long): SecurityResult<Unit> {
        val allowedExtensions = listOf("jpg", "jpeg", "png", "webp")
        val ext = fileName.substringAfterLast('.', "").lowercase()
        if (ext !in allowedExtensions) {
            return SecurityResult.Denied("Security Alert: File extension .$ext is not permitted. Only images allowed.")
        }
        if (!mimeType.startsWith("image/")) {
            return SecurityResult.Denied("Security Alert: Invalid MIME type $mimeType. Non-image files rejected.")
        }
        if (sizeBytes > 5 * 1024 * 1024) { // 5MB limit
            return SecurityResult.Denied("Payload exceeds 5MB limit.")
        }
        return SecurityResult.Success(Unit)
    }

    /**
     * Scenario 8: Extreme input validation
     */
    fun validateInputLength(text: String, maxChars: Int = 1000): SecurityResult<String> {
        if (text.length > maxChars) {
            return SecurityResult.Denied("Payload too large. Exceeds max $maxChars characters limit.", statusCode = 413)
        }
        return SecurityResult.Success(sanitizeInput(text, maxChars))
    }

    /**
     * Sanitizes user string inputs against HTML/Script injection, control characters, and bounds length.
     */
    fun sanitizeInput(text: String, maxChars: Int = 1000): String {
        val trimmed = text.trim()
        val bounded = if (trimmed.length > maxChars) trimmed.take(maxChars) else trimmed
        return bounded
            .replace("<script", "&lt;script", ignoreCase = true)
            .replace("</script>", "&lt;/script&gt;", ignoreCase = true)
            .replace("<iframe", "&lt;iframe", ignoreCase = true)
            .replace("javascript:", "", ignoreCase = true)
            .replace(Regex("[\u0000-\u0008\u000B\u000C\u000E-\u001F]"), "")
    }

    /**
     * Sanitizes and validates phone numbers for Algerian numbers (+213 / 05 / 06 / 07 / 02X)
     */
    fun sanitizePhoneNumber(rawPhone: String): String {
        return rawPhone.filter { it.isDigit() || it == '+' || it == ' ' || it == '-' }
            .trim()
            .take(20)
    }

    /**
     * Scenario 9: AI Rate limiter
     */
    fun checkAiRateLimit(userId: String, maxCallsPerMinute: Int = 8): SecurityResult<Unit> {
        val now = System.currentTimeMillis()
        val window = 60_000L
        val timestamps = aiRateLimiterMap.computeIfAbsent(userId) { mutableListOf() }
        synchronized(timestamps) {
            timestamps.removeAll { now - it > window }
            if (timestamps.size >= maxCallsPerMinute) {
                return SecurityResult.Denied("AI Rate Limit Exceeded. Max $maxCallsPerMinute queries per minute allowed.", statusCode = 429)
            }
            timestamps.add(now)
        }
        return SecurityResult.Success(Unit)
    }

    /**
     * Scenario 10: Role escalation prevention
     */
    fun checkRoleChangeAllowed(currentRole: String, requestedRole: String): SecurityResult<Unit> {
        if (requestedRole == "ADMIN" && currentRole != "ADMIN") {
            return SecurityResult.Denied("Privilege Escalation Blocked: Role cannot be modified to ADMIN from client.", statusCode = 403)
        }
        return SecurityResult.Success(Unit)
    }

    /**
     * Audit log helper
     */
    fun createAuditLog(
        actorUserId: String,
        actorRole: String,
        action: String,
        details: String,
        securityLevel: String = "INFO"
    ): AuditLogEntity {
        return AuditLogEntity(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            actorUserId = actorUserId,
            actorRole = actorRole,
            action = action,
            details = details,
            securityLevel = securityLevel
        )
    }
}
