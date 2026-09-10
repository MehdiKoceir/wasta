package com.example.matching

import com.example.data.model.ProfessionalEntity

data class MatchScoreBreakdown(
    val professional: ProfessionalEntity,
    val totalScorePercent: Int,
    val specialtyScore: Int, // max 30
    val geographicScore: Int, // max 20
    val availabilityScore: Int, // max 15
    val ratingScore: Int, // max 15
    val experienceScore: Int, // max 10
    val responseTimeScore: Int, // max 5
    val reliabilityScore: Int, // max 5
    val matchReasons: List<String>
)

object SmartMatchingEngine {

    fun calculateMatch(
        pro: ProfessionalEntity,
        specialtyNeeded: String,
        customerWilaya: String,
        customerCommune: String = ""
    ): MatchScoreBreakdown {
        var specialtyPts = 0
        var geoPts = 0
        var availPts = 0
        var ratingPts = 0
        var expPts = 0
        var respPts = 0
        var relPts = 0
        val reasons = mutableListOf<String>()

        // 1. Specialty matching (30 pts max)
        val proSpecialtiesLower = pro.specialties.lowercase()
        val neededLower = specialtyNeeded.lowercase()
        val keywords = neededLower.split(" ", "/", ",", "-").filter { it.length > 2 }
        var matchesKeyword = false
        for (kw in keywords) {
            if (proSpecialtiesLower.contains(kw)) {
                matchesKeyword = true
                break
            }
        }
        if (matchesKeyword || proSpecialtiesLower.contains(neededLower)) {
            specialtyPts = 30
            reasons.add("Exact specialty alignment: ${pro.specialties}")
        } else {
            specialtyPts = 12
            reasons.add("General technical capability")
        }

        // 2. Geographic distance & coverage (20 pts max)
        val coveredLower = pro.wilayasCovered.lowercase()
        val custWilayaLower = customerWilaya.lowercase()
        if (pro.baseWilaya.equals(customerWilaya, ignoreCase = true)) {
            geoPts = 20
            reasons.add("Based locally in $customerWilaya (instant dispatch)")
        } else if (coveredLower.contains(custWilayaLower)) {
            geoPts = 16
            reasons.add("Servicing your wilaya: $customerWilaya")
        } else {
            geoPts = 5
            reasons.add("Regional coverage upon request")
        }

        // 3. Availability (15 pts max)
        if (pro.isAvailable) {
            availPts = 15
            reasons.add("Available for immediate dispatch / booking")
        } else {
            availPts = 5
            reasons.add("Next available slot tomorrow")
        }

        // 4. Rating (15 pts max)
        // 5.0 -> 15 pts, 4.5 -> 13.5 pts
        ratingPts = ((pro.rating.coerceIn(0f, 5f) / 5.0f) * 15f).toInt()
        if (pro.rating >= 4.8f) {
            reasons.add("Exceptional verified rating (${pro.rating}★)")
        }

        // 5. Experience / Completed jobs (10 pts max)
        expPts = when {
            pro.completedJobsCount >= 200 -> 10
            pro.completedJobsCount >= 100 -> 8
            pro.completedJobsCount >= 50 -> 6
            else -> 4
        }
        reasons.add("${pro.completedJobsCount}+ completed jobs with verified proof")

        // 6. Response time (5 pts max)
        respPts = when {
            pro.avgResponseMinutes <= 10 -> 5
            pro.avgResponseMinutes <= 20 -> 4
            else -> 3
        }

        // 7. Reliability / Completion rate (5 pts max)
        relPts = when {
            pro.completionRatePercent >= 98f && pro.cancellationRatePercent <= 2f -> 5
            pro.completionRatePercent >= 95f -> 4
            else -> 3
        }

        val total = (specialtyPts + geoPts + availPts + ratingPts + expPts + respPts + relPts).coerceIn(0, 100)

        return MatchScoreBreakdown(
            professional = pro,
            totalScorePercent = total,
            specialtyScore = specialtyPts,
            geographicScore = geoPts,
            availabilityScore = availPts,
            ratingScore = ratingPts,
            experienceScore = expPts,
            responseTimeScore = respPts,
            reliabilityScore = relPts,
            matchReasons = reasons
        )
    }

    fun rankProfessionals(
        professionals: List<ProfessionalEntity>,
        specialtyNeeded: String,
        customerWilaya: String
    ): List<MatchScoreBreakdown> {
        return professionals
            .map { calculateMatch(it, specialtyNeeded, customerWilaya) }
            .sortedByDescending { it.totalScorePercent }
    }
}
