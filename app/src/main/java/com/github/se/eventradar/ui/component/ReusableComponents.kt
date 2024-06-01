package com.github.se.eventradar.ui.component

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.github.se.eventradar.R
import com.github.se.eventradar.model.Location
import com.github.se.eventradar.model.User
import com.github.se.eventradar.model.event.Event
import com.github.se.eventradar.model.event.EventCategory
import com.github.se.eventradar.ui.BottomNavigationMenu
import com.github.se.eventradar.ui.navigation.NavigationActions
import com.github.se.eventradar.ui.navigation.Route
import com.github.se.eventradar.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.github.se.eventradar.ui.navigation.getTopLevelDestination
import com.github.se.eventradar.viewmodel.SearchFilterUiState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

fun getIconFromViewListBool(viewList: Boolean): ImageVector {
  return if (viewList) {
    Icons.Default.Place
  } else {
    Icons.AutoMirrored.Filled.List
  }
}

@Composable
fun ViewToggleFab(modifier: Modifier = Modifier, onClick: () -> Unit, iconVector: ImageVector) {
  FloatingActionButton(onClick = { onClick() }, modifier = modifier) {
    Icon(imageVector = iconVector, contentDescription = "Icon")
  }
}

@Composable
fun Logo(modifier: Modifier = Modifier) {
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    Image(
        painter = painterResource(id = R.drawable.event_logo),
        contentDescription = "Event Radar Logo",
        modifier = Modifier.size(width = 186.dp, height = 50.dp))
  }
}

@Composable
fun EventList(events: List<Event>, modifier: Modifier = Modifier, onCardClick: (String) -> Unit) {
  LazyColumn(modifier = modifier) { items(events) { event -> EventCard(event, onCardClick) } }
}

@Composable
fun EventCard(event: Event, onCardClick: (String) -> Unit) {
  Card(
      modifier =
          Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
              .height(IntrinsicSize.Min)
              .testTag("eventCard"),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      onClick = { onCardClick(event.fireBaseID) }) {
        Row(modifier = Modifier.fillMaxSize()) {
          Column(
              modifier =
                  Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp)
                      .weight(1f),
              verticalArrangement = Arrangement.Center) {
                Text(
                    text = event.eventName,
                    style =
                        TextStyle(
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = event.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style =
                        TextStyle(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.roboto)),
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onPrimaryContainer))
              }
          Image(
              painter =
                  if (event.eventPhoto == "") {
                    painterResource(id = R.drawable.placeholder)
                  } else {
                    rememberAsyncImagePainter(event.eventPhoto)
                  },
              contentDescription = "Event Image",
              contentScale = ContentScale.FillBounds,
              modifier = Modifier.weight(0.3f).fillMaxHeight().aspectRatio(1f))
        }
      }
}

@Composable
fun SearchBarAndFilter(
    onSearchQueryChanged: (String) -> Unit,
    searchQuery: String,
    onSearchActiveChanged: (Boolean) -> Unit,
    onFilterDialogOpen: () -> Unit,
    modifier: Modifier = Modifier,
    placeholderStringResource: Int = R.string.search_placeholder,
) {
  Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
    SearchBarField(
        searchQuery,
        onSearchQueryChanged,
        onSearchActiveChanged,
        Modifier.weight(1f),
        placeholderStringResource)

    // Filter button
    Button(
        onClick = { onFilterDialogOpen() },
        modifier = Modifier.padding(start = 8.dp).testTag("filterButton")) {
          Text(stringResource(id = R.string.filter))
        }
  }
}

@Composable
fun SearchBarField(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearchActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    placeholderStringResource: Int = R.string.search_placeholder,
) {
  // Search bar
  TextField(
      value = searchQuery,
      onValueChange = {
        onSearchQueryChanged(it)
        if (it == "") onSearchActiveChanged(false) else onSearchActiveChanged(true)
      },
      modifier = modifier.fillMaxWidth().testTag("searchBar"),
      maxLines = 1,
      shape = RoundedCornerShape(32.dp),
      colors =
          TextFieldDefaults.colors(
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent,
              disabledIndicatorColor = Color.Transparent),
      placeholder = { Text(stringResource(id = placeholderStringResource)) },
      leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) })
}

@Composable
fun FilterPopUp(
    onFreeSwitchChanged: () -> Unit,
    onFilterApply: () -> Unit,
    uiState: SearchFilterUiState,
    onRadiusQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
  Box(modifier = modifier) {
    Card(
        modifier = Modifier.padding(12.dp).testTag("filterCard"),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
      Column(modifier = Modifier.padding(12.dp).testTag("filterCardColumn")) {
        // Text input for radius
        Row(
            modifier = Modifier.wrapContentWidth().testTag("filterCardColumnRow"),
            verticalAlignment = Alignment.CenterVertically) {
              Row(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.testTag("filterCardColumnRowRadius")) {
                  Text(
                      text = stringResource(id = R.string.radius_label),
                      style = TextStyle(fontSize = 16.sp),
                      modifier = Modifier.testTag("radiusLabel"))
                }
                Box(modifier = Modifier.testTag("radiusInputBox")) {
                  BasicTextField(
                      value = uiState.radiusQuery,
                      onValueChange = { onRadiusQueryChanged(it) },
                      keyboardOptions =
                          KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                      modifier =
                          Modifier.widthIn(max = 36.dp)
                              .border(
                                  width = 1.dp,
                                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                                  shape = RoundedCornerShape(10))
                              .testTag("radiusInput"),
                      textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                      singleLine = true)
                }
                Box(modifier = Modifier.testTag("filterCardColumnRowKm")) {
                  Text(
                      text = stringResource(id = R.string.radius_km_label),
                      style = TextStyle(fontSize = 16.sp),
                      modifier = Modifier.testTag("kmLabel"))
                }
              }
              // Slider for free selection
              Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1.5f).testTag("freeSwitchRow")) {
                    Text(
                        text = stringResource(id = R.string.free_events_label),
                        style =
                            TextStyle(
                                fontSize = 16.sp,
                            ),
                        modifier = Modifier.testTag("freeSwitchLabel"))
                    Switch(
                        checked = uiState.isFreeSwitchOn,
                        onCheckedChange = { onFreeSwitchChanged() },
                        modifier = Modifier.widthIn(32.dp).testTag("freeSwitch"))
                  }
            }

        // Buttons for category selection
        Text(
            text = stringResource(id = R.string.category_label),
            modifier = Modifier.testTag("categoryLabel"),
            style =
                TextStyle(
                    fontSize = 16.sp,
                ))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.testTag("categoryRow"),
        ) {
          CategorySelection(
              uiState = uiState,
              modifier = Modifier.testTag("categoryOptionsColumn"),
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Button to apply filter
        Row(
            modifier = Modifier.testTag("filterApplyRow"),
            horizontalArrangement = Arrangement.Center) {
              Button(
                  onClick = { onFilterApply() }, modifier = Modifier.testTag("filterApplyButton")) {
                    Text(stringResource(id = R.string.filter_apply))
                  }
            }
      }
    }
  }
}

@Composable
fun CategorySelection(uiState: SearchFilterUiState, modifier: Modifier) {
  LazyColumn(modifier = modifier) {
    items(EventCategory.entries.subList(0, EventCategory.entries.size / 2 + 1)) { category ->
      CategoryDisplayColumn(category, uiState)
    }
  }
  LazyColumn(modifier = modifier) {
    items(
        EventCategory.entries.subList(
            EventCategory.entries.size / 2 + 1, EventCategory.entries.size)) { category ->
          CategoryDisplayColumn(category, uiState)
        }
  }
}

@Composable
fun CategoryDisplayColumn(
    category: EventCategory,
    uiState: SearchFilterUiState,
) {
  var isChecked by remember { mutableStateOf(uiState.categoriesCheckedList.contains(category)) }
  Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Start,
      modifier =
          Modifier.padding(vertical = 0.dp).testTag("categoryOptionRow-${category.displayName}")) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
              isChecked = it
              if (isChecked) {
                uiState.categoriesCheckedList.add(category)
              } else {
                uiState.categoriesCheckedList.remove(category)
              }
            },
            modifier = Modifier.scale(0.6f).size(10.dp).padding(start = 10.dp).testTag("checkbox"))
        Text(
            text = category.displayName,
            style =
                TextStyle(
                    fontSize = 16.sp,
                ),
            modifier = Modifier.padding(start = 16.dp).testTag("checkboxText"))
      }
}

@Composable
fun ImagePicker(modifier: Modifier, imageUri: Uri?) {
  Row(
      modifier = modifier.padding(top = 16.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically) {
        if (imageUri != null) {
          val imageBitmap = rememberImagePainter(data = imageUri)
          Image(
              painter = imageBitmap,
              contentDescription = "Selected Image",
              contentScale = ContentScale.Crop,
              modifier = modifier)
        } else {
          Image(
              painter =
                  painterResource(
                      id = R.drawable.placeholder), // Replace with your placeholder image resource
              contentDescription = "Select Picture Placeholder",
              contentScale = ContentScale.Crop,
              modifier = modifier)
        }
      }
}

@Composable
fun StandardInputTextField(
    modifier: Modifier,
    textFieldLabel: String,
    textFieldValue: String,
    placeHolderText: String = "",
    onValueChange: ((String) -> Unit),
    errorState: Boolean
) {
  OutlinedTextField(
      value = textFieldValue,
      onValueChange = onValueChange,
      label = { Text(textFieldLabel) },
      modifier = modifier,
      placeholder = { Text(placeHolderText) },
      colors =
          OutlinedTextFieldDefaults.colors()
              .copy(
                  focusedPrefixColor = MaterialTheme.colorScheme.primary,
                  unfocusedPrefixColor = MaterialTheme.colorScheme.primary),
      shape = RoundedCornerShape(12.dp),
      isError = errorState,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownInputTextField(
    modifier: Modifier = Modifier,
    textFieldLabel: String,
    textFieldValue: String,
    onValueChange: (String) -> Unit,
    errorState: Boolean,
    options: List<String>,
    toggleIconTestTag: String,
) {
  var isExpanded by remember { mutableStateOf(false) }
  var selectedOption by remember { mutableStateOf(textFieldValue) }

  ExposedDropdownMenuBox(
      expanded = isExpanded,
      onExpandedChange = { isExpanded = !isExpanded },
      modifier = Modifier.testTag("exposedDropDownMenuBox"),
  ) {
    OutlinedTextField(
        value = selectedOption,
        onValueChange = onValueChange,
        label = { Text(textFieldLabel) },
        modifier =
            modifier.menuAnchor(
                MenuAnchorType.PrimaryNotEditable), // Open dropdown on text field click
        colors =
            OutlinedTextFieldDefaults.colors()
                .copy(
                    focusedPrefixColor = MaterialTheme.colorScheme.primary,
                    unfocusedPrefixColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(12.dp),
        isError = errorState,
        trailingIcon = {
          ExposedDropdownMenuDefaults.TrailingIcon(
              expanded = isExpanded, modifier = Modifier.testTag(toggleIconTestTag))
        },
        readOnly = true, // Make the text field read-only to handle input via dropdown
        singleLine = true)
    ExposedDropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { isExpanded = false },
        modifier =
            Modifier.exposedDropdownSize(true)
                .testTag("exposedDropDownMenu")
                .requiredSizeIn(maxHeight = 150.dp)) {
          options.forEach { option ->
            DropdownMenuItem(
                text = { Text(text = option, fontSize = 16.sp) },
                onClick = {
                  selectedOption = option
                  onValueChange(option)
                  isExpanded = false
                },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
            )
          }
        }
  }
}

fun displayChosenOrganisers(organisers: List<String>): String {
  var organisersString = ""
  for (i in organisers.indices) {
    if (i == 0) {
      organisersString += organisers[i]
    } else {
      organisersString += ", " + organisers[i]
    }
  }
  return organisersString
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectDropDownMenu( // list of organisers
    onSelectedListChanged: ((List<User>) -> Unit),
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    getFriends: (() -> Unit), // this searches for the friends!
    friendsList: List<User>, // map userId to userName?
) {
  getFriends() // initialize user friends
  val selectedItems = remember { mutableStateListOf<User>() }
  var isExpanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
      expanded = isExpanded,
      onExpandedChange = { isExpanded = !isExpanded },
      modifier = Modifier.testTag("multiSelectExposedDropDownMenuBox"),
  ) {
    OutlinedTextField(
        value =
            displayChosenOrganisers(selectedItems.map { eachUser -> eachUser.username }.toList()),
        onValueChange = {},
        label = label,
        modifier =
            modifier.menuAnchor(
                MenuAnchorType.PrimaryNotEditable), // Open dropdown on text field click
        colors =
            OutlinedTextFieldDefaults.colors()
                .copy(
                    focusedPrefixColor = MaterialTheme.colorScheme.primary,
                    unfocusedPrefixColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = {
          Icon(
              imageVector = Icons.Default.Face,
              contentDescription = "Search Icon",
          )
        },
        trailingIcon = {
          ExposedDropdownMenuDefaults.TrailingIcon(
              expanded = isExpanded, modifier = Modifier.testTag("multiSelectDropDownToggleIcon"))
        },
        readOnly = true // Make the text field read-only to handle input via dropdown
        )
    ExposedDropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { isExpanded = false },
        modifier = Modifier.testTag("multiSelectExposedDropdownMenu")) {
          for (friend in friendsList) {
            val isSelected = selectedItems.contains(friend)
            DropdownMenuItem(
                text = { Text(text = friend.username, style = MaterialTheme.typography.bodyLarge) },
                onClick = {
                  if (isSelected) {
                    selectedItems.remove(friend)
                  } else {
                    selectedItems.add(friend)
                  }
                  onSelectedListChanged(selectedItems.toList())
                },
                trailingIcon = {
                  if (isSelected) {
                    Icon(Icons.Default.Clear, "Check")
                  } else {
                    Icon(Icons.Default.Add, "Add")
                  }
                },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                modifier = Modifier.exposedDropdownSize(true).requiredSizeIn(maxHeight = 150.dp))
          }
        }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDropDownMenu(
    value: String,
    onLocationChanged: (String) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    getLocations: () -> Unit,
    locationList: List<Location>,
) {
  val (allowExpanded, setExpanded) = remember { mutableStateOf(false) }
  val expanded = allowExpanded && locationList.isNotEmpty()

  ExposedDropdownMenuBox(
      expanded = expanded,
      onExpandedChange = setExpanded,
      modifier = Modifier.testTag("locationExposedDropDownMenuBox"),
  ) {
    OutlinedTextField(
        value = value,
        onValueChange = {
          onLocationChanged(it)
          getLocations()
        },
        label = label,
        trailingIcon = {
          ExposedDropdownMenuDefaults.TrailingIcon(
              expanded = expanded, modifier = Modifier.testTag("locationDropDownMenuToggleIcon"))
        },
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.menuAnchor(MenuAnchorType.PrimaryEditable))
    ExposedDropdownMenu(
        modifier =
            Modifier.exposedDropdownSize(true)
                .requiredSizeIn(maxHeight = 150.dp)
                .testTag("locationExposedDropDownMenu"),
        expanded = expanded,
        onDismissRequest = { setExpanded(false) },
    ) {
      locationList.forEach { location ->
        DropdownMenuItem(
            text = { Text(location.address, style = MaterialTheme.typography.bodyLarge) },
            onClick = {
              onLocationChanged(location.address)
              setExpanded(false)
            },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
        )
      }
    }
  }
}

@Composable
fun DateInputTextField(
    modifier: Modifier,
    textFieldLabel: String,
    textFieldValue: String,
    onValueChange: ((String) -> Unit),
    errorState: Boolean
) {
  OutlinedTextField(
      value = textFieldValue,
      onValueChange = onValueChange,
      label = { Text(textFieldLabel) },
      modifier = modifier,
      placeholder = { Text("YYYY-MM-DD") },
      colors =
          OutlinedTextFieldDefaults.colors()
              .copy(
                  focusedPrefixColor = MaterialTheme.colorScheme.primary,
                  unfocusedPrefixColor = MaterialTheme.colorScheme.primary),
      shape = RoundedCornerShape(12.dp),
      isError = errorState,
      trailingIcon = {
        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Select Date")
      })
}

@Composable
fun GoBackButton(modifier: Modifier, goBack: () -> Unit) {
  Button(
      onClick = { goBack() },
      modifier = modifier.testTag("goBackButton"),
      colors =
          ButtonDefaults.buttonColors(
              contentColor = Color.Transparent,
              containerColor = Color.Transparent,
          ),
  ) {
    Icon(
        painter = painterResource(id = R.drawable.back_arrow),
        contentDescription = "Back navigation arrow",
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.width(24.dp).height(24.dp).align(Alignment.CenterVertically))
  }
}

@Composable
fun ProfilePic(
    profilePicUrl: String,
    firstName: String,
    lastName: String,
    modifier: Modifier = Modifier
) {
  AsyncImage(
      model = ImageRequest.Builder(LocalContext.current).data(profilePicUrl).build(),
      placeholder = painterResource(id = R.drawable.placeholder),
      contentDescription = "Profile picture of $firstName $lastName",
      contentScale = ContentScale.Crop,
      modifier = modifier.padding(start = 16.dp).clip(CircleShape).size(56.dp))
}

@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {
      Logo(modifier = Modifier.fillMaxWidth().padding(top = 32.dp, start = 16.dp).testTag("logo"))
    },
    floatingActionButton: @Composable () -> Unit,
    navigationActions: NavigationActions,
    selectedItem: String = Route.HOME,
    content: @Composable (PaddingValues) -> Unit,
) {
  Scaffold(
      modifier = modifier,
      topBar = topBar,
      bottomBar = {
        BottomNavigationMenu(
            onTabSelected = navigationActions::navigateTo,
            tabList = TOP_LEVEL_DESTINATIONS,
            selectedItem = getTopLevelDestination(selectedItem),
            modifier = Modifier.testTag("bottomNavMenu"))
      },
      floatingActionButton = floatingActionButton,
  ) {
    content(it)
  }
}

@Composable
fun GenericDialogBox(
    openDialog: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    onClickConfirmButton: () -> Unit = {},
    boxIcon: @Composable (() -> Unit)?,
) {
  val display by openDialog
  if (display) {
    AlertDialog(
        icon = boxIcon,
        text = {
          Text(
              text = message,
              textAlign = TextAlign.Center,
              modifier = Modifier.testTag("ErrorDisplayText"))
        },
        title = {
          Text(
              text = title,
              modifier = Modifier.testTag("ErrorTitle"),
          )
        },
        onDismissRequest = { openDialog.value = false },
        confirmButton = {
          TextButton(
              onClick = {
                openDialog.value = false
                onClickConfirmButton()
              },
              modifier = Modifier.testTag("dialogConfirmButton")) {
                Text("Ok")
              }
        },
        modifier = modifier)
  }
}

@Composable
fun StandardDialogBox(
    display: Boolean,
    modifier: Modifier,
    title: String,
    message: String,
    onClickConfirmButton: () -> Unit,
    boxIcon: @Composable (() -> Unit)?,
) {
  if (display) {
    AlertDialog(
        icon = boxIcon,
        text = {
          Text(
              text = message,
              textAlign = TextAlign.Center,
              modifier = Modifier.testTag("DisplayText"))
        },
        title = {
          Text(
              text = title,
              modifier = Modifier.testTag("DisplayTitle"),
          )
        },
        onDismissRequest = {},
        confirmButton = {
          TextButton(
              onClick = onClickConfirmButton, modifier = Modifier.testTag("dialogConfirmButton")) {
                Text("Ok")
              }
        },
        modifier = modifier)
  }
}

@Composable
fun GetUserLocation(
    context: Context,
    onUserLocationChanged: (Location) -> Unit,
    locationProvider: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context),
) {
  val locationCallback =
      object : LocationCallback() {
        // 1
        override fun onLocationResult(result: LocationResult) {
          if (ActivityCompat.checkSelfPermission(
              context, Manifest.permission.ACCESS_FINE_LOCATION) !=
              PackageManager.PERMISSION_GRANTED &&
              ActivityCompat.checkSelfPermission(
                  context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                  PackageManager.PERMISSION_GRANTED) {
            // set default location
            val epflLocation = Location(46.519962, 6.56637, "EPFL")
            onUserLocationChanged(epflLocation)
            return
          }

          locationProvider.lastLocation
              .addOnSuccessListener { location ->
                location?.let {
                  val lat = location.latitude
                  val long = location.longitude
                  // Update data class with location data
                  onUserLocationChanged(Location(lat, long, "User"))
                }
              }
              .addOnFailureListener { Log.e("Location_error", "${it.message}") }
        }
      }

  DisposableEffect(key1 = locationProvider) {
    locationUpdate(locationProvider, locationCallback)

    onDispose { stopLocationUpdate(locationProvider, locationCallback) }
  }
}

@SuppressLint("MissingPermission")
private fun locationUpdate(
    locationProvider: FusedLocationProviderClient,
    locationCallback: LocationCallback
) {
  locationCallback.let {
    // An encapsulation of various parameters for requesting
    // location through FusedLocationProviderClient.
    val locationRequest: LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build()
    // use FusedLocationProviderClient to request location update
    locationProvider.requestLocationUpdates(locationRequest, it, Looper.getMainLooper())
  }
}

fun stopLocationUpdate(
    locationProvider: FusedLocationProviderClient,
    locationCallback: LocationCallback
) {
  try {
    // Removes all location updates for the given callback.
    val removeTask = locationProvider.removeLocationUpdates(locationCallback)
    removeTask.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        Log.d("LocationProvider", "Location Callback removed.")
      } else {
        Log.d("LocationProvider", "Failed to remove Location Callback.")
      }
    }
  } catch (se: SecurityException) {
    Log.e("LocationProvider", "Failed to remove Location Callback.. $se")
  }
}
