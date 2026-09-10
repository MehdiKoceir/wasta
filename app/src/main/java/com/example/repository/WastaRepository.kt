package com.example.repository

import com.example.ai.AiTriageEngine
import com.example.ai.AiTriageResult
import com.example.data.WastaDatabase
import com.example.data.model.*
import com.example.security.SecurityAuditor
import com.example.security.SecurityResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class WastaRepository(private val database: WastaDatabase) {

    private val userDao = database.userDao()
    private val proDao = database.professionalDao()
    private val requestDao = database.serviceRequestDao()
    private val jobDao = database.jobDao()
    private val invoiceDao = database.invoiceDao()
    private val reviewDao = database.reviewDao()
    private val disputeDao = database.disputeDao()
    private val auditDao = database.auditLogDao()

    val allProfessionals: Flow<List<ProfessionalEntity>> = proDao.getAllProfessionals()
    val allRequests: Flow<List<ServiceRequestEntity>> = requestDao.getAllRequests()
    val allJobs: Flow<List<JobEntity>> = jobDao.getAllJobs()
    val allInvoices: Flow<List<InvoiceEntity>> = invoiceDao.getAllInvoices()
    val allDisputes: Flow<List<DisputeEntity>> = disputeDao.getAllDisputes()
    val recentAuditLogs: Flow<List<AuditLogEntity>> = auditDao.getRecentAuditLogs()

    fun getRequestsForCustomer(customerId: String): Flow<List<ServiceRequestEntity>> =
        requestDao.getRequestsByCustomer(customerId)

    fun getJobsForCustomer(customerId: String): Flow<List<JobEntity>> =
        jobDao.getJobsForCustomer(customerId)

    fun getJobsForProfessional(proId: String): Flow<List<JobEntity>> =
        jobDao.getJobsForProfessional(proId)

    fun getReviewsForProfessional(proId: String): Flow<List<ReviewEntity>> =
        reviewDao.getReviewsForProfessional(proId)

    suspend fun getProfessionalById(id: String): ProfessionalEntity? =
        proDao.getProfessionalById(id)

    suspend fun getJobById(id: String): JobEntity? =
        jobDao.getJobById(id)

    suspend fun getInvoiceByJobId(jobId: String): InvoiceEntity? =
        invoiceDao.getInvoiceByJobId(jobId)

    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        // Seed verified professionals and users if empty
        val existing = proDao.getProfessionalById("pro_ahmed_b")
        if (existing == null) {
            val seedUsers = listOf(
                UserEntity("usr_cust_mehdi", "CUSTOMER", "Mehdi K.", "+213 555 12 34 56", "mehdi@wasta.dz", "Blida", "Ouled Yaich"),
                UserEntity("usr_pro_ahmed", "PROFESSIONAL", "Ahmed B.", "+213 770 98 76 54", "ahmed.plombier@wasta.dz", "Blida", "Boufarik"),
                UserEntity("usr_pro_karim", "PROFESSIONAL", "Karim M.", "+213 661 22 33 44", "karim.elec@wasta.dz", "Alger", "Bab El Oued"),
                UserEntity("usr_pro_mohamed", "PROFESSIONAL", "Mohamed Z.", "+213 550 44 55 66", "mohamed.clim@wasta.dz", "Oran", "Es Senia"),
                UserEntity("usr_pro_yacine", "PROFESSIONAL", "Yacine K.", "+213 771 66 77 88", "yacine.appliance@wasta.dz", "Constantine", "Ali Mendjeli"),
                UserEntity("usr_admin_1", "ADMIN", "Superviseur WASTA", "+213 21 00 00 00", "admin@wasta.dz", "Alger", "Hydra")
            )
            userDao.insertUsers(seedUsers)

            val seedPros = listOf(
                ProfessionalEntity(
                    id = "pro_ahmed_b",
                    userId = "usr_pro_ahmed",
                    name = "Ahmed B.",
                    phone = "+213 770 98 76 54",
                    specialties = "Plumbing, Water heaters, Boilers (ترصيص وصيانة السخانات)",
                    wilayasCovered = "Blida, Alger, Tipaza, Boumerdes",
                    baseWilaya = "Blida",
                    rating = 4.9f,
                    completedJobsCount = 234,
                    avgResponseMinutes = 7,
                    priceMinDA = 2000,
                    priceMaxDA = 8000,
                    identityVerified = true,
                    phoneVerified = true,
                    profileVerified = true,
                    isAvailable = true,
                    bio = "Certified technician with 12 years experience in domestic gas boilers, water heaters, and sanitary plumbing. Certified safety inspection.",
                    primaryCategory = "Plomberie & Chauffage",
                    serviceCategories = "Chauffe-eau & Chaudières, Plomberie Sanitaire, Recherche de Fuite & Gaz",
                    yearsExperience = 12,
                    punctualityRating = 4.9f,
                    craftsmanshipRating = 5.0f,
                    priceFairnessRating = 4.8f,
                    cleanlinessRating = 4.9f,
                    fiveStarsCount = 218,
                    fourStarsCount = 14,
                    threeStarsCount = 2,
                    insuranceVerified = true,
                    policeRecordVerified = true,
                    workshopAddress = "Boufarik Centre, Blida"
                ),
                ProfessionalEntity(
                    id = "pro_karim_m",
                    userId = "usr_pro_karim",
                    name = "Karim M.",
                    phone = "+213 661 22 33 44",
                    specialties = "Electrical engineering, Circuit breakers, Lighting (كهرباء عامة)",
                    wilayasCovered = "Alger, Blida, Boumerdes",
                    baseWilaya = "Alger",
                    rating = 4.95f,
                    completedJobsCount = 189,
                    avgResponseMinutes = 9,
                    priceMinDA = 2500,
                    priceMaxDA = 9000,
                    identityVerified = true,
                    phoneVerified = true,
                    profileVerified = true,
                    isAvailable = true,
                    bio = "State-certified electrical engineer. Diagnostic of differential tripping, panel renovation, and power balancing.",
                    primaryCategory = "Électricité Bâtiment",
                    serviceCategories = "Tableaux Électriques & Disjoncteurs, Prises & Câblage, Éclairage & Sécurité",
                    yearsExperience = 11,
                    punctualityRating = 4.95f,
                    craftsmanshipRating = 5.0f,
                    priceFairnessRating = 4.85f,
                    cleanlinessRating = 4.9f,
                    fiveStarsCount = 175,
                    fourStarsCount = 13,
                    threeStarsCount = 1,
                    insuranceVerified = true,
                    policeRecordVerified = true,
                    workshopAddress = "Bab El Oued, Alger Centre"
                ),
                ProfessionalEntity(
                    id = "pro_mohamed_z",
                    userId = "usr_pro_mohamed",
                    name = "Mohamed Z.",
                    phone = "+213 550 44 55 66",
                    specialties = "HVAC, Air conditioning, Cold rooms (صيانة المكيفات وتبريد)",
                    wilayasCovered = "Oran, Mostaganem, Sidi Bel Abbes",
                    baseWilaya = "Oran",
                    rating = 4.85f,
                    completedJobsCount = 142,
                    avgResponseMinutes = 12,
                    priceMinDA = 3000,
                    priceMaxDA = 9500,
                    identityVerified = true,
                    phoneVerified = true,
                    profileVerified = true,
                    isAvailable = true,
                    bio = "Specialist in R410A / R32 refrigerant gas recharges, leak detection, inverter compressor electronic boards.",
                    primaryCategory = "Climatisation & Froid",
                    serviceCategories = "Recharge Gaz R410A/R32, Entretien & Nettoyage Climatiseur, Dépannage Inverter",
                    yearsExperience = 9,
                    punctualityRating = 4.8f,
                    craftsmanshipRating = 4.9f,
                    priceFairnessRating = 4.85f,
                    cleanlinessRating = 4.8f,
                    fiveStarsCount = 125,
                    fourStarsCount = 15,
                    threeStarsCount = 2,
                    insuranceVerified = true,
                    policeRecordVerified = true,
                    workshopAddress = "Es Senia, Oran"
                ),
                ProfessionalEntity(
                    id = "pro_yacine_k",
                    userId = "usr_pro_yacine",
                    name = "Yacine K.",
                    phone = "+213 771 66 77 88",
                    specialties = "Washing machines, Dishwashers, Refrigerators (تصليح الأجهزة الكهرومنزلية)",
                    wilayasCovered = "Constantine, Mila, Oum El Bouaghi",
                    baseWilaya = "Constantine",
                    rating = 4.88f,
                    completedJobsCount = 116,
                    avgResponseMinutes = 15,
                    priceMinDA = 2000,
                    priceMaxDA = 7500,
                    identityVerified = true,
                    phoneVerified = true,
                    profileVerified = true,
                    isAvailable = true,
                    bio = "Certified home appliance technician. Genuine parts replacement, drum bearing changes, motor diagnostic.",
                    primaryCategory = "Électroménager",
                    serviceCategories = "Lave-linge & Vidange, Réfrigérateurs & Thermostat, Lave-vaisselle",
                    yearsExperience = 8,
                    punctualityRating = 4.85f,
                    craftsmanshipRating = 4.9f,
                    priceFairnessRating = 4.9f,
                    cleanlinessRating = 4.85f,
                    fiveStarsCount = 104,
                    fourStarsCount = 10,
                    threeStarsCount = 2,
                    insuranceVerified = true,
                    policeRecordVerified = true,
                    workshopAddress = "Ali Mendjeli, Constantine"
                )
            )
            proDao.insertProfessionals(seedPros)

            // Add sample verified reviews from previous clients
            val seedReviews = listOf(
                ReviewEntity(
                    id = "rev_seed_1",
                    jobId = "job_seed_completed_1",
                    customerId = "usr_cust_mehdi",
                    customerName = "Mehdi K.",
                    professionalId = "pro_ahmed_b",
                    rating = 5.0f,
                    comment = "Service impeccable. Ahmed est arrivé à l'heure à Blida, a diagnostiqué la fuite du chauffe-eau rapidement et a changé la membrane. Travail très propre et facture claire.",
                    verifiedJob = true,
                    createdAt = System.currentTimeMillis() - 86400000L * 2
                ),
                ReviewEntity(
                    id = "rev_seed_2",
                    jobId = "job_seed_completed_2",
                    customerId = "usr_cust_farid",
                    customerName = "Farid L.",
                    professionalId = "pro_ahmed_b",
                    rating = 5.0f,
                    comment = "Recherche et réparation de fuite sanitaire sans casser le carrelage inutilement. Artisan sérieux, respectueux et honnête sur le tarif des pièces.",
                    verifiedJob = true,
                    createdAt = System.currentTimeMillis() - 86400000L * 10
                ),
                ReviewEntity(
                    id = "rev_seed_3",
                    jobId = "job_seed_completed_3",
                    customerId = "usr_cust_amel",
                    customerName = "Amel B.",
                    professionalId = "pro_ahmed_b",
                    rating = 4.8f,
                    comment = "Installation d'un mitigeur thermostatique et détartrage de tuyauterie. Très bon artisan et bon conseil technique.",
                    verifiedJob = true,
                    createdAt = System.currentTimeMillis() - 86400000L * 25
                ),
                ReviewEntity(
                    id = "rev_seed_4",
                    jobId = "job_seed_completed_4",
                    customerId = "usr_cust_samir",
                    customerName = "Samir H.",
                    professionalId = "pro_karim_m",
                    rating = 5.0f,
                    comment = "Court-circuit résolu en 30 minutes à Bab El Oued. Diagnostic au multimètre précis et mise en sécurité du tableau avec disjoncteur différentiel.",
                    verifiedJob = true,
                    createdAt = System.currentTimeMillis() - 86400000L * 3
                ),
                ReviewEntity(
                    id = "rev_seed_5",
                    jobId = "job_seed_completed_5",
                    customerId = "usr_cust_nassima",
                    customerName = "Nassima T.",
                    professionalId = "pro_karim_m",
                    rating = 4.7f,
                    comment = "Installation de prises de terre et éclairage LED dans la cuisine. Artisan très ponctuel et courtois.",
                    verifiedJob = true,
                    createdAt = System.currentTimeMillis() - 86400000L * 14
                ),
                ReviewEntity(
                    id = "rev_seed_6",
                    jobId = "job_seed_completed_6",
                    customerId = "usr_cust_reda",
                    customerName = "Reda M.",
                    professionalId = "pro_mohamed_z",
                    rating = 5.0f,
                    comment = "Recharge de gaz R410A et nettoyage complet des filtres du climatiseur. Froid parfait et plus de bruit anormal !",
                    verifiedJob = true,
                    createdAt = System.currentTimeMillis() - 86400000L * 5
                ),
                ReviewEntity(
                    id = "rev_seed_7",
                    jobId = "job_seed_completed_7",
                    customerId = "usr_cust_leila",
                    customerName = "Leila D.",
                    professionalId = "pro_yacine_k",
                    rating = 4.9f,
                    comment = "Machine à laver qui ne vidangeait plus. Pompe de vidange changée avec pièce d'origine sous garantie. Efficace !",
                    verifiedJob = true,
                    createdAt = System.currentTimeMillis() - 86400000L * 7
                )
            )
            seedReviews.forEach { reviewDao.insertReview(it) }
        }
    }

    suspend fun createServiceRequest(
        customerId: String,
        customerName: String,
        customerPhone: String,
        problemDescription: String,
        wilaya: String,
        commune: String,
        exactAddress: String
    ): Pair<ServiceRequestEntity, AiTriageResult> = withContext(Dispatchers.IO) {
        val sanitizedDesc = SecurityAuditor.sanitizeInput(problemDescription, maxChars = 1000)
        val sanitizedName = SecurityAuditor.sanitizeInput(customerName, maxChars = 100)
        val sanitizedPhone = SecurityAuditor.sanitizePhoneNumber(customerPhone)
        val sanitizedAddress = SecurityAuditor.sanitizeInput(exactAddress, maxChars = 300)
        val sanitizedCommune = SecurityAuditor.sanitizeInput(commune, maxChars = 100)
        val sanitizedWilaya = SecurityAuditor.sanitizeInput(wilaya, maxChars = 100)

        val triage = AiTriageEngine.triageProblem(sanitizedDesc)
        val requestId = "req_" + UUID.randomUUID().toString().take(8)

        val request = ServiceRequestEntity(
            id = requestId,
            customerId = customerId,
            customerName = sanitizedName,
            customerPhone = sanitizedPhone,
            problemDescription = sanitizedDesc,
            category = triage.category,
            service = triage.service,
            specialtyNeeded = triage.specialtyNeeded,
            urgency = triage.urgency,
            wilaya = sanitizedWilaya,
            commune = sanitizedCommune,
            approximateArea = "$sanitizedCommune, $sanitizedWilaya",
            exactAddress = sanitizedAddress,
            status = "MATCHED"
        )
        requestDao.insertRequest(request)

        auditDao.insertLog(
            SecurityAuditor.createAuditLog(
                actorUserId = customerId,
                actorRole = "CUSTOMER",
                action = "CREATE_SERVICE_REQUEST",
                details = "Created request $requestId categorized as ${triage.service}"
            )
        )

        Pair(request, triage)
    }

    suspend fun acceptJobAtomically(
        requestId: String,
        professionalId: String,
        professionalName: String,
        scheduledDate: String,
        scheduledTimeSlot: String
    ): SecurityResult<JobEntity> = withContext(Dispatchers.IO) {
        val request = requestDao.getRequestById(requestId)
            ?: return@withContext SecurityResult.Denied("Request not found")

        // Concurrency guard: Only ONE technician can accept a job successfully!
        val lockResult = SecurityAuditor.attemptAtomicJobAcceptance(requestId, professionalId)
        if (lockResult is SecurityResult.Denied) {
            return@withContext lockResult
        }

        if (request.status != "MATCHED" && request.status != "PENDING") {
            return@withContext SecurityResult.Denied("Job is no longer open for acceptance (Current status: ${request.status}).")
        }

        val jobId = "job_" + UUID.randomUUID().toString().take(8)
        val job = JobEntity(
            id = jobId,
            requestId = requestId,
            customerId = request.customerId,
            customerName = request.customerName,
            customerPhone = request.customerPhone,
            professionalId = professionalId,
            professionalName = professionalName,
            status = "SCHEDULED",
            scheduledDate = scheduledDate,
            scheduledTimeSlot = scheduledTimeSlot,
            laborDA = 3000,
            materialsDA = 1500,
            totalDA = 4500
        )

        jobDao.insertJob(job)
        requestDao.updateRequest(request.copy(status = "ACCEPTED", assignedProfessionalId = professionalId))

        auditDao.insertLog(
            SecurityAuditor.createAuditLog(
                actorUserId = professionalId,
                actorRole = "PROFESSIONAL",
                action = "ACCEPT_JOB",
                details = "Job $jobId accepted for request $requestId by $professionalName"
            )
        )

        SecurityResult.Success(job)
    }

    suspend fun updateJobStatus(job: JobEntity, newStatus: String): Unit = withContext(Dispatchers.IO) {
        val updated = job.copy(status = newStatus)
        jobDao.updateJob(updated)
        auditDao.insertLog(
            SecurityAuditor.createAuditLog(
                actorUserId = job.professionalId,
                actorRole = "PROFESSIONAL",
                action = "UPDATE_JOB_STATUS",
                details = "Job ${job.id} updated to status $newStatus"
            )
        )
    }

    suspend fun uploadProofAndCompleteJob(
        jobId: String,
        technicianId: String,
        beforeDesc: String,
        afterDesc: String,
        laborDA: Int,
        materialsDA: Int
    ): SecurityResult<InvoiceEntity> = withContext(Dispatchers.IO) {
        val job = jobDao.getJobById(jobId)
            ?: return@withContext SecurityResult.Denied("Job not found")

        // Prerequisite validation
        val sanitizedBefore = SecurityAuditor.sanitizeInput(beforeDesc, maxChars = 1000)
        val sanitizedAfter = SecurityAuditor.sanitizeInput(afterDesc, maxChars = 1000)
        val hasProof = sanitizedBefore.isNotBlank() && sanitizedAfter.isNotBlank()
        val prereqCheck = SecurityAuditor.validateJobCompletionPrerequisites(job, hasProof)
        if (prereqCheck is SecurityResult.Denied) {
            return@withContext prereqCheck
        }

        val totalDA = laborDA + materialsDA
        val completedJob = job.copy(
            status = "COMPLETED",
            beforePhotoDesc = sanitizedBefore,
            afterPhotoDesc = sanitizedAfter,
            laborDA = laborDA,
            materialsDA = materialsDA,
            totalDA = totalDA,
            completedAt = System.currentTimeMillis()
        )
        jobDao.updateJob(completedJob)

        // Generate authoritative invoice
        val invoiceId = "inv_" + UUID.randomUUID().toString().take(8)
        val invoice = InvoiceEntity(
            id = invoiceId,
            jobId = jobId,
            professionalId = technicianId,
            professionalName = job.professionalName,
            customerId = job.customerId,
            serviceTitle = "Home Service Intervention #${job.id}",
            laborDA = laborDA,
            materialsDA = materialsDA,
            totalDA = totalDA,
            status = "PENDING",
            idempotencyKey = UUID.randomUUID().toString()
        )
        invoiceDao.insertInvoice(invoice)

        auditDao.insertLog(
            SecurityAuditor.createAuditLog(
                actorUserId = technicianId,
                actorRole = "PROFESSIONAL",
                action = "COMPLETE_JOB_AND_GENERATE_INVOICE",
                details = "Job $jobId completed with proof. Generated invoice $invoiceId for $totalDA DA."
            )
        )

        SecurityResult.Success(invoice)
    }

    suspend fun processInvoicePayment(
        invoiceId: String,
        customerId: String,
        clientSubmittedAmountDA: Int,
        paymentMethod: String
    ): SecurityResult<InvoiceEntity> = withContext(Dispatchers.IO) {
        val invoice = invoiceDao.getInvoiceById(invoiceId)
            ?: return@withContext SecurityResult.Denied("Invoice not found")

        // Security Scenario 4: Ensure client didn't manipulate price
        val tamperingCheck = SecurityAuditor.validateInvoicePayment(invoice, clientSubmittedAmountDA)
        if (tamperingCheck is SecurityResult.Denied) {
            auditDao.insertLog(
                SecurityAuditor.createAuditLog(
                    actorUserId = customerId,
                    actorRole = "CUSTOMER",
                    action = "PAYMENT_TAMPER_ALERT",
                    details = "Attempted to pay $clientSubmittedAmountDA DA instead of ${invoice.totalDA} DA",
                    securityLevel = "SECURITY_ALERT"
                )
            )
            return@withContext tamperingCheck
        }

        val paidInvoice = invoice.copy(
            status = "PAID",
            paymentMethod = paymentMethod,
            paidAt = System.currentTimeMillis()
        )
        invoiceDao.updateInvoice(paidInvoice)

        // Update professional balance
        val pro = proDao.getProfessionalById(invoice.professionalId)
        if (pro != null) {
            proDao.updateProfessional(
                pro.copy(
                    completedJobsCount = pro.completedJobsCount + 1,
                    totalEarningsDA = pro.totalEarningsDA + invoice.totalDA
                )
            )
        }

        auditDao.insertLog(
            SecurityAuditor.createAuditLog(
                actorUserId = customerId,
                actorRole = "CUSTOMER",
                action = "INVOICE_PAID",
                details = "Invoice $invoiceId paid: ${invoice.totalDA} DA via $paymentMethod"
            )
        )

        SecurityResult.Success(paidInvoice)
    }

    suspend fun submitVerifiedReview(
        jobId: String,
        customerId: String,
        customerName: String,
        rating: Float,
        comment: String
    ): SecurityResult<ReviewEntity> = withContext(Dispatchers.IO) {
        val job = jobDao.getJobById(jobId)
            ?: return@withContext SecurityResult.Denied("Job not found")

        val invoice = invoiceDao.getInvoiceByJobId(jobId)

        // Scenario 5: Review eligibility check (must be completed + paid)
        val eligibility = SecurityAuditor.validateReviewEligibility(job, invoice, customerId)
        if (eligibility is SecurityResult.Denied) {
            return@withContext eligibility
        }

        // Prevent duplicate review for same job
        val existingReview = reviewDao.getReviewByJobId(jobId)
        if (existingReview != null) {
            return@withContext SecurityResult.Denied("A verified review has already been submitted for this job.")
        }

        val sanitizedComment = SecurityAuditor.sanitizeInput(comment, maxChars = 1000)
        val sanitizedCustomerName = SecurityAuditor.sanitizeInput(customerName, maxChars = 100)

        val review = ReviewEntity(
            id = "rev_" + UUID.randomUUID().toString().take(8),
            jobId = jobId,
            customerId = customerId,
            customerName = sanitizedCustomerName,
            professionalId = job.professionalId,
            rating = rating.coerceIn(1f, 5f),
            comment = sanitizedComment,
            verifiedJob = true
        )
        reviewDao.insertReview(review)

        // Recalculate professional rating
        val pro = proDao.getProfessionalById(job.professionalId)
        if (pro != null) {
            val newRating = ((pro.rating * pro.completedJobsCount) + rating) / (pro.completedJobsCount + 1)
            proDao.updateProfessional(pro.copy(rating = (Math.round(newRating * 10.0) / 10.0).toFloat()))
        }

        auditDao.insertLog(
            SecurityAuditor.createAuditLog(
                actorUserId = customerId,
                actorRole = "CUSTOMER",
                action = "SUBMIT_VERIFIED_REVIEW",
                details = "Submitted verified review for job $jobId, rating $rating"
            )
        )

        SecurityResult.Success(review)
    }

    suspend fun openDispute(
        jobId: String,
        openedByUserId: String,
        openedByRole: String,
        reason: String
    ): DisputeEntity = withContext(Dispatchers.IO) {
        val sanitizedReason = SecurityAuditor.sanitizeInput(reason, maxChars = 1000)
        val dispute = DisputeEntity(
            id = "disp_" + UUID.randomUUID().toString().take(8),
            jobId = jobId,
            openedByUserId = openedByUserId,
            openedByRole = openedByRole,
            reason = sanitizedReason,
            status = "OPEN"
        )
        disputeDao.insertDispute(dispute)

        val job = jobDao.getJobById(jobId)
        if (job != null) {
            jobDao.updateJob(job.copy(status = "DISPUTED"))
        }

        auditDao.insertLog(
            SecurityAuditor.createAuditLog(
                actorUserId = openedByUserId,
                actorRole = openedByRole,
                action = "OPEN_DISPUTE",
                details = "Dispute opened for job $jobId: $sanitizedReason",
                securityLevel = "WARNING"
            )
        )

        dispute
    }

    suspend fun addDirectClientReview(
        professionalId: String,
        customerId: String,
        customerName: String,
        rating: Float,
        comment: String
    ): ReviewEntity = withContext(Dispatchers.IO) {
        val sanitizedComment = SecurityAuditor.sanitizeInput(comment, maxChars = 1000)
        val sanitizedCustomerName = SecurityAuditor.sanitizeInput(customerName, maxChars = 100)
        val review = ReviewEntity(
            id = "rev_" + UUID.randomUUID().toString().take(8),
            jobId = "job_client_" + System.currentTimeMillis().toString().takeLast(6),
            customerId = customerId,
            customerName = sanitizedCustomerName,
            professionalId = professionalId,
            rating = rating.coerceIn(1f, 5f),
            comment = sanitizedComment,
            verifiedJob = true,
            createdAt = System.currentTimeMillis()
        )
        reviewDao.insertReview(review)

        val pro = proDao.getProfessionalById(professionalId)
        if (pro != null) {
            val newRating = ((pro.rating * pro.completedJobsCount) + rating) / (pro.completedJobsCount + 1)
            proDao.updateProfessional(pro.copy(
                rating = (Math.round(newRating * 10.0) / 10.0).toFloat(),
                completedJobsCount = pro.completedJobsCount + 1
            ))
        }

        auditDao.insertLog(
            SecurityAuditor.createAuditLog(
                actorUserId = customerId,
                actorRole = "CUSTOMER",
                action = "SUBMIT_DIRECT_REVIEW",
                details = "Submitted client review for pro $professionalId, rating $rating"
            )
        )

        review
    }

    suspend fun adminResolveDispute(disputeId: String, notes: String) = withContext(Dispatchers.IO) {
        val sanitizedNotes = SecurityAuditor.sanitizeInput(notes, maxChars = 500)
        val dispute = disputeDao.getDisputeById(disputeId)
        if (dispute != null) {
            disputeDao.updateDispute(dispute.copy(status = "RESOLVED", resolutionNotes = sanitizedNotes))
        }
        auditDao.insertLog(
            SecurityAuditor.createAuditLog(
                actorUserId = "usr_admin_1",
                actorRole = "ADMIN",
                action = "RESOLVE_DISPUTE",
                details = "Resolved dispute $disputeId: $sanitizedNotes"
            )
        )
    }
}
