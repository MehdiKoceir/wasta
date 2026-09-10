package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

/**
 * A detailed, sleek view component for displaying professional ratings and certifications.
 * Designed with high-contrast typography, generous padding, and responsive micro-interactions.
 */
@Composable
fun ProfessionalRatingsAndCertificationsView(
    profile: VerifiedProfessionalProfile,
    modifier: Modifier = Modifier,
    onCertificationClick: (ProfessionalCertification) -> Unit = {},
    onRequestBooking: () -> Unit = {}
) {
    var activeSubSection by remember { mutableStateOf(DetailSectionTab.ALL) }
    var selectedStarFilter by remember { mutableStateOf<Int?>(null) }
    var expandedCertId by remember { mutableStateOf<String?>(null) }

    val ratingBreakdown = profile.ratingBreakdown
    val certifications = profile.certifications

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("component_ratings_and_certifications"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Selector Tabs (Tous, Notes & Évaluations, Agréments & Diplômes)
        SleekFilterRow(
            selectedTab = activeSubSection,
            onTabSelected = { activeSubSection = it },
            certificationsCount = certifications.size,
            ratingsCount = ratingBreakdown.totalReviewsCount
        )

        // 1. Ratings Section
        if (activeSubSection == DetailSectionTab.ALL || activeSubSection == DetailSectionTab.RATINGS) {
            SleekRatingBreakdownCard(
                breakdown = ratingBreakdown,
                selectedStarFilter = selectedStarFilter,
                onStarFilterChange = { star ->
                    selectedStarFilter = if (selectedStarFilter == star) null else star
                }
            )

            // Performance Pillars (Ponctualité, Qualité, Propreté, Transparence)
            SleekPerformancePillarsGrid(breakdown = ratingBreakdown)

            // Verified Reviews List
            SleekVerifiedReviewsList(
                starFilter = selectedStarFilter,
                proName = profile.pro.name
            )
        }

        // 2. Certifications & Accreditations Section
        if (activeSubSection == DetailSectionTab.ALL || activeSubSection == DetailSectionTab.CERTIFICATIONS) {
            SleekCertificationsVaultHeader(certificationsCount = certifications.size)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                certifications.forEach { cert ->
                    SleekCertificationDetailCard(
                        certification = cert,
                        isExpanded = expandedCertId == cert.id,
                        onToggleExpand = {
                            expandedCertId = if (expandedCertId == cert.id) null else cert.id
                        },
                        onInspectDetails = { onCertificationClick(cert) }
                    )
                }
            }

            // Trust & State Accreditation Seal Banner
            SleekStateAccreditationBanner(wilaya = profile.pro.baseWilaya)
        }
    }
}

enum class DetailSectionTab {
    ALL,
    RATINGS,
    CERTIFICATIONS
}

@Composable
private fun SleekFilterRow(
    selectedTab: DetailSectionTab,
    onTabSelected: (DetailSectionTab) -> Unit,
    certificationsCount: Int,
    ratingsCount: Int
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SleekNavSurface,
        border = BorderStroke(1.dp, SleekBorder.copy(alpha = 0.7f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SleekTabButton(
                title = "Vue Complète",
                isSelected = selectedTab == DetailSectionTab.ALL,
                onClick = { onTabSelected(DetailSectionTab.ALL) },
                modifier = Modifier.weight(1f)
            )
            SleekTabButton(
                title = "Avis ($ratingsCount)",
                isSelected = selectedTab == DetailSectionTab.RATINGS,
                onClick = { onTabSelected(DetailSectionTab.RATINGS) },
                modifier = Modifier.weight(1f)
            )
            SleekTabButton(
                title = "Agréments ($certificationsCount)",
                isSelected = selectedTab == DetailSectionTab.CERTIFICATIONS,
                onClick = { onTabSelected(DetailSectionTab.CERTIFICATIONS) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SleekTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) SleekSurface else Color.Transparent)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) SleekBorder else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) SleekNavy else SleekTextSecondary
        )
    }
}

@Composable
private fun SleekRatingBreakdownCard(
    breakdown: RatingBreakdown,
    selectedStarFilter: Int?,
    onStarFilterChange: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        border = BorderStroke(1.dp, SleekBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_sleek_rating_breakdown")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SYNTHÈSE DES ÉVALUATIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextSecondary,
                    letterSpacing = 0.8.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "100% Vérifiés après paiement",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Main Big Score Column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(110.dp)
                ) {
                    Text(
                        text = "%.1f".format(breakdown.overallRating),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = SleekNavy,
                        lineHeight = 46.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        (1..5).forEach { _ ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = SleekGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${breakdown.totalReviewsCount} avis clients",
                        fontSize = 11.sp,
                        color = SleekTextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${breakdown.recommendationRatePercent}% recommandent",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Star Distribution Interactive Bars
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val total = breakdown.totalReviewsCount.coerceAtLeast(1).toFloat()
                    StarDistributionRow(star = 5, count = breakdown.fiveStarsCount, total = total, isSelected = selectedStarFilter == 5, onClick = { onStarFilterChange(5) })
                    StarDistributionRow(star = 4, count = breakdown.fourStarsCount, total = total, isSelected = selectedStarFilter == 4, onClick = { onStarFilterChange(4) })
                    StarDistributionRow(star = 3, count = breakdown.threeStarsCount, total = total, isSelected = selectedStarFilter == 3, onClick = { onStarFilterChange(3) })
                    StarDistributionRow(star = 2, count = breakdown.twoStarsCount, total = total, isSelected = selectedStarFilter == 2, onClick = { onStarFilterChange(2) })
                    StarDistributionRow(star = 1, count = breakdown.oneStarsCount, total = total, isSelected = selectedStarFilter == 1, onClick = { onStarFilterChange(1) })
                }
            }
        }
    }
}

@Composable
private fun StarDistributionRow(
    star: Int,
    count: Int,
    total: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) SleekPrimaryContainer.copy(alpha = 0.5f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$star★",
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) SleekPrimary else SleekTextSecondary,
            modifier = Modifier.width(26.dp)
        )
        LinearProgressIndicator(
            progress = { (count / total).coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (isSelected) SleekPrimaryDark else SleekGold,
            trackColor = SleekContainer
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$count",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = SleekNavy,
            modifier = Modifier.width(28.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun SleekPerformancePillarsGrid(breakdown: RatingBreakdown) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PillarMetricCard(
            title = "Ponctualité",
            score = breakdown.punctualityScore,
            icon = Icons.Outlined.Schedule,
            modifier = Modifier.weight(1f)
        )
        PillarMetricCard(
            title = "Qualité Travail",
            score = breakdown.craftsmanshipScore,
            icon = Icons.Outlined.Handyman,
            modifier = Modifier.weight(1f)
        )
        PillarMetricCard(
            title = "Propreté",
            score = breakdown.cleanlinessScore,
            icon = Icons.Outlined.CleaningServices,
            modifier = Modifier.weight(1f)
        )
        PillarMetricCard(
            title = "Transparence",
            score = breakdown.pricingTransparencyScore,
            icon = Icons.Outlined.PriceCheck,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PillarMetricCard(
    title: String,
    score: Float,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SleekSurface,
        border = BorderStroke(1.dp, SleekBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SleekPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = SleekPrimary, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "%.1f".format(score),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = SleekNavy
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = SleekTextSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun SleekVerifiedReviewsList(
    starFilter: Int?,
    proName: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (starFilter != null) "AVIS FILTRÉS ($starFilter ÉTOILES)" else "AVIS CLIENTS RÉCENTS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SleekTextSecondary,
                letterSpacing = 0.8.sp
            )
            if (starFilter != null) {
                Text(
                    text = "Réinitialiser filtre",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SleekPrimary,
                    modifier = Modifier.clickable { /* handled upstream */ }
                )
            }
        }

        // Review 1
        if (starFilter == null || starFilter == 5) {
            SleekReviewItem(
                author = "Amine B.",
                location = "Boufarik, Blida",
                timeAgo = "Hier",
                rating = 5,
                service = "Dépannage Chauffe-eau Gaz",
                quote = "Intervention rapide en 45 minutes chrono. Diagnostic précis de la membrane et contrôle manométrique des fuites de gaz effectué dans les règles de l'art.",
                verifiedJobId = "JOB-DZ-9942"
            )
        }

        // Review 2
        if (starFilter == null || starFilter == 5) {
            SleekReviewItem(
                author = "Khadija M.",
                location = "Ouled Yaich, Blida",
                timeAgo = "Il y a 5 jours",
                rating = 5,
                service = "Recherche de Fuite Encastrée",
                quote = "Artisan très respectueux et professionnel. Prix annoncé par devis conforme au centime près. Chantier laissé parfaitement propre après les travaux.",
                verifiedJobId = "JOB-DZ-9811"
            )
        }

        // Review 3
        if (starFilter == null || starFilter == 4) {
            SleekReviewItem(
                author = "Youcef K.",
                location = "Chéraga, Alger",
                timeAgo = "Il y a 2 semaines",
                rating = 4,
                service = "Installation Robinetterie Sanitaire",
                quote = "Très bon travail technique et raccordement multicouche soigné. Juste 10 minutes de retard avec les embouteillages de l'autoroute, mais a prévenu par téléphone.",
                verifiedJobId = "JOB-DZ-9650"
            )
        }
    }
}

@Composable
private fun SleekReviewItem(
    author: String,
    location: String,
    timeAgo: String,
    rating: Int,
    service: String,
    quote: String,
    verifiedJobId: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SleekSurface,
        border = BorderStroke(1.dp, SleekBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SleekPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = author.take(1),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SleekNavy
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = author, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SleekNavy)
                        Text(text = "$location • $timeAgo", fontSize = 11.sp, color = SleekTextSecondary)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    (1..5).forEach { s ->
                        Icon(
                            imageVector = if (s <= rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = SleekGold,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SleekNavSurface)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = service, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = SleekPrimary)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = quote,
                fontSize = 12.sp,
                color = SleekTextPrimary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Intervention confirmée $verifiedJobId",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF166534)
                    )
                }
                Text(
                    text = "Facture réglée ✓",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextSecondary
                )
            }
        }
    }
}

@Composable
private fun SleekCertificationsVaultHeader(certificationsCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "DIPLÔMES & AGRÉMENTS D'ÉTAT ($certificationsCount)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SleekTextSecondary,
            letterSpacing = 0.8.sp
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEFF6FF))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = SleekPrimary, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Vérifié Registre National",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekPrimary
                )
            }
        }
    }
}

@Composable
private fun SleekCertificationDetailCard(
    certification: ProfessionalCertification,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onInspectDetails: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        border = BorderStroke(
            width = if (isExpanded) 1.5.dp else 1.dp,
            color = if (isExpanded) SleekPrimary else SleekBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleExpand)
            .testTag("card_certification_${certification.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SleekPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = SleekPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = certification.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekNavy
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = certification.issuer,
                            fontSize = 11.sp,
                            color = SleekTextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Agréé",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Réf : ",
                        fontSize = 11.sp,
                        color = SleekTextSecondary
                    )
                    Text(
                        text = certification.credentialNumber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekNavy
                    )
                }
                Text(
                    text = "Délivré en ${certification.issueYear}",
                    fontSize = 11.sp,
                    color = SleekTextSecondary
                )
            }

            // Expandable details block
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = SleekBorder.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Champ d'application & Habilitation :",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekNavy
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = certification.description,
                        fontSize = 12.sp,
                        color = SleekTextSecondary,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onInspectDetails,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Voir l'attestation complète", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SleekPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SleekStateAccreditationBanner(wilaya: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = SleekNavy,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Garantie de Conformité Juridique WASTA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekNavy
                )
                Text(
                    text = "Tous les artisans certifiés sont répertoriés auprès de la CAM de leur wilaya ($wilaya) et soumis au barème tarifaire clair.",
                    fontSize = 10.sp,
                    color = SleekTextSecondary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
