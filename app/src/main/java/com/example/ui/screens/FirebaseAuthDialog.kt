package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.WastaViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FirebaseAuthDialog(
    viewModel: WastaViewModel,
    onDismiss: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val isSyncing by viewModel.isCloudSyncing.collectAsState()
    val lastSync by viewModel.lastCloudSyncTimestamp.collectAsState()
    val language by viewModel.currentLanguage.collectAsState()
    val allRequests by viewModel.allRequests.collectAsState()

    var showDemoLoginInput by remember { mutableStateOf(false) }
    var demoName by remember { mutableStateOf("Mehdi Koceir") }
    var demoEmail by remember { mutableStateOf("koceirmehdi@gmail.com") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SleekBackground),
            border = BorderStroke(1.dp, SleekBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("dialog_firebase_auth")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with Firebase + Google Identity badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFF7ED)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = Color(0xFFEA580C),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (language == "ar") "المصادقة والمزامنة السحابية" else "Firebase Auth & Firestore",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = SleekNavy
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (authState.isAuthenticated) Color(0xFF22C55E) else Color(0xFFEAB308))
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (authState.isAuthenticated)
                                        (if (language == "ar") "متصل • مزامنة سحابية نشطة" else "Connecté • Sync Cloud Active")
                                    else
                                        (if (language == "ar") "وضع زائر • غير متصل" else "Mode Invité • Non Connecté"),
                                    fontSize = 11.sp,
                                    color = if (authState.isAuthenticated) Color(0xFF16A34A) else SleekTextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("button_close_auth_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = SleekTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                HorizontalDivider(color = SleekBorder, thickness = 0.5.dp)

                // User Identity Section
                if (authState.isAuthenticated) {
                    // Authenticated Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SleekSurface,
                        border = BorderStroke(1.dp, SleekBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(SleekPrimaryContainer)
                                        .border(1.5.dp, SleekPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = authState.displayName.take(1).uppercase(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SleekPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = authState.displayName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = SleekNavy
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFDCFCE7))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = authState.provider,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF15803D)
                                            )
                                        }
                                    }
                                    if (authState.email.isNotBlank()) {
                                        Text(
                                            text = authState.email,
                                            fontSize = 12.sp,
                                            color = SleekTextSecondary
                                        )
                                    }
                                    Text(
                                        text = "UID: ${authState.uid.take(14)}...",
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = SleekTextSecondary
                                    )
                                }
                            }

                            // Sign out action
                            OutlinedButton(
                                onClick = { viewModel.signOut() },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                modifier = Modifier.fillMaxWidth().testTag("button_firebase_sign_out")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Logout,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (language == "ar") "تسجيل الخروج من Firebase" else "Déconnexion de Firebase Auth",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                } else {
                    // Not Authenticated: Provide Google Sign-In & Instant Demo Auth
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (language == "ar")
                                "سجل الدخول لحفظ وتتبع طلبات الصيانة، الفواتير، والحرفيين المعتمدين في Firestore."
                            else
                                "Connectez-vous pour synchroniser vos demandes, devis et artisans certifiés en temps réel avec Cloud Firestore.",
                            fontSize = 12.sp,
                            color = SleekTextSecondary,
                            lineHeight = 16.sp
                        )

                        // Google Sign-In Button
                        Button(
                            onClick = { viewModel.signInWithGoogle() },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SleekNavy,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("button_google_signin")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Google",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (language == "ar") "المتابعة بحساب Google" else "Continuer avec Google Sign-In",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Demo Account One-Tap Button
                        OutlinedButton(
                            onClick = { showDemoLoginInput = !showDemoLoginInput },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, SleekBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SleekNavy),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .testTag("button_demo_auth_toggle")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = SleekPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == "ar") "تسجيل دخول سريع (حساب تجريبي)" else "Connexion Rapide (Compte Test)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        AnimatedVisibility(visible = showDemoLoginInput) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SleekNavSurface,
                                border = BorderStroke(1.dp, SleekBorder),
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = demoName,
                                        onValueChange = { demoName = it },
                                        label = { Text("Nom complet", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = demoEmail,
                                        onValueChange = { demoEmail = it },
                                        label = { Text("Email", fontSize = 11.sp) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Button(
                                        onClick = {
                                            viewModel.signInWithDemoAccount(demoName, demoEmail)
                                            showDemoLoginInput = false
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = SleekPrimary),
                                        modifier = Modifier.fillMaxWidth().testTag("button_confirm_demo_login")
                                    ) {
                                        Text("Valider la Connexion Firebase", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = SleekBorder, thickness = 0.5.dp)

                // Cloud Firestore Persistence Section
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF8FAFC),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = SleekPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Base de Données Cloud Firestore",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekNavy
                                )
                            }

                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = SleekPrimary
                                )
                            }
                        }

                        // Persistence Details
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Demandes locales synchronisées :",
                                    fontSize = 11.sp,
                                    color = SleekTextSecondary
                                )
                                Text(
                                    text = "${allRequests.size} requête(s)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekNavy
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Collection Firestore :",
                                    fontSize = 11.sp,
                                    color = SleekTextSecondary
                                )
                                Text(
                                    text = "users/{uid}/requests",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SleekPrimary
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Dernière synchronisation :",
                                    fontSize = 11.sp,
                                    color = SleekTextSecondary
                                )
                                val timeText = if (lastSync != null) {
                                    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(lastSync!!))
                                } else {
                                    "En attente"
                                }
                                Text(
                                    text = timeText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SleekTextPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Manual Sync Button
                        Button(
                            onClick = { viewModel.syncUserDataToFirestore() },
                            enabled = !isSyncing,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SleekPrimary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("button_sync_firestore_now")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == "ar") "مزامنة البيانات السحابية الآن" else "Synchroniser avec Firestore",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
