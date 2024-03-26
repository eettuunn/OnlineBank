package com.akimov.mobilebank.ui.loan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.akimov.mobilebank.R
import com.akimov.mobilebank.ui.components.ButtonWithExposedDropDownMenu
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.koinInject

@Composable
fun GetLoanScreen(navigateBack: () -> Unit) {
    val viewModel = koinInject<LoanViewModel>()
    val accounts by remember { viewModel.accounts }
    val rates by remember { viewModel.rates }

    if (accounts.isNotEmpty() && rates.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val names = accounts.map { it.name }
            var selectedAccountName: String by remember {
                mutableStateOf(names.first())
            }
            var selectedAccountIndex: Int by remember {
                mutableIntStateOf(0)
            }

            Accounts(
                names = names,
                selectedAccount = selectedAccountName,
                onSelection = { index: Int, item: String ->
                    selectedAccountName = item
                    selectedAccountIndex = index
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Ставка
            val ratesValues =
                rates.map { it.interestRate.toString() + stringResource(R.string.percent) }
                    .toImmutableList()
            var selectedRate by remember {
                mutableStateOf(ratesValues.first())
            }
            var selectedRateIndex by remember {
                mutableIntStateOf(0)
            }
            Rates(
                ratesValues = ratesValues,
                selectedRate = selectedRate,
                onSelection = { index: Int, item: String ->
                    selectedRate = item
                    selectedRateIndex = index
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Срок
            val monthValues = mutableListOf<String>()
            val monthNumbers = mutableListOf<Int>()
            repeat(12) {
                monthValues.add((it + 1).toString() + " " + stringResource(R.string.months))
                monthNumbers.add(it + 1)
            }

            var selectedMonth by remember {
                mutableStateOf(monthValues.first())
            }
            var selectedMonthIndex by remember {
                mutableIntStateOf(0)
            }
            Months(
                selectedMonth = selectedMonth,
                onSelection = { index: Int, item: String ->
                    selectedMonth = item
                    selectedMonthIndex = index
                },
                monthValues = monthValues
            )
            Spacer(modifier = Modifier.height(16.dp))

            var summ by remember {
                mutableStateOf("")
            }

            TextField(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                    .fillMaxWidth(),
                value = summ, onValueChange = {
                    summ = it
                },
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                    selectionColors = TextSelectionColors(
                        handleColor = Color(0xFF0990cb),
                        backgroundColor = Color(0xFF0990cb),
                    ),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFF0990cb),
                ),
                trailingIcon = {
                    if (summ.isNotEmpty()) {
                        Icon(
                            modifier = Modifier.clickable {
                                summ = ""
                            },
                            imageVector = Icons.Filled.Clear,
                            contentDescription = null,
                        )
                    }
                }, label = {
                    Text(
                        text = stringResource(id = R.string.sum),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }, singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = ImageVector.vectorResource(
                            id = R.drawable.money_bag_svgrepo_com
                        ),
                        contentDescription = null
                    )
                },
                suffix = {
                    Text(
                        text = stringResource(id = R.string.rubbles_short),
                    )
                }
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .fillMaxWidth(),
                    onClick = {
                        viewModel.getCredit(
                            accountId = accounts[selectedAccountIndex].id.toString(),
                            rateId = rates[selectedRateIndex].id,
                            amount = summ.toInt(),
                            months = monthNumbers[selectedMonthIndex]
                        )
                        navigateBack()
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0990cb),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = stringResource(R.string.take_loan),
                    )
                }
            }
        }
    }
}

@Composable
private fun Accounts(
    names: List<String>,
    selectedAccount: String,
    onSelection: (index: Int, item: String) -> Unit
) {
    ButtonWithExposedDropDownMenu(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        listOfParameters = names.toImmutableList(),
        selectedParameter = selectedAccount,
        label = stringResource(R.string.select_account),
        onItemClick = { index, item ->
            onSelection(index, item)
        },
        leadingIcon = {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(
                    id = R.drawable.baseline_currency_ruble_24
                ),
                contentDescription = null
            )
        }
    )
}

@Composable
private fun Rates(
    ratesValues: List<String>,
    selectedRate: String,
    onSelection: (index: Int, item: String) -> Unit
) {

    ButtonWithExposedDropDownMenu(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        listOfParameters = ratesValues.toImmutableList(),
        selectedParameter = selectedRate,
        label = stringResource(R.string.tarif),
        onItemClick = onSelection,
        leadingIcon = {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(
                    id = R.drawable.percent_svgrepo_com
                ),
                contentDescription = null
            )
        }
    )
}

@Composable
private fun Months(
    selectedMonth: String,
    onSelection: (index: Int, item: String) -> Unit,
    monthValues: List<String>
) {
    ButtonWithExposedDropDownMenu(
        modifier = Modifier
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .fillMaxWidth(),
        listOfParameters = monthValues.toImmutableList(),
        selectedParameter = selectedMonth,
        label = stringResource(R.string.time),
        onItemClick = onSelection,
        leadingIcon = {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(
                    id = R.drawable.time_svgrepo_com
                ),
                contentDescription = null
            )
        }
    )
}

//@Preview(showBackground = true)
//@Composable
//private fun PreviewGetLoanContent() {
//    MobileBankTheme(darkTheme = false) {
//        Column(Modifier.fillMaxSize()) {
//            GetLoanContent()
//        }
//    }
//}