package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

/**
 * This class represents the Login Screen and the elements it contains.
 *
 * It is used to interact with the UI elements during UI tests.
 */
class LoginScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<LoginScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("loginScreen") }) {

    // Structural elements of the UI
    val loginTitle: KNode = child { hasTestTag("loginTitle") }
    val loginButton: KNode = onNode { hasTestTag("loginButton") }
}