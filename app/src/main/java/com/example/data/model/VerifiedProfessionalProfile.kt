package com.example.data.model

import androidx.compose.runtime.Immutable

enum class CertificationStatus {
    VERIFIED,
    OFFICIALLY_ACCREDITED,
    PENDING_RENEWAL
}

@Immutable
data class ProfessionalCertification(
    val id: String,
    val title: String,
    val issuer: String,
    val issueYear: Int,
    val credentialNumber: String,
    val status: CertificationStatus = CertificationStatus.VERIFIED,
    val badgeLabel: String,
    val description: String,
    val isGovernmentAccredited: Boolean = true,
    val expirationDate: String? = null
)

@Immutable
data class ServiceItem(
    val id: String,
    val title: String,
    val description: String,
    val startingPriceDA: Int,
    val pricingModel: String = "Forfait", // "Forfait", "Diagnostic", "Sur Devis"
    val estimatedDuration: String = "1h - 2h",
    val warrantyDays: Int = 30,
    val isPopular: Boolean = false
)

@Immutable
data class ServiceCategoryItem(
    val id: String,
    val name: String,
    val iconType: String, // "plumbing", "heating", "leak", "maintenance"
    val description: String,
    val servicesCount: Int,
    val priceStartingDA: Int,
    val services: List<ServiceItem>
)

@Immutable
data class RatingBreakdown(
    val overallRating: Float,
    val totalReviewsCount: Int,
    val recommendationRatePercent: Int,
    val punctualityScore: Float,
    val craftsmanshipScore: Float,
    val cleanlinessScore: Float,
    val pricingTransparencyScore: Float,
    val fiveStarsCount: Int,
    val fourStarsCount: Int,
    val threeStarsCount: Int,
    val twoStarsCount: Int,
    val oneStarsCount: Int
)

@Immutable
data class VerifiedProfessionalProfile(
    val pro: ProfessionalEntity,
    val headline: String,
    val primaryCategory: String,
    val secondaryCategories: List<String>,
    val certifications: List<ProfessionalCertification>,
    val serviceCategories: List<ServiceCategoryItem>,
    val ratingBreakdown: RatingBreakdown,
    val yearsExperience: Int,
    val workshopAddress: String,
    val guarantees: List<String>,
    val languagesSpoken: List<String>,
    val emergencyAvailable: Boolean = true,
    val responseTimeMinutes: Int = 7
)

object VerifiedProfileFactory {

    fun createProfile(pro: ProfessionalEntity, reviews: List<ReviewEntity> = emptyList()): VerifiedProfessionalProfile {
        return when (pro.id) {
            "pro_karim_m" -> createKarimProfile(pro, reviews)
            "pro_mohamed_z" -> createMohamedProfile(pro, reviews)
            "pro_yacine_k" -> createYacineProfile(pro, reviews)
            else -> createAhmedProfile(pro, reviews)
        }
    }

    private fun createAhmedProfile(pro: ProfessionalEntity, reviews: List<ReviewEntity>): VerifiedProfessionalProfile {
        val certifications = listOf(
            ProfessionalCertification(
                id = "cert_cam_ahmed",
                title = "Carte d'Artisan Professionnel Qualifié",
                issuer = "Chambre d'Artisanat et des Métiers (CAM) - Wilaya de Blida",
                issueYear = 2014,
                credentialNumber = "DZ-CAM-09-4821/A",
                status = CertificationStatus.OFFICIALLY_ACCREDITED,
                badgeLabel = "Agrément d'État CAM",
                description = "Enregistrement officiel au registre national des artisans qualifiés en plomberie et réseaux de fluide.",
                isGovernmentAccredited = true
            ),
            ProfessionalCertification(
                id = "cert_cfpa_ahmed",
                title = "Diplôme d'État : Technicien Installation Sanitaire & Gaz",
                issuer = "Ministère de la Formation et de l'Enseignement Professionnels (CFPA)",
                issueYear = 2012,
                credentialNumber = "CFPA-BLD-2012-993",
                status = CertificationStatus.VERIFIED,
                badgeLabel = "Diplômé d'État",
                description = "Formation d'excellence théorique et pratique de 30 mois aux normes de raccordement gaz naturel et sanitaires.",
                isGovernmentAccredited = true
            ),
            ProfessionalCertification(
                id = "cert_sonelgaz_ahmed",
                title = "Certification Sécurité Brûleurs & Chauffe-eaux à Gaz",
                issuer = "Comité Technique Sécurité Gaz Domestique",
                issueYear = 2021,
                credentialNumber = "SEC-GAZ-DZ-2021-084",
                status = CertificationStatus.VERIFIED,
                badgeLabel = "Conforme Sonelgaz",
                description = "Habilitation aux diagnostics d'évacuation des fumées, étanchéité des conduits et prévention des intoxications au monoxyde de carbone (CO).",
                isGovernmentAccredited = true
            ),
            ProfessionalCertification(
                id = "cert_casnos_ahmed",
                title = "Affiliation en Règle CASNOS & Assurance Activité",
                issuer = "Caisse Nationale de Sécurité Sociale des Non-Salariés",
                issueYear = 2024,
                credentialNumber = "CASNOS-09-7712-4",
                status = CertificationStatus.VERIFIED,
                badgeLabel = "CASNOS Vérifié",
                description = "Cotisations sociales à jour et garantie civile professionnelle pour interventions chez les particuliers.",
                isGovernmentAccredited = true
            )
        )

        val serviceCategories = listOf(
            ServiceCategoryItem(
                id = "cat_water_heater",
                name = "Chauffe-eau & Chaudières",
                iconType = "heating",
                description = "Réparation de chauffe-eau à gaz (Junkers, Beretta, Saunier Duval), détartrage et remplacement de corps de chauffe.",
                servicesCount = 4,
                priceStartingDA = 2500,
                services = listOf(
                    ServiceItem(
                        id = "srv_heater_diagnostic",
                        title = "Diagnostic complet & Réparation Flamme / Veilleuse",
                        description = "Nettoyage gicleur, thermocouple, contrôle de tirage et allumage piézoélectrique.",
                        startingPriceDA = 2500,
                        pricingModel = "Forfait",
                        estimatedDuration = "45 min",
                        warrantyDays = 30,
                        isPopular = true
                    ),
                    ServiceItem(
                        id = "srv_heater_membrane",
                        title = "Remplacement Membrane & Valve d'eau",
                        description = "Résolution problème d'eau tiède, manque de pression ou fuite d'eau interne du bloc gaz.",
                        startingPriceDA = 3500,
                        pricingModel = "Forfait + Pièce",
                        estimatedDuration = "1h",
                        warrantyDays = 60,
                        isPopular = true
                    ),
                    ServiceItem(
                        id = "srv_heater_descaling",
                        title = "Détartrage chimique Serpentin & Corps de chauffe",
                        description = "Élimination totale du calcaire par pompe à acide spéciale cuivre pour rétablir le débit chaud.",
                        startingPriceDA = 4500,
                        pricingModel = "Forfait",
                        estimatedDuration = "1h 30 min",
                        warrantyDays = 90
                    ),
                    ServiceItem(
                        id = "srv_heater_install",
                        title = "Installation Neuf & Raccordement Gaz Conforme",
                        description = "Pose murale sécurisée, test d'étanchéité manomètre gaz et conduit d'évacuation inox.",
                        startingPriceDA = 6000,
                        pricingModel = "Forfait",
                        estimatedDuration = "2h 30 min",
                        warrantyDays = 180
                    )
                )
            ),
            ServiceCategoryItem(
                id = "cat_sanitary_plumbing",
                name = "Plomberie Sanitaire & Robinetterie",
                iconType = "plumbing",
                description = "Installation et dépannage de tuyauterie Multicouche, PPR, cuivre, robinets mélangeurs et mitigeurs.",
                servicesCount = 3,
                priceStartingDA = 2000,
                services = listOf(
                    ServiceItem(
                        id = "srv_faucet_fix",
                        title = "Réparation ou Remplacement Robinet / Mitigeur",
                        description = "Remplacement de cartouche céramique, flexibles armés et joints haute pression.",
                        startingPriceDA = 2000,
                        pricingModel = "Forfait",
                        estimatedDuration = "40 min",
                        warrantyDays = 30
                    ),
                    ServiceItem(
                        id = "srv_toilet_flush",
                        title = "Dépannage Mécanisme Chasse d'eau WC",
                        description = "Arrêt des fuites continues de réservoir, remplacement flotteur et joint cloche.",
                        startingPriceDA = 2200,
                        pricingModel = "Forfait",
                        estimatedDuration = "45 min",
                        warrantyDays = 30
                    ),
                    ServiceItem(
                        id = "srv_clog_drain",
                        title = "Débouchage Canalisation Évier / Baignoire",
                        description = "Intervention furet mécanique haute flexibilité sans endommager les canalisations PVC.",
                        startingPriceDA = 2800,
                        pricingModel = "Forfait",
                        estimatedDuration = "1h",
                        warrantyDays = 15
                    )
                )
            ),
            ServiceCategoryItem(
                id = "cat_leak_detection",
                name = "Recherche de Fuite & Sécurité Gaz",
                iconType = "leak",
                description = "Détection non-destructive de fuites encastrées et mise aux normes des conduits gaz.",
                servicesCount = 2,
                priceStartingDA = 3000,
                services = listOf(
                    ServiceItem(
                        id = "srv_leak_detection",
                        title = "Diagnostic Fuite Infiltrée / Encastrée",
                        description = "Localisation par manomètre différentiel et caméra d'inspection thermique.",
                        startingPriceDA = 3500,
                        pricingModel = "Diagnostic",
                        estimatedDuration = "1h 15 min",
                        warrantyDays = 30
                    ),
                    ServiceItem(
                        id = "srv_gas_safety",
                        title = "Contrôle Étanchéité Gaz & Robinet Arrêt Poussoir",
                        description = "Vérification complète de la ligne compteur-cuisine avec produit moussant certifié et test pression.",
                        startingPriceDA = 3000,
                        pricingModel = "Forfait",
                        estimatedDuration = "50 min",
                        warrantyDays = 90
                    )
                )
            )
        )

        val totalStars = pro.fiveStarsCount + pro.fourStarsCount + pro.threeStarsCount + pro.twoStarsCount + pro.oneStarsCount
        val ratingBreakdown = RatingBreakdown(
            overallRating = pro.rating,
            totalReviewsCount = if (totalStars > 0) totalStars else pro.completedJobsCount,
            recommendationRatePercent = 99,
            punctualityScore = pro.punctualityRating,
            craftsmanshipScore = pro.craftsmanshipRating,
            cleanlinessScore = pro.cleanlinessRating,
            pricingTransparencyScore = pro.priceFairnessRating,
            fiveStarsCount = pro.fiveStarsCount,
            fourStarsCount = pro.fourStarsCount,
            threeStarsCount = pro.threeStarsCount,
            twoStarsCount = pro.twoStarsCount,
            oneStarsCount = pro.oneStarsCount
        )

        return VerifiedProfessionalProfile(
            pro = pro,
            headline = "Artisan Plombier Chauffagiste Certifié d'État",
            primaryCategory = "Plomberie & Chauffage",
            secondaryCategories = listOf("Chauffe-eau à gaz", "Chaudières", "Sanitaire", "Détection Fuites"),
            certifications = certifications,
            serviceCategories = serviceCategories,
            ratingBreakdown = ratingBreakdown,
            yearsExperience = pro.yearsExperience,
            workshopAddress = pro.workshopAddress,
            guarantees = listOf(
                "Facture officielle conforme WASTA avec devis préalable",
                "Garantie écrite 30 jours minimum pièces et main d'œuvre",
                "Matériel agréé et pièces de rechange d'origine",
                "Nettoyage complet du chantier après travaux"
            ),
            languagesSpoken = listOf("Arabe / Darija", "Français", "Tamazight"),
            emergencyAvailable = true,
            responseTimeMinutes = pro.avgResponseMinutes
        )
    }

    private fun createKarimProfile(pro: ProfessionalEntity, reviews: List<ReviewEntity>): VerifiedProfessionalProfile {
        val certifications = listOf(
            ProfessionalCertification(
                id = "cert_cam_karim",
                title = "Carte d'Artisan Électricien Bâtiment",
                issuer = "Chambre d'Artisanat et des Métiers (CAM) - Alger",
                issueYear = 2016,
                credentialNumber = "DZ-CAM-16-1082",
                status = CertificationStatus.OFFICIALLY_ACCREDITED,
                badgeLabel = "Agrément d'État CAM",
                description = "Qualification officielle pour travaux d'électricité basse tension et rénovation de tableaux électriques.",
                isGovernmentAccredited = true
            ),
            ProfessionalCertification(
                id = "cert_cfpa_karim",
                title = "Brevet de Technicien Supérieur (BTS) Électrotechnique",
                issuer = "Institut National Spécialisé de la Formation Professionnelle (INSFP)",
                issueYear = 2015,
                credentialNumber = "INSFP-ALG-2015-442",
                status = CertificationStatus.VERIFIED,
                badgeLabel = "BTS d'État",
                description = "Maîtrise complète des schémas d'armoires électriques, protections différentielles et prises de terre.",
                isGovernmentAccredited = true
            )
        )

        val serviceCategories = listOf(
            ServiceCategoryItem(
                id = "cat_elec_panel",
                name = "Tableau Électrique & Disjoncteurs",
                iconType = "elec",
                description = "Diagnostic disjoncteur qui saute, équilibrage des phases et mise en conformité sécurité.",
                servicesCount = 3,
                priceStartingDA = 2500,
                services = listOf(
                    ServiceItem(
                        id = "srv_breaker_trip",
                        title = "Diagnostic Court-circuit & Disjoncteur différentiel",
                        description = "Identification rapide de la ligne en court-circuit ou de l'appareil défectueux.",
                        startingPriceDA = 2500,
                        pricingModel = "Diagnostic",
                        estimatedDuration = "45 min",
                        warrantyDays = 30,
                        isPopular = true
                    ),
                    ServiceItem(
                        id = "srv_panel_renov",
                        title = "Rénovation & Mise aux Normes Tableau Électrique",
                        description = "Remplacement des fusibles porcelaine par des disjoncteurs modulaires modernes.",
                        startingPriceDA = 7000,
                        pricingModel = "Forfait + Disjoncteurs",
                        estimatedDuration = "3h",
                        warrantyDays = 90
                    )
                )
            ),
            ServiceCategoryItem(
                id = "cat_elec_wiring",
                name = "Prises, Éclairage & Câblage",
                iconType = "light",
                description = "Ajout de prises de courant, pose de luminaires, variateurs et raccordement terre.",
                servicesCount = 2,
                priceStartingDA = 2000,
                services = listOf(
                    ServiceItem(
                        id = "srv_outlet_install",
                        title = "Installation / Réparation Prise de courant & Interrupteur",
                        description = "Remplacement appareillage encastré ou saillie avec fixation solide.",
                        startingPriceDA = 2000,
                        pricingModel = "Forfait",
                        estimatedDuration = "30 min",
                        warrantyDays = 30
                    )
                )
            )
        )

        val ratingBreakdown = RatingBreakdown(
            overallRating = pro.rating,
            totalReviewsCount = pro.completedJobsCount,
            recommendationRatePercent = 99,
            punctualityScore = 4.95f,
            craftsmanshipScore = 5.0f,
            cleanlinessScore = 4.9f,
            pricingTransparencyScore = 4.85f,
            fiveStarsCount = 175,
            fourStarsCount = 13,
            threeStarsCount = 1,
            twoStarsCount = 0,
            oneStarsCount = 0
        )

        return VerifiedProfessionalProfile(
            pro = pro,
            headline = "Électricien d'État Diplômé & Expert Sécurité Bâtiment",
            primaryCategory = "Électricité Bâtiment",
            secondaryCategories = listOf("Tableaux différentiels", "Diagnostic court-circuit", "Câblage", "Éclairage"),
            certifications = certifications,
            serviceCategories = serviceCategories,
            ratingBreakdown = ratingBreakdown,
            yearsExperience = 11,
            workshopAddress = "Bab El Oued, Alger Centre",
            guarantees = listOf(
                "Facture numérique détaillée avec garantie 30 jours",
                "Vérification systématique de la prise de terre",
                "Conformité avec les règles de protection Sonelgaz"
            ),
            languagesSpoken = listOf("Arabe / Darija", "Français"),
            emergencyAvailable = true,
            responseTimeMinutes = pro.avgResponseMinutes
        )
    }

    private fun createMohamedProfile(pro: ProfessionalEntity, reviews: List<ReviewEntity>): VerifiedProfessionalProfile {
        val certifications = listOf(
            ProfessionalCertification(
                id = "cert_cam_mohamed",
                title = "Carte Professionnelle Frigoriste & Climatisation",
                issuer = "Chambre d'Artisanat et des Métiers (CAM) - Oran",
                issueYear = 2017,
                credentialNumber = "DZ-CAM-31-9041",
                status = CertificationStatus.OFFICIALLY_ACCREDITED,
                badgeLabel = "Agrément CAM Oran",
                description = "Qualification officielle pour manipulation des fluides frigorigènes et installation HVAC.",
                isGovernmentAccredited = true
            ),
            ProfessionalCertification(
                id = "cert_froid_mohamed",
                title = "Attestation d'Aptitude Fluides Frigorigènes R410A / R32",
                issuer = "Centre National Technique du Froid",
                issueYear = 2019,
                credentialNumber = "FRIG-DZ-2019-301",
                status = CertificationStatus.VERIFIED,
                badgeLabel = "Certifié Frigoriste",
                description = "Habilitation officielle au tirage au vide, détection de fuite azote et recharge gaz.",
                isGovernmentAccredited = true
            )
        )

        val serviceCategories = listOf(
            ServiceCategoryItem(
                id = "cat_ac_recharge",
                name = "Recharge Gaz & Nettoyage Climatiseur",
                iconType = "ac",
                description = "Entretien saisonnier, traitement antibactérien de l'échangeur et recharge de gaz frigorigène.",
                servicesCount = 2,
                priceStartingDA = 3000,
                services = listOf(
                    ServiceItem(
                        id = "srv_ac_clean",
                        title = "Nettoyage & Désinfection Complète Unité Intérieure/Extérieure",
                        description = "Nettoyage haute pression des filtres, traitement fongicide du bac à condensats.",
                        startingPriceDA = 3000,
                        pricingModel = "Forfait",
                        estimatedDuration = "1h",
                        warrantyDays = 30,
                        isPopular = true
                    ),
                    ServiceItem(
                        id = "srv_ac_gas",
                        title = "Tirage au vide & Recharge Gaz R410A / R32",
                        description = "Contrôle manométrique de pression et pesée précise du fluide.",
                        startingPriceDA = 5500,
                        pricingModel = "Forfait gaz inclus",
                        estimatedDuration = "1h 30 min",
                        warrantyDays = 60
                    )
                )
            )
        )

        val ratingBreakdown = RatingBreakdown(
            overallRating = pro.rating,
            totalReviewsCount = pro.completedJobsCount,
            recommendationRatePercent = 97,
            punctualityScore = 4.8f,
            craftsmanshipScore = 4.9f,
            cleanlinessScore = 4.8f,
            pricingTransparencyScore = 4.85f,
            fiveStarsCount = 125,
            fourStarsCount = 15,
            threeStarsCount = 2,
            twoStarsCount = 0,
            oneStarsCount = 0
        )

        return VerifiedProfessionalProfile(
            pro = pro,
            headline = "Frigoriste Spécialiste Climatiseurs & Pompes à Chaleur",
            primaryCategory = "Climatisation & Froid",
            secondaryCategories = listOf("Clim Split", "Recharge R410A", "Nettoyage antibactérien"),
            certifications = certifications,
            serviceCategories = serviceCategories,
            ratingBreakdown = ratingBreakdown,
            yearsExperience = 9,
            workshopAddress = "Es Senia, Oran",
            guarantees = listOf(
                "Facture officielle WASTA et garantie 30 jours",
                "Contrôle d'étanchéité manométrique avant recharge",
                "Pièces et compresseurs d'origine garantis"
            ),
            languagesSpoken = listOf("Arabe / Darija", "Français"),
            emergencyAvailable = true,
            responseTimeMinutes = pro.avgResponseMinutes
        )
    }

    private fun createYacineProfile(pro: ProfessionalEntity, reviews: List<ReviewEntity>): VerifiedProfessionalProfile {
        val certifications = listOf(
            ProfessionalCertification(
                id = "cert_cam_yacine",
                title = "Carte d'Artisan Électroménager & Froid Domestique",
                issuer = "Chambre d'Artisanat et des Métiers (CAM) - Constantine",
                issueYear = 2018,
                credentialNumber = "DZ-CAM-25-3390",
                status = CertificationStatus.OFFICIALLY_ACCREDITED,
                badgeLabel = "Agrément CAM",
                description = "Agrément de réparation d'appareils de gros électroménager.",
                isGovernmentAccredited = true
            )
        )

        val serviceCategories = listOf(
            ServiceCategoryItem(
                id = "cat_appliance_wash",
                name = "Lave-linge & Lave-vaisselle",
                iconType = "appliance",
                description = "Réparation de pompes de vidange, changement de roulements de tambour et cartes électroniques.",
                servicesCount = 2,
                priceStartingDA = 2000,
                services = listOf(
                    ServiceItem(
                        id = "srv_washer_diag",
                        title = "Diagnostic Panne & Vidange Lave-linge",
                        description = "Test moteur, charbons, électrovanne et pressostat.",
                        startingPriceDA = 2000,
                        pricingModel = "Diagnostic",
                        estimatedDuration = "45 min",
                        warrantyDays = 30,
                        isPopular = true
                    )
                )
            )
        )

        val ratingBreakdown = RatingBreakdown(
            overallRating = pro.rating,
            totalReviewsCount = pro.completedJobsCount,
            recommendationRatePercent = 98,
            punctualityScore = 4.85f,
            craftsmanshipScore = 4.9f,
            cleanlinessScore = 4.85f,
            pricingTransparencyScore = 4.9f,
            fiveStarsCount = 104,
            fourStarsCount = 10,
            threeStarsCount = 2,
            twoStarsCount = 0,
            oneStarsCount = 0
        )

        return VerifiedProfessionalProfile(
            pro = pro,
            headline = "Technicien Électroménager & Diagnostic Électronique",
            primaryCategory = "Électroménager",
            secondaryCategories = listOf("Lave-linge", "Réfrigérateurs", "Lave-vaisselle"),
            certifications = certifications,
            serviceCategories = serviceCategories,
            ratingBreakdown = ratingBreakdown,
            yearsExperience = 8,
            workshopAddress = "Ali Mendjeli, Constantine",
            guarantees = listOf(
                "Facture officielle WASTA et garantie 30 jours",
                "Utilisation de pièces de rechange testées"
            ),
            languagesSpoken = listOf("Arabe / Darija", "Français"),
            emergencyAvailable = true,
            responseTimeMinutes = pro.avgResponseMinutes
        )
    }
}
