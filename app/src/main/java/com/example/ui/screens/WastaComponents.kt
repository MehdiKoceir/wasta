package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProfessionalEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WastaHeaderBar(
    currentLanguage: String,
    currentRole: String,
    onLanguageChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
    userDisplayName: String = "",
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Surface(
        color = SleekBackground,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 0.5.dp, color = SleekBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Sleek Top Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sleek Brand Logo & Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SleekPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "W?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "WASTA?",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekNavy,
                                letterSpacing = (-0.5).sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SleekPrimaryContainer)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "DZ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekPrimary
                                )
                            }
                        }
                        Text(
                            text = if (currentLanguage == "ar") "جد الخدمة المناسبة، ليس الشخص المناسب"
                                   else if (currentLanguage == "en") "Find the right service, not the right person"
                                   else "Trouvez le bon service, pas le bon piston",
                            fontSize = 11.sp,
                            color = SleekTextSecondary,
                            maxLines = 1
                        )
                    }
                }

                // Sleek Header Actions: Notifications & Profile & Language
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Notification Icon with red dot badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SleekSurface)
                            .border(1.dp, SleekBorder, CircleShape)
                            .clickable { onNotificationClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = SleekTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        // Red indicator dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-2).dp, y = 2.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                                .border(1.dp, Color.White, CircleShape)
                        )
                    }

                    // Sleek Profile avatar badge (Opens Firebase Auth & Firestore Dialog)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SleekPrimaryContainer)
                            .border(2.dp, SleekPrimary, CircleShape)
                            .clickable { onProfileClick() }
                            .testTag("button_header_profile_auth"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (userDisplayName.isNotBlank()) userDisplayName.take(1).uppercase() else "A",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SleekPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary row: Language selector + Role switcher tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sleek Role selector tabs (Customer, Professional, Admin)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(SleekContainer)
                        .border(1.dp, SleekBorder, RoundedCornerShape(24.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RoleTabItem(
                        title = if (currentLanguage == "ar") "الزبون" else "Client",
                        selected = currentRole == "CUSTOMER",
                        modifier = Modifier.weight(1f).testTag("tab_customer")
                    ) { onRoleChange("CUSTOMER") }

                    RoleTabItem(
                        title = if (currentLanguage == "ar") "المهني" else "Artisan",
                        selected = currentRole == "PROFESSIONAL",
                        modifier = Modifier.weight(1f).testTag("tab_pro")
                    ) { onRoleChange("PROFESSIONAL") }

                    RoleTabItem(
                        title = if (currentLanguage == "ar") "الإدارة" else "Admin",
                        selected = currentRole == "ADMIN",
                        modifier = Modifier.weight(1f).testTag("tab_admin")
                    ) { onRoleChange("ADMIN") }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Language pills
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SleekContainer)
                        .border(1.dp, SleekBorder, RoundedCornerShape(20.dp))
                        .padding(2.dp)
                ) {
                    LanguagePill(label = "عربي", selected = currentLanguage == "ar") { onLanguageChange("ar") }
                    LanguagePill(label = "FR", selected = currentLanguage == "fr") { onLanguageChange("fr") }
                    LanguagePill(label = "EN", selected = currentLanguage == "en") { onLanguageChange("en") }
                }
            }
        }
    }
}

@Composable
private fun LanguagePill(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) SleekPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else SleekTextSecondary
        )
    }
}

@Composable
private fun RoleTabItem(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) SleekPrimary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else SleekTextSecondary
        )
    }
}

@Composable
fun WastaBottomNavBar(
    currentTab: String = "home",
    onTabSelected: (String) -> Unit = {}
) {
    Surface(
        color = SleekNavSurface,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = SleekBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = currentTab == "home",
                onClick = { onTabSelected("home") }
            )
            BottomNavItem(
                icon = Icons.AutoMirrored.Outlined.Assignment,
                label = "Demandes",
                isSelected = currentTab == "requests",
                onClick = { onTabSelected("requests") }
            )
            BottomNavItem(
                icon = Icons.Outlined.History,
                label = "Historique",
                isSelected = currentTab == "history",
                onClick = { onTabSelected("history") }
            )
            BottomNavItem(
                icon = Icons.Outlined.Person,
                label = "Profil",
                isSelected = currentTab == "profile",
                onClick = { onTabSelected("profile") }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) SleekPrimary else SleekTextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) SleekPrimary else SleekTextSecondary
        )
    }
}

@Composable
fun VerificationBadgesRow(pro: ProfessionalEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (pro.identityVerified) {
            BadgeChip(icon = Icons.Default.VerifiedUser, text = "Identity Verified", color = SleekPrimary)
        }
        if (pro.phoneVerified) {
            BadgeChip(icon = Icons.Default.Phone, text = "Phone Verified", color = SleekPrimary)
        }
        if (pro.profileVerified) {
            BadgeChip(icon = Icons.Default.WorkspacePremium, text = "Profile Verified", color = SleekPrimaryDark)
        }
    }
}

@Composable
fun BadgeChip(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SleekPrimaryContainer.copy(alpha = 0.5f))
            .border(1.dp, SleekBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = SleekNavy)
    }
}
