package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index("role"), Index("wilaya")]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val role: String, // CUSTOMER, PROFESSIONAL, ADMIN
    val fullName: String,
    val phone: String,
    val email: String,
    val wilaya: String,
    val commune: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "professionals",
    indices = [Index("userId"), Index("baseWilaya"), Index("isAvailable")]
)
data class ProfessionalEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val phone: String,
    val specialties: String, // Comma separated, e.g. "Plumbing, Water heaters, Boilers"
    val wilayasCovered: String, // "Blida, Alger, Tipaza"
    val baseWilaya: String, // "Blida"
    val rating: Float, // 4.9
    val completedJobsCount: Int, // 234
    val avgResponseMinutes: Int, // 7
    val priceMinDA: Int, // 2000
    val priceMaxDA: Int, // 8000
    val identityVerified: Boolean,
    val phoneVerified: Boolean,
    val profileVerified: Boolean,
    val isAvailable: Boolean = true,
    val cancellationRatePercent: Float = 1.2f,
    val completionRatePercent: Float = 98.8f,
    val disputeRatePercent: Float = 0.4f,
    val totalEarningsDA: Long = 0L,
    val bio: String = "",
    val primaryCategory: String = "Plomberie & Chauffage",
    val serviceCategories: String = "Chauffe-eau & Chaudières, Plomberie Sanitaire, Recherche de Fuite",
    val yearsExperience: Int = 10,
    val punctualityRating: Float = 4.9f,
    val craftsmanshipRating: Float = 5.0f,
    val priceFairnessRating: Float = 4.8f,
    val cleanlinessRating: Float = 4.9f,
    val fiveStarsCount: Int = 210,
    val fourStarsCount: Int = 20,
    val threeStarsCount: Int = 4,
    val twoStarsCount: Int = 0,
    val oneStarsCount: Int = 0,
    val insuranceVerified: Boolean = true,
    val policeRecordVerified: Boolean = true,
    val workshopAddress: String = "Boufarik Centre, Blida"
)

@Entity(
    tableName = "service_requests",
    indices = [Index("customerId"), Index("assignedProfessionalId"), Index("status")]
)
data class ServiceRequestEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val problemDescription: String,
    val category: String,
    val service: String,
    val specialtyNeeded: String,
    val urgency: String, // low, medium, high, emergency
    val wilaya: String,
    val commune: String,
    val approximateArea: String,
    val exactAddress: String, // Protected: only revealed to accepted pro
    val status: String, // PENDING, MATCHED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED
    val assignedProfessionalId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "jobs",
    indices = [Index("requestId"), Index("customerId"), Index("professionalId"), Index("status")]
)
data class JobEntity(
    @PrimaryKey val id: String,
    val requestId: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val professionalId: String,
    val professionalName: String,
    val status: String, // ACCEPTED, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    val scheduledDate: String, // e.g. "2026-09-08"
    val scheduledTimeSlot: String, // e.g. "09:00 - 11:00"
    val laborDA: Int = 3000,
    val materialsDA: Int = 0,
    val totalDA: Int = 3000,
    val beforePhotoDesc: String? = null,
    val afterPhotoDesc: String? = null,
    val completionNotes: String? = null,
    val acceptedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

@Entity(
    tableName = "invoices",
    indices = [Index("jobId"), Index("professionalId"), Index("customerId"), Index("status")]
)
data class InvoiceEntity(
    @PrimaryKey val id: String,
    val jobId: String,
    val professionalId: String,
    val professionalName: String,
    val customerId: String,
    val serviceTitle: String,
    val laborDA: Int,
    val materialsDA: Int,
    val totalDA: Int,
    val status: String, // PENDING, PAID, REFUNDED
    val paymentMethod: String = "CASH_ON_DELIVERY", // CASH_ON_DELIVERY, BARIDIMOB, CIB
    val idempotencyKey: String,
    val createdAt: Long = System.currentTimeMillis(),
    val paidAt: Long? = null
)

@Entity(
    tableName = "reviews",
    indices = [Index("jobId"), Index("professionalId"), Index("customerId")]
)
data class ReviewEntity(
    @PrimaryKey val id: String,
    val jobId: String,
    val customerId: String,
    val customerName: String,
    val professionalId: String,
    val rating: Float, // 1 to 5
    val comment: String,
    val verifiedJob: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "disputes",
    indices = [Index("jobId"), Index("openedByUserId"), Index("status")]
)
data class DisputeEntity(
    @PrimaryKey val id: String,
    val jobId: String,
    val openedByUserId: String,
    val openedByRole: String,
    val reason: String,
    val status: String, // OPEN, RESOLVED, REJECTED
    val resolutionNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "audit_logs",
    indices = [Index("actorUserId"), Index("timestamp"), Index("securityLevel")]
)
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actorUserId: String,
    val actorRole: String,
    val action: String,
    val details: String,
    val securityLevel: String = "INFO" // INFO, WARNING, SECURITY_ALERT
)
