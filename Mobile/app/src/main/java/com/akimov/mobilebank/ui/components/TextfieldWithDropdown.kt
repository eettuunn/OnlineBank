package com.akimov.mobilebank.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.akimov.mobilebank.R
import kotlinx.collections.immutable.ImmutableList

@Composable
fun <T> ButtonWithExposedDropDownMenu(
    modifier: Modifier = Modifier,
    listOfParameters: ImmutableList<T>,
    label: String,
    onItemClick: (index: Int, item: T) -> Unit,
    isHighlighted: Boolean = false,
    selectedParameter: T? = null,
    selectedParameterName: String? = null,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    var dropDownExpanded by remember { mutableStateOf(false) }

    val icon =
        if (dropDownExpanded) {
            Icons.Filled.KeyboardArrowUp
        } else {
            Icons.Filled.KeyboardArrowDown
        }

    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val currSelected = if (selectedParameter == null) "" else "$selectedParameter"

    Column(modifier = modifier) {
        TextField(
            value = selectedParameterName ?: currSelected,
            onValueChange = {},
            label = {
                Text(
                    text = label,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            leadingIcon = leadingIcon,
            modifier =
            Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                }
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    if (enabled) {
                        dropDownExpanded = !dropDownExpanded
                    }
                },
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            },
            colors = TextFieldDefaults.colors(
                disabledContainerColor = MaterialTheme.colorScheme.tertiary,
                focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color(0xFF0990cb),
                disabledIndicatorColor = Color.Transparent,
                disabledLabelColor = MaterialTheme.colorScheme.onSurface,
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = false,
            singleLine = true,
            maxLines = 1,
        )

        DropdownMenu(
            expanded = dropDownExpanded,
            onDismissRequest = { dropDownExpanded = false },
            modifier =
            Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                .heightIn(0.dp, 256.dp)
                .background(MaterialTheme.colorScheme.tertiary)
        ) {
            listOfParameters.forEachIndexed { index, item ->
                Column {
                    Row(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                dropDownExpanded = false
                                onItemClick(index, item)
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier =
                            Modifier
                                .padding(top = 8.dp, bottom = 7.dp)
                                .weight(1f),
                            text = "$item",
                            textAlign = TextAlign.Center,
                        )

                        if (item == selectedParameter) {
                            Icon(
                                painter =
                                painterResource(
                                    R.drawable.selected_in_dropdown,
                                ),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                        }
                    }
                    if (index != listOfParameters.size - 1)
                        Divider()
                }
            }
        }
    }
}