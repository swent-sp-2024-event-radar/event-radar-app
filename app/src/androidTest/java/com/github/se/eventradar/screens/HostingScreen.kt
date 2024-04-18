package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class HostingScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<HostingScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("hostingScreen") }) {

    val logo: KNode = child { hasTestTag("logo") }
    val tabs: KNode = child { hasTestTag("tabs") }
    val myHostedEventsTab : KNode = tabs.child {hasTestTag("myHostedEventsTab")}
    val eventCard: KNode = onNode { hasTestTag("eventCard") }
    val bottomNav: KNode = child { hasTestTag("bottomNavMenu") }
    val floatingActionButtons : KNode = child{hasTestTag("floatingActionButtons")}
    val createEventButton : KNode = floatingActionButtons.child{hasTestTag("createEventButton")}
    val switchViewButton : KNode = floatingActionButtons.child{hasTestTag("switchViewButton")}
}
