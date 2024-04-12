package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/**
 *
 */
class SignupScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<SignupScreen>(semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("SignupScreen") }) {

    // Structural elements of the UI of the Signup screen
    val eventRadarLogo: KNode = onNode { hasTestTag("eventRadarLogo") }
    val usernameTextField: KNode = onNode { hasTestTag("signUpUsernameField") }
    val nameTextField: KNode = onNode { hasTestTag("signUpNameField") }
    val surnameTextField: KNode = onNode { hasTestTag("signUpSurnameField") }
    val phoneTextField: KNode = onNode { hasTestTag("signUpPhoneField") }
    val birthDateTextField: KNode = onNode { hasTestTag("signUpBirthDateField") }
    val signUpButton: KNode = onNode { hasTestTag("signUpLoginButton") }
    val profilePicture: KNode = onNode { hasTestTag("signUpProfilePicture") }
}