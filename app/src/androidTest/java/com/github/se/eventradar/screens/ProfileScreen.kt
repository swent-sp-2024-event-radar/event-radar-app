package com.github.se.eventradar.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen
import io.github.kakaocup.compose.node.element.KNode

class ProfileScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ProfileScreen>(
        semanticsProvider = semanticsProvider,
        viewBuilderAction = { hasTestTag("profileScreen") }) {

  val profileScreen: KNode = onNode { hasTestTag("profileScreen") }
  val topBar: KNode = profileScreen.child { hasTestTag("topBar") }
  val chatButton: KNode = profileScreen.child { hasTestTag("chatButton") }
  val editButton: KNode = profileScreen.child { hasTestTag("editButton") }
  val logo: KNode = topBar.child { hasTestTag("logo") }
  val goBackButton: KNode = topBar.child { hasTestTag("goBackButton") }
  val editProfile: KNode = topBar.child { hasTestTag("editProfile") }
  val bottomNav: KNode = profileScreen.child { hasTestTag("bottomNavMenu") }
  val centeredViewProfileColumn: KNode =
      profileScreen.child { hasTestTag("centeredViewProfileColumn") }
  val profilePic: KNode = centeredViewProfileColumn.child { hasTestTag("profilePic") }
  val name: KNode = centeredViewProfileColumn.child { hasTestTag("name") }
  val nameRow: KNode = centeredViewProfileColumn.child { hasTestTag("nameRow") }
  val firstNameTextField: KNode = nameRow.child { hasTestTag("firstNameTextField") }
  val nameSpacer: KNode = nameRow.child { hasTestTag("nameSpacer") }
  val lastNameTextField: KNode = nameRow.child { hasTestTag("lastNameTextField") }
  val username: KNode = centeredViewProfileColumn.child { hasTestTag("username") }
  val usernameTextField: KNode = centeredViewProfileColumn.child { hasTestTag("usernameTextField") }
  val leftAlignedViewProfileColumn: KNode =
      centeredViewProfileColumn.child { hasTestTag("leftAlignedViewProfileColumn") }
  val bioLabelText: KNode = leftAlignedViewProfileColumn.child { hasTestTag("bioLabelText") }
  val bioInfoText: KNode = leftAlignedViewProfileColumn.child { hasTestTag("bioInfoText") }
  val bioTextField: KNode = leftAlignedViewProfileColumn.child { hasTestTag("bioTextField") }
  val phoneNumberBirthDateRow: KNode =
      leftAlignedViewProfileColumn.child { hasTestTag("phoneNumberBirthDateRow") }
  val phoneNumberColumn: KNode = phoneNumberBirthDateRow.child { hasTestTag("phoneNumberColumn") }
  val phoneNumberLabelText: KNode = phoneNumberColumn.child { hasTestTag("phoneNumberLabelText") }
  val phoneNumberInfoText: KNode = phoneNumberColumn.child { hasTestTag("phoneNumberInfoText") }
  val phoneNumberTextField: KNode =
      phoneNumberBirthDateRow.child { hasTestTag("phoneNumberTextField") }
  val birthDateColumn: KNode = phoneNumberBirthDateRow.child { hasTestTag("birthDateColumn") }
  val birthDateLabelText: KNode = birthDateColumn.child { hasTestTag("birthDateLabelText") }
  val birthDateInfoText: KNode = birthDateColumn.child { hasTestTag("birthDateInfoText") }
  val birthDateTextField: KNode = phoneNumberBirthDateRow.child { hasTestTag("birthDateTextField") }
  val phoneNumberBirthDateSpacer: KNode =
      phoneNumberBirthDateRow.child { hasTestTag("phoneNumberBirthDateSpacer") }
  val saveButton: KNode = centeredViewProfileColumn.child { hasTestTag("saveButton") }
}
