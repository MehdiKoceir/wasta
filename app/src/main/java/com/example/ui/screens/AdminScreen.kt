package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.ui.ScenarioTestResult
import com.example.ui.WastaViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminScreen(viewModel: WastaViewModel) {
    val allJobs by viewModel.allJobs.collectAsState()
    val allPros by viewModel.allProfessionals.collectAsState()
    val allRequests by viewModel.allRequests.collectAsState()
    val allInvoices by viewModel.allInvoices.collectAsState()
    val allDisputes by viewModel.allDisputes.collectAsState()
    val recentAuditLogs by viewModel.recentAuditLogs.collectAsState()
    val securityAuditTests by viewModel.securityAuditTests.collectAsState()

    val completedJobsCount = allJobs.count { it.status == "COMPLETED" }
    val activeJobsCount = allJobs.count { it.status == "IN_PROGRESS" || it.status == "SCHEDULED" }
    val totalRevenueDA = allInvoices.filter { it.status == "PAID" }.sumOf { it.totalDA }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WastaBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Platform Dashboard Overview
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimaryDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = SaharaGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Centre de Supervision & Sécurité WASTA?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(text = "Région DZ", fontSize = 12.sp, color = SaharaGoldLight)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        AdminStatBadge(label = "Artisans", value = "${allPros.size}")
                        AdminStatBadge(label = "En Cours", value = "$activeJobsCount")
                        AdminStatBadge(label = "Terminés", value = "$completedJobsCount")
                        AdminStatBadge(label = "Litiges", value = "${allDisputes.size}")
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFF1B4E38))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Volume total des transactions :", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        Text("$totalRevenueDA DA", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SaharaGold)
                    }
                }
            }
        }

        // Section 26: Security Audit Test Lab (Interactive Runner)
        item {
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
                        Column {
                            Text("Banc d'Essai de Sécurité (Section 26)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("Validation des 10 scénarios d'attaque et contrôles stricts", fontSize = 11.sp, color = WastaTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.runSecurityScenarioAudit() },
                        modifier = Modifier.fillMaxWidth().testTag("button_run_security_audit"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SaharaGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lancer l'Audit Automatisé des 10 Scénarios", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Render Security Scenario Results
        if (securityAuditTests.isNotEmpty()) {
            item {
                Text(
                    text = "Résultats de l'Audit de Sécurité (${securityAuditTests.count { it.passed }}/10 Validés) :",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
            }

            items(securityAuditTests) { test ->
                SecurityScenarioCard(test = test)
            }
        }

        // Section 13: Disputes Management
        if (allDisputes.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Gestion des Litiges et Réclamations", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            items(allDisputes) { dispute ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WastaSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Litige #${dispute.id}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(StatusCancelled.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(dispute.status, color = StatusCancelled, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Chantier lié : #${dispute.jobId}", fontSize = 12.sp, color = WastaTextSecondary)
                        Text("Motif : ${dispute.reason}", fontSize = 12.sp, fontWeight = FontWeight.Medium)

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.showNotification("Litige #${dispute.id} résolu après médiation.") },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Arbitrer & Clôturer", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Section 16 & 25: Live Security Audit Logs
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Journal d'Audit Immuable (Audit Logs)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("${recentAuditLogs.size} événements", fontSize = 12.sp, color = WastaTextSecondary)
            }
        }

        if (recentAuditLogs.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WastaSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(16.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Aucun événement d'audit enregistré.", fontSize = 12.sp, color = WastaTextSecondary)
                    }
                }
            }
        } else {
            items(recentAuditLogs.take(15)) { log ->
                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = WastaSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = log.action,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (log.securityLevel == "SECURITY_ALERT") StatusCancelled else EmeraldPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "[${log.actorRole}]", fontSize = 10.sp, color = WastaTextSecondary)
                            }
                            Text(text = log.details, fontSize = 11.sp, maxLines = 2)
                        }
                        Text(text = timeStr, fontSize = 10.sp, color = WastaTextSecondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
        Text(text = label, fontSize = 11.sp, color = SaharaGoldLight)
    }
}

@Composable
private fun SecurityScenarioCard(test: ScenarioTestResult) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = WastaSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = if (test.passed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = if (test.passed) EmeraldPrimary else StatusCancelled,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Scénario ${test.scenarioNumber} : ${test.title}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = WastaTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Attendu : ${test.expected}", fontSize = 11.sp, color = WastaTextSecondary)
                Text(text = "Résultat : ${test.actual}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (test.passed) EmeraldPrimary else StatusCancelled)
                Text(text = test.details, fontSize = 10.sp, color = EmeraldPrimaryDark)
            }
        }
    }
}
