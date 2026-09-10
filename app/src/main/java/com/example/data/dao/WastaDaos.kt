package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AuditLogEntity
import com.example.data.model.DisputeEntity
import com.example.data.model.InvoiceEntity
import com.example.data.model.JobEntity
import com.example.data.model.ProfessionalEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.ServiceRequestEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
}

@Dao
interface ProfessionalDao {
    @Query("SELECT * FROM professionals ORDER BY rating DESC, completedJobsCount DESC")
    fun getAllProfessionals(): Flow<List<ProfessionalEntity>>

    @Query("SELECT * FROM professionals WHERE id = :id")
    suspend fun getProfessionalById(id: String): ProfessionalEntity?

    @Query("SELECT * FROM professionals WHERE userId = :userId")
    suspend fun getProfessionalByUserId(userId: String): ProfessionalEntity?

    @Query("SELECT * FROM professionals WHERE isAvailable = 1")
    fun getAvailableProfessionals(): Flow<List<ProfessionalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfessional(pro: ProfessionalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfessionals(pros: List<ProfessionalEntity>)

    @Update
    suspend fun updateProfessional(pro: ProfessionalEntity)
}

@Dao
interface ServiceRequestDao {
    @Query("SELECT * FROM service_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<ServiceRequestEntity>>

    @Query("SELECT * FROM service_requests WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getRequestsByCustomer(customerId: String): Flow<List<ServiceRequestEntity>>

    @Query("SELECT * FROM service_requests WHERE id = :id")
    suspend fun getRequestById(id: String): ServiceRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: ServiceRequestEntity)

    @Update
    suspend fun updateRequest(request: ServiceRequestEntity)
}

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY acceptedAt DESC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE customerId = :customerId ORDER BY acceptedAt DESC")
    fun getJobsForCustomer(customerId: String): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE professionalId = :proId ORDER BY acceptedAt DESC")
    fun getJobsForProfessional(proId: String): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id")
    suspend fun getJobById(id: String): JobEntity?

    @Query("SELECT * FROM jobs WHERE requestId = :requestId LIMIT 1")
    suspend fun getJobByRequestId(requestId: String): JobEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity)

    @Update
    suspend fun updateJob(job: JobEntity)
}

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY createdAt DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE jobId = :jobId LIMIT 1")
    suspend fun getInvoiceByJobId(jobId: String): InvoiceEntity?

    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getInvoiceById(id: String): InvoiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity)

    @Update
    suspend fun updateInvoice(invoice: InvoiceEntity)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE professionalId = :proId ORDER BY createdAt DESC")
    fun getReviewsForProfessional(proId: String): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE jobId = :jobId LIMIT 1")
    suspend fun getReviewByJobId(jobId: String): ReviewEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)
}

@Dao
interface DisputeDao {
    @Query("SELECT * FROM disputes ORDER BY createdAt DESC")
    fun getAllDisputes(): Flow<List<DisputeEntity>>

    @Query("SELECT * FROM disputes WHERE id = :id LIMIT 1")
    suspend fun getDisputeById(id: String): DisputeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispute(dispute: DisputeEntity)

    @Update
    suspend fun updateDispute(dispute: DisputeEntity)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)
}
