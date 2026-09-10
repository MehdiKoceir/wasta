package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.WastaViewModel
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CustomerScreen
import com.example.ui.screens.FirebaseAuthDialog
import com.example.ui.screens.ProfessionalScreen
import com.example.ui.screens.VerifiedProfessionalProfileScreen
import com.example.ui.screens.WastaBottomNavBar
import com.example.ui.screens.WastaHeaderBar
import com.example.ui.theme.WastaTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      WastaApp()
    }
  }
}

@Composable
fun WastaApp(viewModel: WastaViewModel = viewModel()) {
  val currentRole by viewModel.currentUserRole.collectAsState()
  val currentLanguage by viewModel.currentLanguage.collectAsState()
  val notificationMessage by viewModel.notificationMessage.collectAsState()
  val selectedVerifiedProfile by viewModel.selectedVerifiedProfile.collectAsState()
  val allProfessionals by viewModel.allProfessionals.collectAsState()
  val authState by viewModel.authState.collectAsState()
  val snackbarHostState = remember { SnackbarHostState() }
  var currentNavTab by remember { mutableStateOf("home") }
  var showAuthDialog by remember { mutableStateOf(false) }

  LaunchedEffect(notificationMessage) {
    notificationMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearNotification()
    }
  }

  val layoutDirection = if (currentLanguage == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr

  CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
    WastaTheme {
      if (showAuthDialog) {
        FirebaseAuthDialog(
          viewModel = viewModel,
          onDismiss = { showAuthDialog = false }
        )
      }

      if (selectedVerifiedProfile != null) {
        VerifiedProfessionalProfileScreen(
          profile = selectedVerifiedProfile!!,
          viewModel = viewModel,
          onBack = { viewModel.closeVerifiedProfile() }
        )
      } else {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          topBar = {
            WastaHeaderBar(
              currentLanguage = currentLanguage,
              currentRole = currentRole,
              userDisplayName = authState.displayName,
              onLanguageChange = { viewModel.setLanguage(it) },
              onRoleChange = { viewModel.switchRole(it) },
              onProfileClick = { showAuthDialog = true },
              onNotificationClick = {
                viewModel.showNotification("Notifications vérifiées à jour.")
              }
            )
          },
          bottomBar = {
            WastaBottomNavBar(
              currentTab = currentNavTab,
              onTabSelected = { tab ->
                currentNavTab = tab
                when (tab) {
                  "requests" -> viewModel.switchRole("CUSTOMER")
                  "history" -> viewModel.showNotification("Historique des interventions et factures synchronisé.")
                  "profile" -> {
                    showAuthDialog = true
                  }
                }
              }
            )
          },
          snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            when (currentRole) {
              "CUSTOMER" -> CustomerScreen(viewModel = viewModel)
              "PROFESSIONAL" -> ProfessionalScreen(viewModel = viewModel)
              "ADMIN" -> AdminScreen(viewModel = viewModel)
              else -> CustomerScreen(viewModel = viewModel)
            }
          }
        }
      }
    }
  }
}


