package com.example.ai

data class AiTriageResult(
    val category: String,
    val service: String,
    val specialtyNeeded: String,
    val detectedProblems: List<String>,
    val urgency: String, // "low", "medium", "high", "emergency"
    val estimatedPriceMinDA: Int,
    val estimatedPriceMaxDA: Int,
    val safetyAdvice: String,
    val triageDisclaimer: String = "Possible issue based on symptom classification. The certified professional makes the definitive technical diagnosis on-site.",
    val languageDetected: String = "Mixed/Multilingual"
)

object AiTriageEngine {

    /**
     * Sanitizes and bounds customer input against prompt injection and oversized payloads.
     */
    fun sanitizeInput(rawText: String): String {
        val trimmed = rawText.trim()
        val truncated = if (trimmed.length > 800) trimmed.substring(0, 800) else trimmed
        // Strip system prompt injection markers and control characters
        return truncated
            .replace(Regex("<system>.*?</system>", RegexOption.IGNORE_CASE), "")
            .replace(Regex("ignore previous instructions", RegexOption.IGNORE_CASE), "")
            .replace(Regex("role: *admin", RegexOption.IGNORE_CASE), "")
    }

    /**
     * Comprehensive trilingual AI triage supporting Algerian Darija, Arabic, French, and English.
     */
    fun triageProblem(rawProblemText: String): AiTriageResult {
        val clean = sanitizeInput(rawProblemText).lowercase()

        // 1. Water heater / Chauffe-eau / سخان الماء / Plomberie
        if (clean.contains("chauffe") || clean.contains("سخان") || clean.contains("fuit") ||
            clean.contains("fuyant") || clean.contains("fuite") || clean.contains("يقطر") ||
            clean.contains("chaufou") || clean.contains("plombier") || clean.contains("plomberie") ||
            clean.contains("tuyau") || clean.contains("robinet") || clean.contains("ماء") ||
            clean.contains("gaz chauffe") || clean.contains("thermostat") || clean.contains("boiler")
        ) {
            val problems = mutableListOf<String>()
            var urgency = "medium"
            if (clean.contains("fuit") || clean.contains("يقطر") || clean.contains("leak")) {
                problems.add("Water leakage / تسرب المياه")
            }
            if (clean.contains("gaz") || clean.contains("غاز") || clean.contains("odeur") || clean.contains("ريح")) {
                problems.add("Suspected Gas / Burner issue")
                urgency = "emergency"
            }
            if (clean.contains("allume") || clean.contains("ma ykhdemch") || clean.contains("ما يشعلش") || clean.contains("eteint")) {
                problems.add("Ignition / Thermocouple failure")
            }
            if (problems.isEmpty()) problems.add("Heating regulation malfunction")

            return AiTriageResult(
                category = "Home Services & Plumbing",
                service = "Water Heater & Boiler Repair (سخان الماء)",
                specialtyNeeded = "Water Heater Technician / Plombier",
                detectedProblems = problems,
                urgency = urgency,
                estimatedPriceMinDA = 2000,
                estimatedPriceMaxDA = 7500,
                safetyAdvice = if (urgency == "emergency") "⚠️ Close the gas valve and open windows immediately if you smell gas." else "Turn off the cold water supply valve to the water heater to minimize water damage.",
                languageDetected = if (clean.contains("سخان") || clean.contains("يقطر")) "العربية / الدارجة الجزائريّة" else "Français / Darija"
            )
        }

        // 2. Washing machine & Appliances / Machine à laver / غسالة / ثلاجة
        if (clean.contains("machine") || clean.contains("laver") || clean.contains("غسالة") ||
            clean.contains("lave-linge") || clean.contains("frigo") || clean.contains("refrig") ||
            clean.contains("ثلاجة") || clean.contains("bruit") || clean.contains("صوت") ||
            clean.contains("essorage") || clean.contains("drain") || clean.contains("ne vide") ||
            clean.contains("ma t3serch") || clean.contains("ma tfarach")
        ) {
            val problems = mutableListOf<String>()
            if (clean.contains("bruit") || clean.contains("صوت") || clean.contains("noise")) {
                problems.add("Unusual mechanical friction or bearing noise")
            }
            if (clean.contains("vide") || clean.contains("drain") || clean.contains("ماء") || clean.contains("t3ser")) {
                problems.add("Drainage pump blockage or discharge failure")
            }
            if (clean.contains("demarre") || clean.contains("ما تمشيش") || clean.contains("marche pas")) {
                problems.add("Electronic control board / Motor start issue")
            }
            if (problems.isEmpty()) problems.add("General appliance diagnostic")

            return AiTriageResult(
                category = "Appliance Repair",
                service = "Washing Machine & Appliance Diagnostic (تصليح الغسالات)",
                specialtyNeeded = "Home Appliance Repair Specialist",
                detectedProblems = problems,
                urgency = "medium",
                estimatedPriceMinDA = 2500,
                estimatedPriceMaxDA = 8500,
                safetyAdvice = "Unplug the appliance from electricity and do not force the drum door open if water is inside.",
                languageDetected = if (clean.contains("غسالة") || clean.contains("ثلاجة")) "العربية" else "Français / Algérien"
            )
        }

        // 3. Electrical / Electricien / كهرباء / كونتور / ديجونكتور
        if (clean.contains("electr") || clean.contains("كهربا") || clean.contains("prise") ||
            clean.contains("disjoncteur") || clean.contains("court-circuit") || clean.contains("ضارب") ||
            clean.contains("tableau") || clean.contains("compteur") || clean.contains("ضو") ||
            clean.contains("short circuit") || clean.contains("installation") || clean.contains("كابل")
        ) {
            val urgency = if (clean.contains("court-circuit") || clean.contains("etincelle") || clean.contains("شرارة") || clean.contains("smoke")) "emergency" else "medium"
            return AiTriageResult(
                category = "Electrical Engineering",
                service = "Electrical Installation & Fault Troubleshooting (كهرباء عامة)",
                specialtyNeeded = "Certified Electrician (كهربائي معتمد)",
                detectedProblems = listOf(
                    if (urgency == "emergency") "Severe short circuit / Electrical arcing hazard" else "Circuit load balancing or socket installation"
                ),
                urgency = urgency,
                estimatedPriceMinDA = 2000,
                estimatedPriceMaxDA = 9000,
                safetyAdvice = "Switch off the main differential breaker (Disjoncteur général) immediately.",
                languageDetected = if (clean.contains("كهربا") || clean.contains("ضو")) "العربية" else "Français"
            )
        }

        // 4. HVAC & Air Conditioning / Climatiseur / تبريد / مكيف
        if (clean.contains("clim") || clean.contains("مكيف") || clean.contains("تبريد") ||
            clean.contains("cooling") || clean.contains("gaz r410") || clean.contains("gaz r22") ||
            clean.contains("ne refroidit") || clean.contains("ma yberredch") || clean.contains("chaud")
        ) {
            return AiTriageResult(
                category = "HVAC & Air Conditioning",
                service = "Air Conditioning Maintenance & Gas Refill (صيانة المكيفات)",
                specialtyNeeded = "HVAC & Climatisation Technician",
                detectedProblems = listOf("Refrigerant gas level depletion or filter coil obstruction"),
                urgency = "medium",
                estimatedPriceMinDA = 3000,
                estimatedPriceMaxDA = 9500,
                safetyAdvice = "Turn off the AC compressor unit to prevent motor burnout until technical diagnostic.",
                languageDetected = if (clean.contains("مكيف") || clean.contains("تبريد")) "العربية" else "Français / Darija"
            )
        }

        // 5. Locksmith & Access / Serrurerie / قفل / باب / مفتاح
        if (clean.contains("serrur") || clean.contains("قفل") || clean.contains("مفتاح") ||
            clean.contains("porte") || clean.contains("cle") || clean.contains("clé") ||
            clean.contains("bloque") || clean.contains("bab") || clean.contains("verrou")
        ) {
            return AiTriageResult(
                category = "Locksmith & Security",
                service = "Emergency Door Opening & Lock Replacement (فتح الأقفال وتغيير الكالون)",
                specialtyNeeded = "Professional Locksmith (صانع الأقفال)",
                detectedProblems = listOf("Cylinder jam or deadbolt misalignment"),
                urgency = "high",
                estimatedPriceMinDA = 2500,
                estimatedPriceMaxDA = 6500,
                safetyAdvice = "Do not force broken keys deeper into the barrel cylinder.",
                languageDetected = if (clean.contains("قفل") || clean.contains("مفتاح")) "العربية" else "Français"
            )
        }

        // 6. Default generic high-care triage
        return AiTriageResult(
            category = "General Home Maintenance",
            service = "Multi-Trade Technical Diagnostic & Repair",
            specialtyNeeded = "Verified General Maintenance Professional",
            detectedProblems = listOf("On-site symptom evaluation required"),
            urgency = "medium",
            estimatedPriceMinDA = 2000,
            estimatedPriceMaxDA = 7000,
            safetyAdvice = "Keep the working area clear and maintain safe distance from potentially compromised fixtures.",
            languageDetected = "Auto-detected"
        )
    }
}
