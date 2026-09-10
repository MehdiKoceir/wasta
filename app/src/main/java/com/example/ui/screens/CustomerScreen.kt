package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ai.AiTriageResult
import com.example.data.model.*
import com.example.matching.MatchScoreBreakdown
import com.example.ui.WastaViewModel
import com.example.ui.theme.*

@Composable
fun CustomerScreen(viewModel: WastaViewModel) {
    val language by viewModel.currentLanguage.collectAsState()
    val problemInput by viewModel.problemInput.collectAsState()
    val selectedWilaya by viewModel.selectedWilaya.collectAsState()
    val selectedCommune by viewModel.selectedCommune.collectAsState()
    val exactAddress by viewModel.exactAddress.collectAsState()
    val triageResult by viewModel.triageResult.collectAsState()
    val matchedPros by viewModel.matchedPros.collectAsState()
    val selectedProBreakdown by viewModel.selectedProBreakdown.collectAsState()
    val allProfessionals by viewModel.allProfessionals.collectAsState()
    val allJobs by viewModel.allJobs.collectAsState()
    val allInvoices by viewModel.allInvoices.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val isSyncing by viewModel.isCloudSyncing.collectAsState()
    val lastSync by viewModel.lastCloudSyncTimestamp.collectAsState()

    var showAuthDialog by remember { mutableStateOf(false) }
    var showInvoiceDialog by remember { mutableStateOf(false) }
    var selectedInvoiceForPayment by remember { mutableStateOf<InvoiceEntity?>(null) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var selectedJobForReview by remember { mutableStateOf<JobEntity?>(null) }
    var reviewRating by remember { mutableFloatStateOf(5f) }
    var reviewComment by remember { mutableStateOf("") }
    var proCategoryFilter by remember { mutableStateOf("ALL") }
    var proSearchQuery by remember { mutableStateOf("") }
    var selectedCityFilter by remember { mutableStateOf("ALL") }

    val algeriaCitiesList = remember {
        listOf(
            Triple("ALL", "Toutes les villes (Algérie)", "كل المدن والولايات"),
            Triple("Alger", "Alger (16) - العاصمة", "الجزائر (16)"),
            Triple("Blida", "Blida (09) - البليدة", "البليدة (09)"),
            Triple("Oran", "Oran (31) - وهران", "وهران (31)"),
            Triple("Constantine", "Constantine (25) - قسنطينة", "قسنطينة (25)"),
            Triple("Sétif", "Sétif (19) - سطيف", "سطيف (19)"),
            Triple("Boumerdes", "Boumerdès (35) - بومرداس", "بومرداس (35)"),
            Triple("Tipaza", "Tipaza (42) - تيبازة", "تيبازة (42)"),
            Triple("Annaba", "Annaba (23) - عنابة", "عنابة (23)"),
            Triple("Tlemcen", "Tlemcen (13) - تلمسان", "تلمسان (13)"),
            Triple("Béjaïa", "Béjaïa (06) - بجاية", "بجاية (06)"),
            Triple("Batna", "Batna (05) - باتنة", "باتنة (05)"),
            Triple("Mostaganem", "Mostaganem (27) - مستغانم", "مستغانم (27)"),
            Triple("Mila", "Mila (43) - ميلة", "ميلة (43)"),
            Triple("Sidi Bel Abbes", "Sidi Bel Abbès (22) - بلعباس", "سيدي بلعباس (22)"),
            Triple("Chlef", "Chlef (02) - الشلف", "الشلف (02)"),
            Triple("Tizi Ouzou", "Tizi Ouzou (15) - تيزي وزو", "تيزي وزو (15)"),
            Triple("Biskra", "Biskra (07) - بسكرة", "بسكرة (07)")
        )
    }

    val filteredVerifiedPros = remember(allProfessionals, proCategoryFilter, proSearchQuery, selectedCityFilter) {
        allProfessionals.filter { pro ->
            val matchesCategory = when (proCategoryFilter) {
                "PLUMBING" -> pro.primaryCategory.contains("Plomb", ignoreCase = true) || pro.specialties.contains("Plomb", ignoreCase = true)
                "ELECTRICITY" -> pro.primaryCategory.contains("Électr", ignoreCase = true) || pro.specialties.contains("Electr", ignoreCase = true)
                "CLIMATE" -> pro.primaryCategory.contains("Clim", ignoreCase = true) || pro.specialties.contains("Clim", ignoreCase = true)
                "LOCKSMITH" -> pro.primaryCategory.contains("Serrur", ignoreCase = true) || pro.specialties.contains("Serrur", ignoreCase = true)
                else -> true
            }

            val matchesCity = if (selectedCityFilter == "ALL") {
                true
            } else {
                pro.baseWilaya.contains(selectedCityFilter, ignoreCase = true) ||
                pro.wilayasCovered.contains(selectedCityFilter, ignoreCase = true) ||
                pro.workshopAddress.contains(selectedCityFilter, ignoreCase = true) ||
                (selectedCityFilter.equals("Boumerdes", ignoreCase = true) && (pro.baseWilaya.contains("Boumerd", ignoreCase = true) || pro.wilayasCovered.contains("Boumerd", ignoreCase = true))) ||
                (selectedCityFilter.equals("Sétif", ignoreCase = true) && (pro.baseWilaya.contains("Setif", ignoreCase = true) || pro.wilayasCovered.contains("Setif", ignoreCase = true))) ||
                (selectedCityFilter.equals("Sidi Bel Abbes", ignoreCase = true) && (pro.baseWilaya.contains("Abbes", ignoreCase = true) || pro.wilayasCovered.contains("Abbes", ignoreCase = true)))
            }

            val query = proSearchQuery.trim()
            val matchesSearch = if (query.isBlank()) {
                true
            } else {
                pro.name.contains(query, ignoreCase = true) ||
                pro.primaryCategory.contains(query, ignoreCase = true) ||
                pro.specialties.contains(query, ignoreCase = true) ||
                pro.baseWilaya.contains(query, ignoreCase = true) ||
                pro.serviceCategories.contains(query, ignoreCase = true)
            }

            pro.profileVerified && matchesCategory && matchesCity && matchesSearch
        }
    }

    if (showAuthDialog) {
        FirebaseAuthDialog(viewModel = viewModel, onDismiss = { showAuthDialog = false })
    }

    val quickProblems = listOf(
        "Mon chauffe-eau fuit",
        "سخان الماء راه يقطر",
        "Ma machine à laver ne vide plus l'eau",
        "Mon climatiseur ne refroidit plus",
        "Court-circuit électrique dans le tableau",
        "Serrure bloquée / clé coincée"
    )

    val algerianWilayas = listOf("Blida", "Alger", "Oran", "Constantine", "Setif", "Boumerdes", "Tipaza")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WastaBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sleek Interface Greeting Section
        item {
            val greetingName = if (authState.isAuthenticated && authState.displayName.isNotBlank()) {
                authState.displayName.split(" ").firstOrNull() ?: authState.displayName
            } else {
                if (language == "ar") "أحمد" else "Ahmed"
            }

            Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp)) {
                Text(
                    text = if (language == "ar") "سلام، $greetingName." else "Salam, $greetingName.",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    color = SleekTextPrimary,
                    lineHeight = 34.sp
                )
                Text(
                    text = if (language == "ar") "ما الذي يمكننا القيام به من أجلك؟" else "Que pouvons-nous faire pour vous ?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = SleekTextSecondary,
                    lineHeight = 26.sp
                )
            }
        }

        // Search & Location Filter Section: Filter verified professionals by keyword and Algerian city
        item {
            var cityDropdownExpanded by remember { mutableStateOf(false) }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SleekSurface,
                border = BorderStroke(
                    1.dp,
                    if (proSearchQuery.isNotBlank() || selectedCityFilter != "ALL") SleekPrimary else SleekBorder
                ),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("top_search_bar_container")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // Keyword search input
                    OutlinedTextField(
                        value = proSearchQuery,
                        onValueChange = { proSearchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("top_search_bar_professionals"),
                        placeholder = {
                            Text(
                                text = if (language == "ar") "ابحث عن حرفي بالاسم أو التخصص (أحمد، سباكة...)"
                                else "Rechercher un artisan par nom ou catégorie (ex: Ahmed, Plomberie...)",
                                fontSize = 13.sp,
                                color = SleekTextSecondary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Recherche",
                                tint = if (proSearchQuery.isNotBlank()) SleekPrimary else SleekTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (proSearchQuery.isNotBlank()) {
                                IconButton(
                                    onClick = { proSearchQuery = "" },
                                    modifier = Modifier.testTag("button_clear_top_search")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Effacer la recherche",
                                        tint = SleekTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SleekPrimary.copy(alpha = 0.5f),
                            unfocusedBorderColor = SleekBorder,
                            focusedContainerColor = SleekBackground,
                            unfocusedContainerColor = SleekBackground,
                            focusedTextColor = SleekTextPrimary,
                            unfocusedTextColor = SleekTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 'Filter by City' Dropdown Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Dropdown menu button
                        Box(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (selectedCityFilter != "ALL") SleekPrimary.copy(alpha = 0.08f) else SleekBackground,
                                border = BorderStroke(
                                    1.dp,
                                    if (selectedCityFilter != "ALL") SleekPrimary else SleekBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { cityDropdownExpanded = true }
                                    .testTag("dropdown_filter_by_city")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = "Filtrer par ville",
                                            tint = if (selectedCityFilter != "ALL") SleekPrimary else SleekTextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        val selectedCityEntry = algeriaCitiesList.find { it.first == selectedCityFilter }
                                        Text(
                                            text = if (selectedCityFilter == "ALL") {
                                                if (language == "ar") "تصفية حسب المدينة: كل الجزائر"
                                                else "Filtrer par ville : Toute l'Algérie"
                                            } else {
                                                if (language == "ar") "المدينة: ${selectedCityEntry?.third ?: selectedCityFilter}"
                                                else "Ville : ${selectedCityEntry?.second ?: selectedCityFilter}"
                                            },
                                            fontSize = 12.sp,
                                            fontWeight = if (selectedCityFilter != "ALL") FontWeight.Bold else FontWeight.Medium,
                                            color = if (selectedCityFilter != "ALL") SleekPrimary else SleekTextPrimary,
                                            maxLines = 1
                                        )
                                    }

                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Ouvrir menu des villes",
                                        tint = if (selectedCityFilter != "ALL") SleekPrimary else SleekTextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = cityDropdownExpanded,
                                onDismissRequest = { cityDropdownExpanded = false },
                                modifier = Modifier
                                    .widthIn(min = 260.dp, max = 320.dp)
                                    .background(SleekSurface)
                                    .testTag("menu_cities_dropdown")
                            ) {
                                Text(
                                    text = if (language == "ar") "اختر ولاية / مدينة في الجزائر" else "SÉLECTIONNEZ UNE VILLE (ALGÉRIE)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekTextSecondary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                HorizontalDivider(color = SleekBorder)

                                algeriaCitiesList.forEach { (codeOrKey, labelFr, labelAr) ->
                                    val isSelected = selectedCityFilter == codeOrKey
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = if (codeOrKey == "ALL") Icons.Default.Public else Icons.Default.LocationCity,
                                                        contentDescription = null,
                                                        tint = if (isSelected) SleekPrimary else SleekTextSecondary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text(
                                                        text = if (language == "ar") labelAr else labelFr,
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isSelected) SleekPrimary else SleekTextPrimary
                                                    )
                                                }
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = SleekPrimary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            selectedCityFilter = codeOrKey
                                            cityDropdownExpanded = false
                                        },
                                        modifier = Modifier.testTag("city_item_$codeOrKey")
                                    )
                                }
                            }
                        }

                        // Reset city button if a city filter is currently active
                        if (selectedCityFilter != "ALL") {
                            IconButton(
                                onClick = { selectedCityFilter = "ALL" },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(SleekContainer)
                                    .testTag("button_reset_city_filter")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Réinitialiser le filtre ville",
                                    tint = SleekTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Firebase Auth & Cloud Firestore Sync Card Banner
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (authState.isAuthenticated) Color(0xFFF0FDF4) else SleekContainer,
                border = BorderStroke(1.dp, if (authState.isAuthenticated) Color(0xFFBBF7D0) else SleekBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAuthDialog = true }
                    .testTag("banner_firebase_firestore_sync")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (authState.isAuthenticated) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (authState.isAuthenticated) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                                contentDescription = null,
                                tint = if (authState.isAuthenticated) Color(0xFF16A34A) else SleekPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (authState.isAuthenticated) "Firebase Auth & Firestore" else "Connexion & Sauvegarde Cloud",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekNavy
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (authState.isAuthenticated) Color(0xFF22C55E) else Color(0xFFF59E0B))
                                )
                            }
                            Text(
                                text = if (authState.isAuthenticated) {
                                    "${authState.displayName} • ${authState.provider}"
                                } else {
                                    if (language == "ar") "سجل الدخول بحساب Google لحفظ طلباتك" else "Google Sign-in pour synchroniser vos données"
                                },
                                fontSize = 11.sp,
                                color = SleekTextSecondary,
                                maxLines = 1
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = { showAuthDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("button_open_auth_hub")
                    ) {
                        Text(
                            text = if (authState.isAuthenticated) "Gérer" else "Connexion",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Sleek Interface Main Problem Input Card (Section 3)
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = SleekContainer),
                border = BorderStroke(1.dp, SleekBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    OutlinedTextField(
                        value = problemInput,
                        onValueChange = { viewModel.setProblemInput(it) },
                        placeholder = {
                            Text(
                                if (language == "ar") "صِف مشكلتك هنا... (مثال: سخان الماء تاعي راه يقطر من تحت)"
                                else "Décrivez votre problème... (ex: Mon chauffe-eau fuit depuis ce matin...)",
                                color = SleekTextSecondary,
                                fontSize = 15.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_problem_description"),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SleekBackground,
                            unfocusedContainerColor = SleekBackground,
                            focusedBorderColor = SleekPrimary,
                            unfocusedBorderColor = SleekBorder,
                            focusedTextColor = SleekTextPrimary,
                            unfocusedTextColor = SleekTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Location Picker with sleek rounded styling
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Wilaya 📍", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SleekTextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            var wilayaExpanded by remember { mutableStateOf(false) }
                            Box {
                                OutlinedButton(
                                    onClick = { wilayaExpanded = true },
                                    modifier = Modifier.fillMaxWidth().testTag("select_wilaya_button"),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = SleekBackground),
                                    border = BorderStroke(1.dp, SleekBorder)
                                ) {
                                    Text(selectedWilaya, maxLines = 1, color = SleekTextPrimary, fontSize = 13.sp)
                                }
                                DropdownMenu(
                                    expanded = wilayaExpanded,
                                    onDismissRequest = { wilayaExpanded = false }
                                ) {
                                    algerianWilayas.forEach { w ->
                                        DropdownMenuItem(
                                            text = { Text(w) },
                                            onClick = {
                                                viewModel.setLocation(w, selectedCommune)
                                                wilayaExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Commune", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SleekTextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = selectedCommune,
                                onValueChange = { viewModel.setLocation(selectedWilaya, it) },
                                singleLine = true,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SleekBackground,
                                    unfocusedContainerColor = SleekBackground,
                                    focusedBorderColor = SleekPrimary,
                                    unfocusedBorderColor = SleekBorder,
                                    focusedTextColor = SleekTextPrimary,
                                    unfocusedTextColor = SleekTextPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Lock, contentDescription = null, tint = SleekPrimary, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Confidentialité : adresse exacte masquée jusqu'à l'acceptation de l'artisan.",
                            fontSize = 10.sp,
                            color = SleekTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sleek Interface Action Row: [Camera] [Mic] [Trouver / Classifier ->]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Camera attachment button
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(SleekBackground)
                                    .border(1.dp, SleekBorder, CircleShape)
                                    .clickable {
                                        viewModel.showNotification("Photo jointe pour analyse du problème.")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PhotoCamera,
                                    contentDescription = "Prendre une photo",
                                    tint = SleekPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Voice mic button
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(SleekBackground)
                                    .border(1.dp, SleekBorder, CircleShape)
                                    .clickable {
                                        viewModel.showNotification("🎙️ Saisie vocale active : Darija / Arabe / Français")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Message vocal",
                                    tint = SleekPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Sleek Primary Action Button "Trouver"
                        Button(
                            onClick = { viewModel.analyzeAndTriageProblem() },
                            modifier = Modifier
                                .testTag("button_analyze_and_match")
                                .height(46.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                            contentPadding = PaddingValues(horizontal = 22.dp)
                        ) {
                            Text(
                                text = if (language == "ar") "إيجاد المهني" else "Trouver",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Sleek Interface: Quick Suggestions Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (language == "ar") "اقتراحات سريعة" else "SUGGESTIONS RAPIDES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekTextSecondary,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                val sleekSuggestions = listOf(
                    "Panne d'électricité",
                    "Installation Clim",
                    "Fuite Chauffe-eau",
                    "Réparation PC",
                    "Peinture",
                    "Serrure bloquée"
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sleekSuggestions) { suggestion ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(SleekBackground)
                                .border(1.dp, SleekBorder, RoundedCornerShape(20.dp))
                                .clickable {
                                    viewModel.setProblemInput(suggestion)
                                    viewModel.analyzeAndTriageProblem(suggestion)
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = suggestion,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = SleekTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Sleek Interface: Verified Pros Online Status Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SleekPrimaryContainer),
                border = BorderStroke(1.dp, SleekPrimary.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == "ar") "42 مهني معتمد متصل الآن" else "42 professionnels vérifiés en ligne",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SleekNavy
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$selectedWilaya, DZ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekPrimary
                        )
                    }
                }
            }
        }

        // Section 4: AI Service Triage Output Card
        if (triageResult != null) {
            item {
                TriageResultCard(triageResult = triageResult!!, language = language)
            }
        }

        // Section 5: Smart Matching Engine Results
        if (matchedPros.isNotEmpty()) {
            item {
                Text(
                    text = if (language == "ar") "المهنيون المطابقون لطلبك" else "Artisans Vérifiés Compatibles",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WastaTextPrimary
                )
            }

            items(matchedPros) { breakdown ->
                MatchedProCard(
                    breakdown = breakdown,
                    isSelected = selectedProBreakdown?.professional?.id == breakdown.professional.id,
                    onSelect = { viewModel.selectPro(breakdown) },
                    onBook = { viewModel.submitServiceRequestAndBook() },
                    onViewProfile = { viewModel.openVerifiedProfileForPro(breakdown.professional) }
                )
            }
        }

        // Section 5B: Verified Professional Profiles Vertical List View
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (language == "ar") "الحرفيون والمهنيون المعتمدون" else "Artisans & Professionnels Vérifiés",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekNavy
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SleekPrimaryContainer,
                            border = BorderStroke(1.dp, SleekPrimary.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = "${filteredVerifiedPros.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekPrimary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == "ar") "اعتمادات رسمية" else "100% Agréés CAM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Active search / location filter indicator banner
                if (proSearchQuery.isNotBlank() || selectedCityFilter != "ALL") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val activeCityLabel = algeriaCitiesList.find { it.first == selectedCityFilter }?.second
                        val bannerText = buildString {
                            if (language == "ar") {
                                append("نتائج التصفية: ")
                                if (proSearchQuery.isNotBlank()) append("\"$proSearchQuery\" ")
                                if (selectedCityFilter != "ALL") append("📍 ${activeCityLabel ?: selectedCityFilter} ")
                                append("(${filteredVerifiedPros.size})")
                            } else {
                                append("Résultats : ")
                                if (proSearchQuery.isNotBlank()) append("\"$proSearchQuery\" ")
                                if (selectedCityFilter != "ALL") append("📍 ${activeCityLabel ?: selectedCityFilter} ")
                                append("(${filteredVerifiedPros.size})")
                            }
                        }
                        Text(
                            text = bannerText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SleekPrimary
                        )
                        Text(
                            text = if (language == "ar") "مسح الكل" else "Effacer",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekTextSecondary,
                            modifier = Modifier
                                .clickable {
                                    proSearchQuery = ""
                                    selectedCityFilter = "ALL"
                                }
                                .padding(4.dp)
                                .testTag("button_clear_search_text")
                        )
                    }
                }

                // Filter chips row
                val categoryFilters = listOf(
                    Triple("ALL", "Tous", "الكل"),
                    Triple("PLUMBING", "Plomberie", "سباكة"),
                    Triple("ELECTRICITY", "Électricité", "كهرباء"),
                    Triple("CLIMATE", "Climatisation", "تكييف"),
                    Triple("LOCKSMITH", "Serrurerie", "أقفال")
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categoryFilters) { (filterKey, labelFr, labelAr) ->
                        val isSelected = proCategoryFilter == filterKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { proCategoryFilter = filterKey },
                            label = {
                                Text(
                                    text = if (language == "ar") labelAr else labelFr,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SleekPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = SleekContainer,
                                labelColor = SleekTextPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = SleekBorder,
                                selectedBorderColor = SleekPrimary
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("filter_chip_$filterKey")
                        )
                    }
                }
            }
        }

        // Empty state if search or category filters yield no results
        if (filteredVerifiedPros.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SleekSurface,
                    border = BorderStroke(1.dp, SleekBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("empty_verified_pros_state")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SleekContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = SleekTextSecondary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (language == "ar") "لا يوجد حرفي مطابق لبحثك" else "Aucun artisan trouvé",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = SleekNavy
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (proSearchQuery.isNotBlank()) {
                                if (language == "ar") "لا توجد نتائج تطابق \"$proSearchQuery\"."
                                else "Aucun résultat ne correspond à \"$proSearchQuery\"."
                            } else if (selectedCityFilter != "ALL") {
                                val city = algeriaCitiesList.find { it.first == selectedCityFilter }?.second ?: selectedCityFilter
                                if (language == "ar") "لا يوجد حرفي مسجل حالياً في $city."
                                else "Aucun artisan vérifié disponible actuellement pour $city."
                            } else {
                                if (language == "ar") "لا يوجد حرفيون معتمدون حالياً في هذه الفئة."
                                else "Aucun artisan vérifié disponible pour cette catégorie."
                            },
                            fontSize = 12.sp,
                            color = SleekTextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = {
                                proSearchQuery = ""
                                proCategoryFilter = "ALL"
                                selectedCityFilter = "ALL"
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SleekPrimary),
                            modifier = Modifier.testTag("button_reset_pro_filters")
                        ) {
                            Text(
                                text = if (language == "ar") "إعادة تعيين الفلاتر" else "Réinitialiser les filtres",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SleekPrimary
                            )
                        }
                    }
                }
            }
        }

        // Vertical list view items for verified professional profiles
        items(filteredVerifiedPros, key = { it.id }) { pro ->
            VerifiedProfessionalCard(
                pro = pro,
                language = language,
                onOpenProfile = { viewModel.openVerifiedProfileForPro(pro, initialTab = 0) },
                onViewReviews = { viewModel.openVerifiedProfileForPro(pro, initialTab = 2) },
                onContact = {
                    viewModel.showNotification(
                        if (language == "ar") "الاتصال بـ ${pro.name} (${pro.phone})"
                        else "Appel direct vers ${pro.name} (${pro.phone})"
                    )
                }
            )
        }

        // Section 7, 8, 9, 11: Active Jobs Tracking for Customer
        val customerJobs = allJobs.filter { it.customerId == "usr_cust_mehdi" }
        if (customerJobs.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (language == "ar") "متابعة الطلبات المباشرة" else "Vos Interventions en Cours",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WastaTextPrimary
                )
            }

            items(customerJobs) { job ->
                CustomerJobTrackingCard(
                    job = job,
                    invoices = allInvoices,
                    onPay = { inv ->
                        selectedInvoiceForPayment = inv
                        showInvoiceDialog = true
                    },
                    onReview = {
                        selectedJobForReview = job
                        showReviewDialog = true
                    },
                    onDispute = { viewModel.reportDispute(job.id, "Dispute reported on service quality") }
                )
            }
        }
    }

    // Official Invoice & Payment Dialog
    if (showInvoiceDialog && selectedInvoiceForPayment != null) {
        val inv = selectedInvoiceForPayment!!
        AlertDialog(
            onDismissRequest = { showInvoiceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Facture Numérique Officielle WASTA?")
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Réf: #${inv.id}", fontSize = 11.sp, color = WastaTextSecondary)
                    Text(text = "Artisan: ${inv.professionalName}", fontWeight = FontWeight.Bold)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Main d'œuvre certifiée :")
                        Text("${inv.laborDA} DA", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pièces & Fournitures :")
                        Text("${inv.materialsDA} DA", fontWeight = FontWeight.SemiBold)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TOTAL À PAYER :", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("${inv.totalDA} DA", fontWeight = FontWeight.Black, fontSize = 16.sp, color = EmeraldPrimary)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Mode de règlement sécurisé :", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.payInvoice(inv, "CASH_ON_DELIVERY")
                            showInvoiceDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("💵 Payer en Espèces après vérification")
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.payInvoice(inv, "BARIDIMOB")
                            showInvoiceDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("💳 BaridiMob / CIB Algérie Poste (RIP)")
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Security Test Button: Scenario 4 Price Tampering Test
                    TextButton(
                        onClick = {
                            viewModel.payInvoice(inv, "CASH_ON_DELIVERY", tamperedAmount = 100)
                            showInvoiceDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🧪 Tester falsification prix client (ex: 100 DA)", fontSize = 10.sp, color = StatusCancelled)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showInvoiceDialog = false }) { Text("Fermer") }
            }
        )
    }

    // Verified Review Dialog
    if (showReviewDialog && selectedJobForReview != null) {
        val job = selectedJobForReview!!
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = SaharaGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Évaluation Certifiée")
                }
            },
            text = {
                Column {
                    Text("Votre avis est garanti authentique car rattaché à l'intervention #${job.id} achevée et payée.", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { reviewRating = star.toFloat() }) {
                                Icon(
                                    imageVector = if (star <= reviewRating) Icons.Default.Star else Icons.Outlined.StarOutline,
                                    contentDescription = "$star stars",
                                    tint = SaharaGold,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        placeholder = { Text("Détaillez la ponctualité, la propreté du travail...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitReview(job.id, reviewRating, reviewComment)
                        showReviewDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Publier l'avis certifié")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
fun TriageResultCard(triageResult: AiTriageResult, language: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
        modifier = Modifier.fillMaxWidth().testTag("card_ai_triage_result")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = EmeraldPrimaryDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Diagnostic IA & Classification",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnEmeraldContainer
                    )
                }
                UrgencyBadge(urgency = triageResult.urgency)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = triageResult.service,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = OnEmeraldContainer
            )
            Text(
                text = "Spécialité requise : ${triageResult.specialtyNeeded}",
                fontSize = 13.sp,
                color = EmeraldPrimaryDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Detected problems list
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                triageResult.detectedProblems.forEach { prob ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = prob, fontSize = 12.sp, color = OnEmeraldContainer)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.7f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tarif indicatif estimé :", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${triageResult.estimatedPriceMinDA} – ${triageResult.estimatedPriceMaxDA} DA",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Safety notice
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.Info, contentDescription = null, tint = SaharaGoldDark, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = triageResult.safetyAdvice, fontSize = 11.sp, color = Color(0xFF4A3408))
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Crucial Medical/Diagnostic Disclaimer
            Text(
                text = "ℹ️ ${triageResult.triageDisclaimer}",
                fontSize = 10.sp,
                color = OnEmeraldContainer.copy(alpha = 0.8f),
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
fun UrgencyBadge(urgency: String) {
    val (bgColor, textColor, label) = when (urgency.lowercase()) {
        "emergency" -> Triple(StatusCancelled, Color.White, "🚨 Urgence Critique")
        "high" -> Triple(StatusPending, Color.White, "⚡ Priorité Haute")
        else -> Triple(EmeraldPrimary, Color.White, "Standard")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

@Composable
fun MatchedProCard(
    breakdown: MatchScoreBreakdown,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onBook: () -> Unit,
    onViewProfile: () -> Unit
) {
    val pro = breakdown.professional
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WastaSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) EmeraldPrimary else WastaBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onSelect)
            .testTag("card_pro_${pro.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable(onClick = onViewProfile),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = pro.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, contentDescription = "Agréé", tint = SleekPrimary, modifier = Modifier.size(15.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = SaharaGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = "${pro.rating}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = " (${pro.completedJobsCount} interventions)", fontSize = 12.sp, color = WastaTextSecondary)
                        }
                    }
                }

                // Match Score Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldPrimary)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${breakdown.totalScorePercent}% MATCH",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Badges
            VerificationBadgesRow(pro = pro)

            Spacer(modifier = Modifier.height(10.dp))

            // Transparent scoring explanation
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(WastaSurfaceVariant)
                    .padding(10.dp)
            ) {
                Text("Score de compatibilité transparent :", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Spécialité : ${breakdown.specialtyScore}/30", fontSize = 11.sp)
                    Text("Distance : ${breakdown.geographicScore}/20", fontSize = 11.sp)
                    Text("Disponibilité : ${breakdown.availabilityScore}/15", fontSize = 11.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Avis vérifiés : ${breakdown.ratingScore}/15", fontSize = 11.sp)
                    Text("Expérience : ${breakdown.experienceScore}/10", fontSize = 11.sp)
                    Text("Réactivité : ${breakdown.responseTimeScore}/5", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• " + breakdown.matchReasons.joinToString("\n• "),
                    fontSize = 11.sp,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Zone d'intervention :", fontSize = 11.sp, color = WastaTextSecondary)
                    Text("${pro.baseWilaya} & environs", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("Délai réponse moyen : ~${pro.avgResponseMinutes} min", fontSize = 11.sp, color = EmeraldPrimary)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onViewProfile,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("button_view_profile_${pro.id}")
                    ) {
                        Icon(Icons.Outlined.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Profil", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onBook,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.testTag("button_book_pro_${pro.id}")
                    ) {
                        Text("Prendre RDV", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerJobTrackingCard(
    job: JobEntity,
    invoices: List<InvoiceEntity>,
    onPay: (InvoiceEntity) -> Unit,
    onReview: () -> Unit,
    onDispute: () -> Unit
) {
    val invoice = invoices.find { it.jobId == job.id }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WastaSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Intervention #${job.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                JobStatusChip(status = job.status)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Artisan assigné : ${job.professionalName}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "Créneau : ${job.scheduledDate} (${job.scheduledTimeSlot})", fontSize = 12.sp, color = WastaTextSecondary)

            Spacer(modifier = Modifier.height(10.dp))

            // Proof photos inspection (Before / After)
            if (job.beforePhotoDesc != null || job.afterPhotoDesc != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(WastaSurfaceVariant)
                        .padding(8.dp)
                ) {
                    Text("Preuves de travail vérifiées :", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    if (job.beforePhotoDesc != null) {
                        Text("📸 Avant : ${job.beforePhotoDesc}", fontSize = 11.sp)
                    }
                    if (job.afterPhotoDesc != null) {
                        Text("📸 Après : ${job.afterPhotoDesc}", fontSize = 11.sp, color = EmeraldPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Action rows based on lifecycle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (invoice != null && invoice.status == "PENDING") {
                    Button(
                        onClick = { onPay(invoice) },
                        colors = ButtonDefaults.buttonColors(containerColor = SaharaGoldDark),
                        modifier = Modifier.weight(1f).testTag("button_pay_invoice")
                    ) {
                        Text("Régler Facture (${invoice.totalDA} DA)", fontSize = 12.sp, color = Color.White)
                    }
                } else if (invoice != null && invoice.status == "PAID") {
                    Button(
                        onClick = onReview,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.weight(1f).testTag("button_leave_review")
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Laisser un avis certifié", fontSize = 12.sp)
                    }
                }

                OutlinedButton(
                    onClick = onDispute,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Litige", fontSize = 12.sp, color = StatusCancelled)
                }
            }
        }
    }
}

@Composable
fun JobStatusChip(status: String) {
    val (bg, label) = when (status) {
        "SCHEDULED" -> Pair(StatusActive, "RDV Planifié")
        "IN_PROGRESS" -> Pair(SaharaGoldDark, "En Cours")
        "COMPLETED" -> Pair(EmeraldPrimary, "Terminé ✓")
        "DISPUTED" -> Pair(StatusCancelled, "En Litige")
        else -> Pair(Color.Gray, status)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun VerifiedProProfileOverviewCard(
    pro: ProfessionalEntity,
    onOpenProfile: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        border = BorderStroke(1.dp, SleekBorder),
        modifier = Modifier
            .width(230.dp)
            .clickable(onClick = onOpenProfile)
            .testTag("overview_card_pro_${pro.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(42.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SleekPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pro.name.take(2).uppercase(),
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = SleekNavy
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E))
                            .border(1.5.dp, Color.White, CircleShape)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SleekGoldLight)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = SleekGoldDark, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "${pro.rating}", fontWeight = FontWeight.Black, fontSize = 12.sp, color = SleekGoldDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = pro.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SleekTextPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.Verified, contentDescription = "Agréé", tint = SleekPrimary, modifier = Modifier.size(15.dp))
            }

            Text(
                text = pro.primaryCategory,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = SleekPrimary,
                maxLines = 1
            )
            Text(
                text = "${pro.baseWilaya} • ${pro.yearsExperience} ans d'exp.",
                fontSize = 11.sp,
                color = SleekTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))
            // Certification preview tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFEFF6FF))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "🎖️ Agrément CAM Vérifié",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = SleekPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Tarif indicatif", fontSize = 9.sp, color = SleekTextSecondary)
                    Text("${pro.priceMinDA} DA", fontSize = 12.sp, fontWeight = FontWeight.Black, color = SleekNavy)
                }

                Button(
                    onClick = onOpenProfile,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Voir Profil", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Vertical list view card component for verified professional profiles on the home screen.
 * Displays professional name, service category, verification badge, performance metrics,
 * and profile navigation actions.
 */
@Composable
fun VerifiedProfessionalCard(
    pro: ProfessionalEntity,
    language: String,
    onOpenProfile: () -> Unit,
    onViewReviews: () -> Unit = onOpenProfile,
    onContact: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SleekSurface),
        border = BorderStroke(1.dp, SleekBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenProfile)
            .testTag("verified_pro_card_${pro.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Avatar + Pro Name + Verification Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circle with initials and online dot
                Box(modifier = Modifier.size(48.dp)) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SleekPrimaryContainer)
                            .border(1.5.dp, SleekPrimary.copy(alpha = 0.25f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pro.name.take(2).uppercase(),
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = SleekPrimary
                        )
                    }
                    if (pro.isAvailable) {
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Professional Name & Verification Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = pro.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekNavy,
                            maxLines = 1,
                            modifier = Modifier.weight(1f, fill = false).testTag("pro_name_${pro.id}")
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Verification Badge (Prominent)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFECFDF5),
                            border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                            modifier = Modifier.testTag("verification_badge_${pro.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Vérifié",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (language == "ar") "معتمد" else "Vérifié",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Service Category
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SleekPrimaryContainer,
                            modifier = Modifier.testTag("service_category_${pro.id}")
                        ) {
                            Text(
                                text = pro.primaryCategory,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SleekPrimary,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                maxLines = 1
                            )
                        }

                        Text(
                            text = "•",
                            fontSize = 11.sp,
                            color = SleekTextSecondary
                        )

                        Text(
                            text = "${pro.baseWilaya}, DZ",
                            fontSize = 11.sp,
                            color = SleekTextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics row: Star Rating (Clickable to read reviews) + Experience + Response Time
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SleekBackground)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Star-Rating Feature: Average Rating & Reviews Counter
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SleekGoldLight,
                    border = BorderStroke(1.dp, SleekGoldDark.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .clickable(onClick = onViewReviews)
                        .testTag("pro_card_rating_button_${pro.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Note moyenne",
                            tint = SleekGoldDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${pro.rating}",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = SleekGoldDark
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(${pro.completedJobsCount} ${if (language == "ar") "تقييم" else "avis"})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SleekGoldDark
                        )
                    }
                }

                // Experience
                Text(
                    text = "${pro.yearsExperience} ${if (language == "ar") "سنوات خبرة" else "ans d'exp."}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = SleekTextSecondary
                )

                // Response Speed
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = SleekPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "~${pro.avgResponseMinutes} min",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SleekNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom: Indicative Price & Call-to-Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (language == "ar") "السعر التقديري" else "Tarif indicatif",
                        fontSize = 10.sp,
                        color = SleekTextSecondary
                    )
                    Text(
                        text = "${pro.priceMinDA} - ${pro.priceMaxDA} DA",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = SleekNavy
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Contact button (touch target >= 48dp)
                    OutlinedButton(
                        onClick = onContact,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, SleekBorder),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = SleekBackground),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("button_contact_pro_${pro.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Contacter",
                            tint = SleekPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == "ar") "اتصال" else "Appel",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SleekNavy
                        )
                    }

                    // View Profile Button (touch target >= 48dp)
                    Button(
                        onClick = onOpenProfile,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("button_view_profile_${pro.id}")
                    ) {
                        Text(
                            text = if (language == "ar") "الملف" else "Voir Profil",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
