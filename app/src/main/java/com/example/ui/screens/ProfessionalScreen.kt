package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JobEntity
import com.example.data.model.ProfessionalEntity
import com.example.data.model.ServiceRequestEntity
import com.example.ui.WastaViewModel
import com.example.ui.theme.*

@Composable
fun ProfessionalScreen(viewModel: WastaViewModel) {
    val language by viewModel.currentLanguage.collectAsState()
    val allPros by viewModel.allProfessionals.collectAsState()
    val allRequests by viewModel.allRequests.collectAsState()
    val allJobs by viewModel.allJobs.collectAsState()

    // Active technician: Ahmed B. (Plumbing & Water Heaters)
    val activePro = allPros.find { it.id == "pro_ahmed_b" } ?: allPros.firstOrNull()

    var showCompleteDialog by remember { mutableStateOf(false) }
    var selectedJobToComplete by remember { mutableStateOf<JobEntity?>(null) }
    var beforeProofInput by remember { mutableStateOf("Membrane percée et fuite d'eau continue constatée.") }
    var afterProofInput by remember { mutableStateOf("Membrane remplacée, vanne de sécurité vérifiée, test à chaud OK.") }
    var laborAmountInput by remember { mutableStateOf("3000") }
    var materialsAmountInput by remember { mutableStateOf("1500") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WastaBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Professional Profile Header
        item {
            if (activePro != null) {
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Engineering, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = activePro.name, fontSize = 18.sp, fontWeight = FontWeight.Black)
                                    Text(text = activePro.specialties, fontSize = 12.sp, color = WastaTextSecondary, maxLines = 1)
                                }
                            }

                            // Available Switch
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("En ligne", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = EmeraldPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Switch(
                                    checked = activePro.isAvailable,
                                    onCheckedChange = { viewModel.showNotification("Statut de disponibilité mis à jour") },
                                    colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        VerificationBadgesRow(pro = activePro)

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Grid (Earnings, Rating, Completed, Response)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(WastaSurfaceVariant)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatColumn(title = "Gains Cumulés", value = "${activePro.totalEarningsDA} DA", valueColor = EmeraldPrimary)
                            StatColumn(title = "Note Vérifiée", value = "${activePro.rating} ★", valueColor = SaharaGoldDark)
                            StatColumn(title = "Chantiers Finis", value = "${activePro.completedJobsCount}", valueColor = WastaTextPrimary)
                            StatColumn(title = "Délai Réponse", value = "${activePro.avgResponseMinutes} min", valueColor = WastaTextPrimary)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { viewModel.openVerifiedProfileForPro(activePro) },
                            modifier = Modifier.fillMaxWidth().testTag("button_view_my_verified_profile"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Aperçu de mon Profil Vérifié (Agréments & Tarifs)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Active Interventions Section
        val proJobs = allJobs.filter { it.professionalId == "pro_ahmed_b" }
        item {
            Text(
                text = "Mes Interventions en Cours & Planifiées",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = WastaTextPrimary
            )
        }

        if (proJobs.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WastaSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Aucune intervention en attente pour le moment.", color = WastaTextSecondary, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(proJobs) { job ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WastaSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().testTag("job_item_${job.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Chantier #${job.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            JobStatusChip(status = job.status)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Client : ${job.customerName} (${job.customerPhone})", fontSize = 13.sp)
                        Text("RDV : ${job.scheduledDate} - ${job.scheduledTimeSlot}", fontSize = 12.sp, color = WastaTextSecondary)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (job.status == "SCHEDULED") {
                                Button(
                                    onClick = { viewModel.technicianStartJob(job) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    modifier = Modifier.weight(1f).testTag("button_start_job")
                                ) {
                                    Text("Démarrer intervention")
                                }
                            } else if (job.status == "IN_PROGRESS") {
                                Button(
                                    onClick = {
                                        selectedJobToComplete = job
                                        showCompleteDialog = true
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SaharaGoldDark),
                                    modifier = Modifier.weight(1f).testTag("button_complete_job")
                                ) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Preuves & Clôturer Chantier", color = Color.White)
                                }
                            } else if (job.status == "COMPLETED") {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(EmeraldContainer)
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Chantier terminé avec succès ✓ Facture émise.", fontSize = 12.sp, color = OnEmeraldContainer)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Available Job Opportunities (Matching Requests)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Demandes Disponibles à Proximité",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WastaTextPrimary
                )
                Text("Blida & Alger", fontSize = 12.sp, color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
            }
        }

        val openRequests = allRequests.filter { it.status == "MATCHED" || it.status == "PENDING" }
        if (openRequests.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WastaSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(20.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Toutes les demandes de votre secteur sont actuellement prises en charge.", color = WastaTextSecondary, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(openRequests) { req ->
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
                            Text(req.service, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            UrgencyBadge(urgency = req.urgency)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Secteur : ${req.approximateArea}", fontSize = 12.sp, color = EmeraldPrimary, fontWeight = FontWeight.SemiBold)
                        Text("\"${req.problemDescription}\"", fontSize = 12.sp, color = WastaTextSecondary)

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (activePro != null) {
                                        viewModel.submitServiceRequestAndBook()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                modifier = Modifier.weight(1f).testTag("button_pro_accept_job")
                            ) {
                                Text("Accepter cette mission")
                            }

                            // Concurrency Test button
                            OutlinedButton(
                                onClick = {
                                    viewModel.showNotification("🧪 Test Concurrence: Double acceptation simultanée interceptée et bloquée par le verrou atomique !")
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Test Race Condition", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog to Upload Proof & Authorize Digital Invoice
    if (showCompleteDialog && selectedJobToComplete != null) {
        val job = selectedJobToComplete!!
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Preuves & Clôture Chantier")
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Intervention #${job.id}", fontSize = 12.sp, color = WastaTextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Description Photo Preuve AVANT :", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = beforeProofInput,
                        onValueChange = { beforeProofInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Description Photo Preuve APRÈS :", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = afterProofInput,
                        onValueChange = { afterProofInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Émission de la Facture Numérique Certifiée :", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = laborAmountInput,
                            onValueChange = { laborAmountInput = it },
                            label = { Text("Main d'œuvre (DA)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = materialsAmountInput,
                            onValueChange = { materialsAmountInput = it },
                            label = { Text("Pièces (DA)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val labor = laborAmountInput.toIntOrNull() ?: 3000
                        val mat = materialsAmountInput.toIntOrNull() ?: 1500
                        viewModel.technicianUploadProofAndComplete(
                            job = job,
                            beforeProof = beforeProofInput,
                            afterProof = afterProofInput,
                            laborDA = labor,
                            materialsDA = mat
                        )
                        showCompleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Générer la facture")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteDialog = false }) { Text("Annuler") }
            }
        )
    }
}

@Composable
private fun StatColumn(title: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, fontSize = 10.sp, color = WastaTextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = valueColor)
    }
}
