package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("profileScreen") }) {

    val chatButton: KNode = onNode { hasTestTag("chatButton") }
    val logo: KNode = onNode { hasTestTag("logo") }
    val goBackButton: KNode = onNode { hasTestTag("goBackButton") }
    val bottomNav: KNode = onNode { hasTestTag("bottomNavMenu") }
    val centeredViewProfileColumn: KNode = onNode { hasTestTag("centeredViewProfileColumn") }
    val profilePic: KNode = centeredViewProfileColumn.child { hasTestTag("profilePic") }
    val name: KNode = centeredViewProfileColumn.child { hasTestTag("name") }
    val username: KNode = centeredViewProfileColumn.child { hasTestTag("username") }
    val leftAlignedViewProfileColumn: KNode =
        centeredViewProfileColumn.child { hasTestTag("leftAlignedViewProfileColumn") }
    val bioLabelText: KNode = leftAlignedViewProfileColumn.child { hasTestTag("bioLabelText") }
    val bioInfoText: KNode = leftAlignedViewProfileColumn.child { hasTestTag("bioInfoText") }
    val phoneNumberBirthDateRow: KNode = leftAlignedViewProfileColumn.child { hasTestTag("phoneNumberBirthDateRow") }
    val phoneNumberColumn: KNode = phoneNumberBirthDateRow.child { hasTestTag("phoneNumberColumn") }
    val phoneNumberLabelText: KNode = phoneNumberColumn.child { hasTestTag("phoneNumberLabelText") }
    val phoneNumberInfoText: KNode = phoneNumberColumn.child { hasTestTag("phoneNumberInfoText") }
    val birthDateColumn: KNode = phoneNumberBirthDateRow.child { hasTestTag("birthDateColumn") }
    val birthDateLabelText: KNode = birthDateColumn.child { hasTestTag("birthDateLabelText") }
    val birthDateInfoText: KNode = birthDateColumn.child { hasTestTag("birthDateInfoText") }
}
