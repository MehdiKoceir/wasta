package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.AiTriageEngine
import com.example.matching.SmartMatchingEngine
import com.example.data.model.ProfessionalEntity
import com.example.security.SecurityAuditor
import com.example.security.SecurityResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("WASTA?", appName)
  }

  @Test
  fun `ai triage detects water heater leak in French`() {
    val result = AiTriageEngine.triageProblem("Mon chauffe-eau fuit par le dessous")
    assertEquals("Water Heater & Boiler Repair (سخان الماء)", result.service)
    assertEquals("Water Heater Technician / Plombier", result.specialtyNeeded)
  }

  @Test
  fun `ai triage detects arabic or darija problem`() {
    val result = AiTriageEngine.triageProblem("سخان الماء تاعي راه يقطر من تحت")
    assertEquals("Water Heater & Boiler Repair (سخان الماء)", result.service)
  }

  @Test
  fun `security auditor prevents price tampering`() {
    val invoice = com.example.data.model.InvoiceEntity(
      id = "inv_test",
      jobId = "job_test",
      professionalId = "pro_test",
      professionalName = "Ahmed",
      customerId = "cust_test",
      serviceTitle = "Repair",
      laborDA = 3000,
      materialsDA = 1500,
      totalDA = 4500,
      status = "PENDING",
      idempotencyKey = "key_1"
    )
    val check = SecurityAuditor.validateInvoicePayment(invoice, clientSubmittedAmountDA = 100)
    assertTrue(check is SecurityResult.Denied)
  }

  @Test
  fun `verified profile data model factory loads official certifications and service categories`() {
    val pro = ProfessionalEntity(
      id = "pro_ahmed_b",
      userId = "usr_pro_ahmed",
      name = "Ahmed B.",
      phone = "+213 770 98 76 54",
      specialties = "Plumbing, Water heaters",
      wilayasCovered = "Blida, Alger",
      baseWilaya = "Blida",
      rating = 4.9f,
      completedJobsCount = 234,
      avgResponseMinutes = 7,
      priceMinDA = 2000,
      priceMaxDA = 8000,
      identityVerified = true,
      phoneVerified = true,
      profileVerified = true
    )

    val profile = com.example.data.model.VerifiedProfileFactory.createProfile(pro)

    assertEquals("Ahmed B.", profile.pro.name)
    assertTrue(profile.certifications.isNotEmpty())
    assertTrue(profile.serviceCategories.isNotEmpty())
    assertTrue(profile.certifications.any { it.issuer.contains("CAM") || it.issuer.contains("Artisanat") })
    assertEquals(4.9f, profile.ratingBreakdown.overallRating)
    assertTrue(profile.ratingBreakdown.recommendationRatePercent >= 95)
    assertTrue(profile.serviceCategories.first().services.isNotEmpty())
  }

  @Test
  fun `ratings and certifications metrics contain quality pillars and registration credentials`() {
    val pro = ProfessionalEntity(
      id = "pro_karim_m",
      userId = "usr_pro_karim",
      name = "Karim M.",
      phone = "+213 555 12 34 56",
      specialties = "Electricity, Home Automation",
      wilayasCovered = "Alger, Tipaza",
      baseWilaya = "Alger",
      rating = 4.85f,
      completedJobsCount = 189,
      avgResponseMinutes = 12,
      priceMinDA = 1500,
      priceMaxDA = 9000,
      identityVerified = true,
      phoneVerified = true,
      profileVerified = true
    )

    val profile = com.example.data.model.VerifiedProfileFactory.createProfile(pro)
    val breakdown = profile.ratingBreakdown

    // Quality performance pillars validation
    assertTrue(breakdown.punctualityScore in 1.0f..5.0f)
    assertTrue(breakdown.craftsmanshipScore in 1.0f..5.0f)
    assertTrue(breakdown.cleanlinessScore in 1.0f..5.0f)
    assertTrue(breakdown.pricingTransparencyScore in 1.0f..5.0f)

    // Star distributions sum must equal total reviews
    val totalStars = breakdown.fiveStarsCount + breakdown.fourStarsCount + breakdown.threeStarsCount + breakdown.twoStarsCount + breakdown.oneStarsCount
    assertEquals(breakdown.totalReviewsCount, totalStars)

    // Certifications must have non-blank credential numbers
    profile.certifications.forEach { cert ->
      assertTrue(cert.credentialNumber.isNotBlank())
      assertTrue(cert.issuer.isNotBlank())
    }
  }

  @Test
  fun `firebase manager initializes safely without crashing`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    com.example.data.firebase.FirebaseManager.initialize(context)
    // Should initialize or fallback cleanly without exception
    assertTrue(true)
  }

  @Test
  fun `wasta auth state preserves authentication status and credentials`() {
    val defaultState = com.example.data.firebase.WastaAuthState()
    assertEquals(false, defaultState.isAuthenticated)
    assertEquals("GUEST", defaultState.provider)

    val signedInState = com.example.data.firebase.WastaAuthState(
      isAuthenticated = true,
      uid = "usr_firebase_12345",
      displayName = "Mehdi Koceir",
      email = "mehdi@wasta.dz",
      provider = "GOOGLE"
    )
    assertEquals(true, signedInState.isAuthenticated)
    assertEquals("Mehdi Koceir", signedInState.displayName)
    assertEquals("GOOGLE", signedInState.provider)
  }

  @Test
  fun `verified professional entity contains verified profile badge data and service category`() {
    val pro = ProfessionalEntity(
      id = "pro_ahmed_b",
      userId = "usr_pro_ahmed",
      name = "Ahmed B.",
      phone = "+213 770 98 76 54",
      specialties = "Chauffe-eau & Chaudières, Plomberie Sanitaire",
      wilayasCovered = "Blida, Alger",
      baseWilaya = "Blida",
      rating = 4.9f,
      completedJobsCount = 234,
      avgResponseMinutes = 7,
      priceMinDA = 2000,
      priceMaxDA = 8000,
      identityVerified = true,
      phoneVerified = true,
      profileVerified = true,
      primaryCategory = "Plomberie & Chauffage"
    )

    // Verify required fields for verified pro card
    assertEquals("Ahmed B.", pro.name)
    assertEquals("Plomberie & Chauffage", pro.primaryCategory)
    assertTrue(pro.profileVerified)
    assertTrue(pro.identityVerified)
    assertTrue(pro.rating >= 4.5f)
    assertTrue(pro.priceMinDA > 0)
    assertTrue(pro.priceMaxDA > pro.priceMinDA)
  }
}

