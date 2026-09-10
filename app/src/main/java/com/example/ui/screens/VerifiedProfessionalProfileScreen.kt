package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.WastaViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifiedProfessionalProfileScreen(
    profile: VerifiedProfessionalProfile,
    viewModel: WastaViewModel,
    onBack: () -> Unit
) {
    val language by viewModel.currentLanguage.collectAsState()
    val allReviews by viewModel.allJobs.collectAsState()
    val initialTab by viewModel.profileInitialTab.collectAsState()
    var selectedTabIndex by remember(initialTab) { mutableIntStateOf(initialTab) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>("Tous") }
    var showCertificationDetailDialog by remember { mutableStateOf<ProfessionalCertification?>(null) }
    var showBookingConfirmDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showAddReviewDialog by remember { mutableStateOf(false) }
    var reviewStarFilter by remember { mutableIntStateOf(0) }
    val dbReviews by viewModel.getReviewsForProfessional(profile.pro.id).collectAsState(initial = emptyList())

    val pro = profile.pro

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (language == "ar") "الملف المهني المعتمد" else "Profil Professionnel Vérifié",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekNavy
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == "ar") "معتمد رسمياً • متاح الآن" else "Agréé Officiel CAM • En Ligne",
                                fontSize = 11.sp,
                                color = SleekPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("button_back_profile")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = SleekNavy
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.showNotification("Lien du profil certifié copié !") }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Partager",
                            tint = SleekNavy
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SleekBackground
                )
            )
        },
        bottomBar = {
            Surface(
                color = SleekSurface,
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                border = BorderStroke(1.dp, SleekBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tarif indicatif moyen",
                            fontSize = 11.sp,
                            color = SleekTextSecondary
                        )
                        Text(
                            text = "${pro.priceMinDA} – ${pro.priceMaxDA} DA",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = SleekPrimary
                        )
                        Text(
                            text = "Garantie 30j incluse",
                            fontSize = 10.sp,
                            color = Color(0xFF16A34A),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { showBookingConfirmDialog = true },
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("button_book_profile_pro"),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == "ar") "حجز موعد فوري" else "Réserver cet artisan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showContactDialog = true },
                icon = {
                    Icon(
                        imageVector = Icons.Default.ContactPhone,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                },
                text = {
                    Text(
                        text = if (language == "ar") "اتصال بالحرفي" else "Contacter l'artisan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                },
                containerColor = SleekNavy,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .testTag("fab_contact_professional")
                    .padding(bottom = 8.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SleekBackground)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Hero Identity Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SleekSurface),
                    border = BorderStroke(1.dp, SleekBorder),
                    modifier = Modifier.fillMaxWidth().testTag("card_profile_hero")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Avatar with Gold Shield & Online Indicator
                            Box(modifier = Modifier.size(72.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(SleekPrimaryContainer)
                                        .border(2.dp, SleekPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = pro.name.take(2).uppercase(),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SleekNavy
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(SleekGold)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Agréé",
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = pro.name,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SleekTextPrimary
                                    )
                                    // Rating Pill (Clickable to switch directly to Reviews & Ratings tab)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(SleekGoldLight)
                                            .clickable { selectedTabIndex = 2 }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                            .testTag("hero_rating_pill_${pro.id}")
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "Notes et avis",
                                                tint = SleekGoldDark,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "${profile.ratingBreakdown.overallRating}",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 13.sp,
                                                color = SleekGoldDark
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = profile.headline,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SleekPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocationOn,
                                        contentDescription = null,
                                        tint = SleekTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${pro.baseWilaya} (${pro.wilayasCovered})",
                                        fontSize = 11.sp,
                                        color = SleekTextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = SleekBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick stats ribbon
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ProfileStatItem(
                                title = "Expérience",
                                value = "${profile.yearsExperience} ans",
                                icon = Icons.Outlined.WorkspacePremium
                            )
                            ProfileStatItem(
                                title = "Chantiers",
                                value = "${pro.completedJobsCount} finis",
                                icon = Icons.Outlined.CheckCircle
                            )
                            ProfileStatItem(
                                title = "Réactivité",
                                value = "~${profile.responseTimeMinutes} min",
                                icon = Icons.Outlined.Speed
                            )
                            ProfileStatItem(
                                title = "Avis positifs",
                                value = "${profile.ratingBreakdown.recommendationRatePercent}%",
                                icon = Icons.Outlined.ThumbUp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bio
                        Text(
                            text = pro.bio,
                            fontSize = 13.sp,
                            color = SleekTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // 2. Official Verification Badges Ribbon
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "VÉRIFICATIONS OFFICIELLES WASTA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekTextSecondary,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            VerificationPill(
                                icon = Icons.Default.Shield,
                                title = "Carte Artisan CAM",
                                subtitle = "Enregistré & Vérifié",
                                isVerified = true,
                                onClick = {
                                    showCertificationDetailDialog = profile.certifications.firstOrNull { it.id.contains("cam") }
                                }
                            )
                        }
                        item {
                            VerificationPill(
                                icon = Icons.Default.School,
                                title = "Diplôme d'État CFPA",
                                subtitle = "Installateur Sanitaire/Gaz",
                                isVerified = true,
                                onClick = {
                                    showCertificationDetailDialog = profile.certifications.firstOrNull { it.id.contains("cfpa") }
                                }
                            )
                        }
                        item {
                            VerificationPill(
                                icon = Icons.Default.VerifiedUser,
                                title = "Identité & Casier",
                                subtitle = "Contrôlés par WASTA",
                                isVerified = pro.identityVerified,
                                onClick = {
                                    viewModel.showNotification("Identité nationale (NIN) et casier vérifiés conforme.")
                                }
                            )
                        }
                        item {
                            VerificationPill(
                                icon = Icons.Default.LocalPolice,
                                title = "Assurance & CASNOS",
                                subtitle = "À jour 2026",
                                isVerified = pro.insuranceVerified,
                                onClick = {
                                    showCertificationDetailDialog = profile.certifications.firstOrNull { it.id.contains("casnos") }
                                }
                            )
                        }
                    }
                }
            }

            // 3. Navigation Tabs (Services / Certifications / Ratings)
            item {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = SleekContainer,
                    contentColor = SleekPrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, SleekBorder, RoundedCornerShape(16.dp))
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = {
                            Text(
                                text = "Services (${profile.serviceCategories.sumOf { it.services.size }})",
                                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = { Icon(Icons.Outlined.Handyman, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = {
                            Text(
                                text = "Agréments & Notes",
                                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = { Icon(Icons.Outlined.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = {
                            Text(
                                text = "Avis (${profile.ratingBreakdown.totalReviewsCount})",
                                fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        },
                        icon = { Icon(Icons.Outlined.StarRate, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }

            // 4. Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // Services & Categories Tab
                    item {
                        Column {
                            Text(
                                text = "CATÉGORIES DE PRESTATIONS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextSecondary,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    FilterChip(
                                        selected = selectedCategoryFilter == "Tous",
                                        onClick = { selectedCategoryFilter = "Tous" },
                                        label = { Text("Tous les services") },
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                }
                                items(profile.serviceCategories) { cat ->
                                    FilterChip(
                                        selected = selectedCategoryFilter == cat.name,
                                        onClick = { selectedCategoryFilter = cat.name },
                                        label = { Text(cat.name) },
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    val filteredCategories = if (selectedCategoryFilter == "Tous") {
                        profile.serviceCategories
                    } else {
                        profile.serviceCategories.filter { it.name == selectedCategoryFilter }
                    }

                    items(filteredCategories) { category ->
                        ServiceCategoryCard(
                            category = category,
                            onSelectService = { service ->
                                viewModel.showNotification("Prestation sélectionnée : ${service.title}")
                                showBookingConfirmDialog = true
                            }
                        )
                    }

                    // Guarantees banner
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = SleekPrimaryContainer),
                            border = BorderStroke(1.dp, SleekPrimary.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.GppGood,
                                        contentDescription = null,
                                        tint = SleekNavy,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Engagements & Garanties WASTA",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = SleekNavy
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                profile.guarantees.forEach { guarantee ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = SleekPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = guarantee, fontSize = 12.sp, color = SleekNavy)
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Detailed Ratings & Certifications View Component
                    item {
                        ProfessionalRatingsAndCertificationsView(
                            profile = profile,
                            onCertificationClick = { showCertificationDetailDialog = it },
                            onRequestBooking = { showBookingConfirmDialog = true }
                        )
                    }
                }

                2 -> {
                    // Ratings & Evaluations Tab
                    item {
                        RatingSummaryOverviewCard(ratingBreakdown = profile.ratingBreakdown)
                    }

                    // Button to leave a star rating and written review
                    item {
                        Button(
                            onClick = { showAddReviewDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("button_open_add_review")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RateReview,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == "ar") "تقييم هذا الحرفي وكتابة رأي" else "Laisser une note et un avis client",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }

                    // Filter reviews by star count
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (language == "ar") "آراء وتقييمات العملاء السابقين" else "AVIS CLIENTS CERTIFIÉS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekTextSecondary,
                                letterSpacing = 0.8.sp
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(
                                    0 to (if (language == "ar") "الكل" else "Tous"),
                                    5 to "5★",
                                    4 to "4★"
                                ).forEach { (star, label) ->
                                    val isSelected = reviewStarFilter == star
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) SleekGoldDark else SleekContainer,
                                        border = BorderStroke(1.dp, if (isSelected) SleekGoldDark else SleekBorder),
                                        modifier = Modifier
                                            .clickable { reviewStarFilter = star }
                                            .testTag("filter_star_$star")
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else SleekTextPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Real Client Reviews from Room Database
                    val filteredDbReviews = if (reviewStarFilter == 0) {
                        dbReviews
                    } else {
                        dbReviews.filter { it.rating.toInt() == reviewStarFilter }
                    }

                    if (filteredDbReviews.isNotEmpty()) {
                        items(filteredDbReviews, key = { it.id }) { rev ->
                            VerifiedCustomerReviewCard(
                                customerName = rev.customerName,
                                date = "Client Vérifié",
                                serviceTitle = "Prestation confirmée & garantie",
                                rating = rev.rating,
                                comment = rev.comment,
                                verifiedProof = "Intervention vérifiée #${rev.jobId.takeLast(6)}"
                            )
                        }
                    }

                    // Curated verified reviews if filter matches
                    if (reviewStarFilter == 0 || reviewStarFilter == 5) {
                        item {
                            VerifiedCustomerReviewCard(
                                customerName = "Mehdi K.",
                                date = "Il y a 3 jours",
                                serviceTitle = "Dépannage Chauffe-eau Junkers",
                                rating = 5.0f,
                                comment = "Ahmed est intervenu chez moi à Boufarik en moins d'une heure. Il a changé la membrane et contrôlé l'évacuation des fumées avec son appareil. Très pro, ponctuel et travail extrêmement soigné. Facture claire.",
                                verifiedProof = "Intervention #job_0912 payée et clôturée"
                            )
                        }

                        item {
                            VerifiedCustomerReviewCard(
                                customerName = "Farid L.",
                                date = "Il y a 2 semaines",
                                serviceTitle = "Recherche et réparation fuite sanitaire",
                                rating = 5.0f,
                                comment = "Diagnostic précis sans casser le carrelage inutilement. Artisan sérieux, respectueux et honnête sur les prix des pièces.",
                                verifiedProof = "Intervention #job_0874 payée et clôturée"
                            )
                        }
                    }

                    if (reviewStarFilter == 0 || reviewStarFilter == 4) {
                        item {
                            VerifiedCustomerReviewCard(
                                customerName = "Amel B.",
                                date = "Il y a 1 mois",
                                serviceTitle = "Installation Mitigeur et détartrage",
                                rating = 4.8f,
                                comment = "Très bon artisan, bon conseil pour le choix des raccords. Je recommande fortement pour tous travaux de plomberie.",
                                verifiedProof = "Intervention #job_0798 payée et clôturée"
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Modal Dialog: Add Rating & Review for Professional
    if (showAddReviewDialog) {
        var userRating by remember { mutableFloatStateOf(5.0f) }
        var reviewText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddReviewDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.StarRate,
                        contentDescription = null,
                        tint = SleekGoldDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == "ar") "تقييم ${pro.name}" else "Noter ${pro.name}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (language == "ar") "حدد تقييمك العام لجودة الخدمة" else "Sélectionnez votre note sur 5 étoiles :",
                        fontSize = 12.sp,
                        color = SleekTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // 5 Star interactive selector
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..5).forEach { starIndex ->
                            IconButton(
                                onClick = { userRating = starIndex.toFloat() },
                                modifier = Modifier.testTag("star_picker_$starIndex")
                            ) {
                                Icon(
                                    imageVector = if (starIndex <= userRating) Icons.Default.Star else Icons.Outlined.StarOutline,
                                    contentDescription = "$starIndex étoiles",
                                    tint = if (starIndex <= userRating) SleekGoldDark else SleekTextSecondary,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = when (userRating.toInt()) {
                            5 -> if (language == "ar") "5.0 ★ ممتاز جداً" else "5.0 ★ Excellent travail"
                            4 -> if (language == "ar") "4.0 ★ جيد جداً" else "4.0 ★ Très bon service"
                            3 -> if (language == "ar") "3.0 ★ جيد" else "3.0 ★ Bon service"
                            2 -> if (language == "ar") "2.0 ★ متوسط" else "2.0 ★ Moyen"
                            else -> if (language == "ar") "1.0 ★ غير مرضٍ" else "1.0 ★ Insatisfaisant"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekGoldDark
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_review_comment"),
                        label = {
                            Text(
                                text = if (language == "ar") "اكتب رأيك بالتفصيل (جودة العمل، الالتزام بالموعد...)"
                                else "Votre avis (ponctualité, propreté, tarif...)",
                                fontSize = 12.sp
                            )
                        },
                        placeholder = {
                            Text(
                                text = if (language == "ar") "عمل متقن وسريع، أنصح به بشدة..." else "Artisan très sérieux, travail propre et soigné...",
                                fontSize = 11.sp,
                                color = SleekTextSecondary
                            )
                        },
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val commentToSubmit = if (reviewText.isNotBlank()) reviewText.trim()
                        else if (language == "ar") "خدمة ممتازة ومطابقة للمواصفات."
                        else "Service impeccable, artisan ponctuel et travail très propre."

                        viewModel.submitDirectClientReview(pro.id, userRating, commentToSubmit)
                        showAddReviewDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("button_submit_review_dialog")
                ) {
                    Text(
                        text = if (language == "ar") "نشر التقييم" else "Publier l'avis",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddReviewDialog = false },
                    modifier = Modifier.testTag("button_cancel_review_dialog")
                ) {
                    Text(text = if (language == "ar") "إلغاء" else "Annuler")
                }
            }
        )
    }

    // Modal Dialog: Certification Details
    if (showCertificationDetailDialog != null) {
        val cert = showCertificationDetailDialog!!
        AlertDialog(
            onDismissRequest = { showCertificationDetailDialog = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = SleekPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = cert.badgeLabel, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = cert.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Organisme émetteur :", fontSize = 11.sp, color = SleekTextSecondary)
                    Text(text = cert.issuer, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "N° de Registre / Diplôme :", fontSize = 10.sp, color = SleekTextSecondary)
                            Text(text = cert.credentialNumber, fontSize = 12.sp, fontWeight = FontWeight.Black, color = SleekPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Année d'obtention :", fontSize = 10.sp, color = SleekTextSecondary)
                            Text(text = "${cert.issueYear}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    Text(text = cert.description, fontSize = 12.sp, color = SleekTextSecondary, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Statut : Vérifié conforme auprès du registre d'État",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCertificationDetailDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary)
                ) {
                    Text("Fermer")
                }
            }
        )
    }

    // Modal Dialog: Direct Booking Confirmation
    if (showBookingConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBookingConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EventAvailable, contentDescription = null, tint = SleekPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirmer la Réservation")
                }
            },
            text = {
                Column {
                    Text("Vous êtes sur le point de solliciter l'artisan certifié :", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("${pro.name} (${pro.specialties})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Zone d'intervention : ${pro.baseWilaya}", fontSize = 12.sp, color = SleekTextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "🔒 L'artisan recevra votre demande avec votre commune. Votre adresse exacte et numéro personnel ne seront partagés qu'après acceptation mutuelle.",
                        fontSize = 11.sp,
                        color = SleekTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitServiceRequestAndBook()
                        showBookingConfirmDialog = false
                        viewModel.closeVerifiedProfile()
                        viewModel.showNotification("Demande d'intervention envoyée à ${pro.name} !")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                    modifier = Modifier.testTag("button_confirm_booking_dialog")
                ) {
                    Text("Confirmer l'intervention")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBookingConfirmDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Modal Dialog: Contact Professional (Phone & Message Integration Placeholder)
    if (showContactDialog) {
        val context = LocalContext.current
        val proPhoneNumber = pro.phone.ifBlank { "+213 550 00 00 00" }

        AlertDialog(
            onDismissRequest = { showContactDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SleekPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = SleekPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == "ar") "الاتصال بـ ${pro.name}" else "Contacter ${pro.name}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekNavy
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (language == "ar") "متاح الآن • رد في ~${pro.avgResponseMinutes} د" else "En ligne • Répond en ~${pro.avgResponseMinutes} min",
                                fontSize = 11.sp,
                                color = Color(0xFF16A34A),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (language == "ar")
                            "اختر وسيلة الاتصال المباشرة بالحرفي المعتمد :"
                        else
                            "Sélectionnez le canal de contact direct avec l'artisan certifié :",
                        fontSize = 12.sp,
                        color = SleekTextSecondary
                    )

                    // Option 1: Phone Call
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SleekSurface,
                        border = BorderStroke(1.dp, SleekBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showContactDialog = false
                                try {
                                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${proPhoneNumber.replace(" ", "")}")
                                    }
                                    context.startActivity(dialIntent)
                                    viewModel.showNotification("Lancement de l'appel téléphonique vers ${pro.name} ($proPhoneNumber)")
                                } catch (e: Exception) {
                                    viewModel.showNotification("Appel vers ${pro.name} : $proPhoneNumber")
                                }
                            }
                            .testTag("button_contact_phone")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneInTalk,
                                    contentDescription = "Téléphone",
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == "ar") "مكالمة هاتفية مباشرة" else "Appel Téléphonique Direct",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekNavy
                                )
                                Text(
                                    text = proPhoneNumber,
                                    fontSize = 12.sp,
                                    color = SleekTextSecondary
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = SleekTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Option 2: Message / SMS
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SleekSurface,
                        border = BorderStroke(1.dp, SleekBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showContactDialog = false
                                try {
                                    val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("smsto:${proPhoneNumber.replace(" ", "")}")
                                        putExtra("sms_body", "Bonjour ${pro.name}, je vous contacte via WASTA pour des travaux de ${pro.specialties}.")
                                    }
                                    context.startActivity(smsIntent)
                                    viewModel.showNotification("Ouverture de la messagerie pour ${pro.name}")
                                } catch (e: Exception) {
                                    viewModel.showNotification("Message à ${pro.name} : ouverture SMS")
                                }
                            }
                            .testTag("button_contact_message")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Chat,
                                    contentDescription = "Message",
                                    tint = SleekPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (language == "ar") "إرسال رسالة SMS / نصية" else "Message Instantané / SMS",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekNavy
                                )
                                Text(
                                    text = if (language == "ar") "رسالة مسبقة التعبئة للخدمة" else "Demande d'information ou question rapide",
                                    fontSize = 11.sp,
                                    color = SleekTextSecondary
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = SleekTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Security / Privacy Note
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SleekNavSurface,
                        border = BorderStroke(1.dp, SleekBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = SleekPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == "ar")
                                    "حماية خصوصية WASTA : المكالمات والرسائل مؤطرة وفق ميثاق الحرفيين والضمان."
                                else
                                    "Protection WASTA : Les échanges sont encadrés par la charte artisan et la garantie de prestation.",
                                fontSize = 10.sp,
                                color = SleekTextSecondary,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showContactDialog = false },
                    modifier = Modifier.testTag("button_close_contact_dialog")
                ) {
                    Text(
                        text = if (language == "ar") "إغلاق" else "Fermer",
                        fontWeight = FontWeight.Bold,
                        color = SleekPrimary
                    )
                }
            }
        )
    }
}

@Composable
fun ProfileStatItem(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = SleekPrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SleekNavy)
        Text(text = title, fontSize = 10.sp, color = SleekTextSecondary)
    }
}

@Composable
fun VerificationPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isVerified: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SleekSurface,
        border = BorderStroke(1.dp, if (isVerified) SleekPrimary.copy(alpha = 0.4f) else SleekBorder),
        modifier = Modifier
            .clickable(onClick = onClick)
            .widthIn(min = 140.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isVerified) SleekPrimaryContainer else SleekContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isVerified) SleekPrimary else SleekTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SleekTextPrimary)
                Text(text = subtitle, fontSize = 9.sp, color = SleekTextSecondary)
            }
        }
    }
}

@Composable
fun ServiceCategoryCard(
    category: ServiceCategoryItem,
    onSelectService: (ServiceItem) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        border = BorderStroke(1.dp, SleekBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = SleekPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = category.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SleekTextPrimary)
                        Text(text = "${category.services.size} prestations disponibles", fontSize = 11.sp, color = SleekTextSecondary)
                    }
                }
                Text(
                    text = "Dès ${category.priceStartingDA} DA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = category.description, fontSize = 12.sp, color = SleekTextSecondary)
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                category.services.forEach { service ->
                    ServiceItemRow(service = service, onSelect = { onSelectService(service) })
                }
            }
        }
    }
}

@Composable
fun ServiceItemRow(
    service: ServiceItem,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SleekBackground,
        border = BorderStroke(1.dp, SleekBorder.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = service.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = SleekTextPrimary
                    )
                    if (service.isPopular) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SleekGoldLight)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Populaire", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SleekGoldDark)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = service.description, fontSize = 11.sp, color = SleekTextSecondary, lineHeight = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("⏱️ ~${service.estimatedDuration}", fontSize = 10.sp, color = SleekTextSecondary)
                    Text("🛡️ Garantie ${service.warrantyDays}j", fontSize = 10.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.SemiBold)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${service.startingPriceDA} DA",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = SleekPrimary
                )
                Text(text = service.pricingModel, fontSize = 10.sp, color = SleekTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekPrimary)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Choisir", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CertificationCard(
    certification: ProfessionalCertification,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        border = BorderStroke(1.dp, SleekBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
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
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SleekTextPrimary
                        )
                        Text(
                            text = certification.issuer,
                            fontSize = 11.sp,
                            color = SleekTextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFDCFCE7))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Vérifié",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF166534)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = certification.description,
                fontSize = 12.sp,
                color = SleekTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Réf: ${certification.credentialNumber}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SleekPrimary
                )
                Text(
                    text = "Délivré en ${certification.issueYear}",
                    fontSize = 11.sp,
                    color = SleekTextSecondary
                )
            }
        }
    }
}

@Composable
fun RatingSummaryOverviewCard(ratingBreakdown: RatingBreakdown) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        border = BorderStroke(1.dp, SleekBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${ratingBreakdown.overallRating}",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = SleekNavy,
                        lineHeight = 44.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        (1..5).forEach { _ ->
                            Icon(Icons.Default.Star, contentDescription = null, tint = SleekGold, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${ratingBreakdown.totalReviewsCount} interventions vérifiées",
                        fontSize = 11.sp,
                        color = SleekTextSecondary
                    )
                }

                Column(
                    modifier = Modifier.weight(1f).padding(start = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    RatingProgressBarRow(label = "Ponctualité", score = ratingBreakdown.punctualityScore)
                    RatingProgressBarRow(label = "Qualité travail", score = ratingBreakdown.craftsmanshipScore)
                    RatingProgressBarRow(label = "Propreté", score = ratingBreakdown.cleanlinessScore)
                    RatingProgressBarRow(label = "Transparence prix", score = ratingBreakdown.pricingTransparencyScore)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = SleekBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ThumbUp, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${ratingBreakdown.recommendationRatePercent}% recommandent cet artisan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF166534)
                    )
                }
            }
        }
    }
}

@Composable
fun RatingProgressBarRow(label: String, score: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = SleekTextSecondary,
            modifier = Modifier.width(100.dp)
        )
        LinearProgressIndicator(
            progress = { (score / 5.0f).coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = SleekPrimary,
            trackColor = SleekContainer
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$score",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SleekNavy
        )
    }
}

@Composable
fun VerifiedCustomerReviewCard(
    customerName: String,
    date: String,
    serviceTitle: String,
    rating: Float,
    comment: String,
    verifiedProof: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SleekContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = customerName.take(1), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = date, fontSize = 10.sp, color = SleekTextSecondary)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = if (star <= rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = SleekGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SleekBackground)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = serviceTitle, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = SleekPrimary)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = comment, fontSize = 12.sp, color = SleekTextPrimary, lineHeight = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(13.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = verifiedProof, fontSize = 10.sp, color = Color(0xFF166534), fontWeight = FontWeight.Medium)
            }
        }
    }
}
