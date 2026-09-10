package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AuditLogDao
import com.example.data.dao.DisputeDao
import com.example.data.dao.InvoiceDao
import com.example.data.dao.JobDao
import com.example.data.dao.ProfessionalDao
import com.example.data.dao.ReviewDao
import com.example.data.dao.ServiceRequestDao
import com.example.data.dao.UserDao
import com.example.data.model.AuditLogEntity
import com.example.data.model.DisputeEntity
import com.example.data.model.InvoiceEntity
import com.example.data.model.JobEntity
import com.example.data.model.ProfessionalEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.ServiceRequestEntity
import com.example.data.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ProfessionalEntity::class,
        ServiceRequestEntity::class,
        JobEntity::class,
        InvoiceEntity::class,
        ReviewEntity::class,
        DisputeEntity::class,
        AuditLogEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class WastaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun professionalDao(): ProfessionalDao
    abstract fun serviceRequestDao(): ServiceRequestDao
    abstract fun jobDao(): JobDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun reviewDao(): ReviewDao
    abstract fun disputeDao(): DisputeDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: WastaDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Compatible schema migration from v1 to v2
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // High-performance indices for foreign keys and frequent query filters
                db.execSQL("CREATE INDEX IF NOT EXISTS index_users_role ON users(role)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_users_wilaya ON users(wilaya)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_professionals_userId ON professionals(userId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_professionals_baseWilaya ON professionals(baseWilaya)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_professionals_isAvailable ON professionals(isAvailable)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_service_requests_customerId ON service_requests(customerId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_service_requests_assignedProfessionalId ON service_requests(assignedProfessionalId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_service_requests_status ON service_requests(status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_jobs_requestId ON jobs(requestId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_jobs_customerId ON jobs(customerId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_jobs_professionalId ON jobs(professionalId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_jobs_status ON jobs(status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_invoices_jobId ON invoices(jobId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_invoices_professionalId ON invoices(professionalId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_invoices_customerId ON invoices(customerId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_invoices_status ON invoices(status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_reviews_jobId ON reviews(jobId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_reviews_professionalId ON reviews(professionalId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_reviews_customerId ON reviews(customerId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_disputes_jobId ON disputes(jobId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_disputes_openedByUserId ON disputes(openedByUserId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_disputes_status ON disputes(status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_audit_logs_actorUserId ON audit_logs(actorUserId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_audit_logs_timestamp ON audit_logs(timestamp)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_audit_logs_securityLevel ON audit_logs(securityLevel)")
            }
        }

        fun getDatabase(context: Context): WastaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WastaDatabase::class.java,
                    "wasta_production_dz.db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
