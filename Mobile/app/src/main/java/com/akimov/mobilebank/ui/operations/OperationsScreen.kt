package com.akimov.mobilebank.ui.operations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akimov.mobilebank.R
import com.akimov.mobilebank.data.models.OperationType
import com.akimov.mobilebank.domain.OperationUI
import com.akimov.mobilebank.ui.accounts.CommonIcon
import com.akimov.mobilebank.ui.accounts.InputDataContent
import com.akimov.mobilebank.ui.accounts.TextWithDescription
import com.akimov.mobilebank.ui.components.ButtonWithExposedDropDownMenu
import com.akimov.mobilebank.ui.theme.MobileBankTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.koinInject

@Composable
fun OperationsScreen(
    accountID: String,
    onNavigateBack: () -> Unit,
) {
    val viewModel = koinInject<OperationsViewModel>()
    val operations by viewModel.operations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val accountName by viewModel.accountName.collectAsState()

    OperationsStateless(
        operations = operations,
        loading = isLoading,
        accountName = accountName,
        onRefresh = remember(viewModel) { {} },
        onNavigateBack = onNavigateBack,
        onCompleteClick = remember(viewModel) {
            { amount: String, operationType: OperationType ->
                viewModel.reduce(
                    OperationsScreenIntent.OnAddOperationClicked(
                        amount = amount,
                        operationType = operationType,
                        accountId = accountID
                    )
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OperationsStateless(
    operations: Map<String, List<OperationUI>>,
    loading: Boolean,
    accountName: String,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit,
    onCompleteClick: (String, OperationType) -> Unit,
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = loading)
    var showActionsBottomSheet by remember { mutableStateOf(false) }

    SwipeRefresh(state = swipeRefreshState, onRefresh = onRefresh) {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.shadow(4.dp),
                    title = {
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = stringResource(R.string.operations),
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        Icon(
                            modifier = Modifier.clickable {
                                onNavigateBack()
                            },
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    shape = CircleShape,
                    containerColor = Color(0xFF0990cb),
                    onClick = {
                        showActionsBottomSheet = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        ) { paddingValues ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding(), start = 16.dp, end = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                operations.entries.forEach { (date, operations) ->
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = date,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    operations.forEach { operation ->
                        OperationItem(operation, accountName)
                    }
                }
            }
        }
    }

    if (showActionsBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showActionsBottomSheet = false }) {
            val operationTypes = OperationType.entries.map { it.name }
            var selectedStringOperationType by remember { mutableStateOf(operationTypes[0]) }
            var selectedOperationTypeEnum by remember { mutableStateOf(OperationType.entries[0]) }

            ButtonWithExposedDropDownMenu(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                listOfParameters = operationTypes.toImmutableList(),
                selectedParameter = selectedStringOperationType,
                label = stringResource(R.string.payment),
                onItemClick = { index, item ->
                    selectedStringOperationType = item
                    selectedOperationTypeEnum = OperationType.valueOf(item)
                },
            )

            InputDataContent(
                onCompleteClick = {
                    showActionsBottomSheet = false
                    onCompleteClick(it, selectedOperationTypeEnum)
                },
                labelText = stringResource(R.string.operation_amount),
                keyboardType = KeyboardType.Number
            )
        }
    }
}

@Composable
fun OperationItem(operation: OperationUI, accountName: String) {
    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
    ) {
        CommonIcon(
            imageVector = ImageVector.vectorResource(
                id = R.drawable.baseline_currency_ruble_24
            ),
            modifier = Modifier
                .padding(top = 16.dp)
                .size(24.dp),
            isHidden = false
        )

        val mainText = when (operation.transactionType) {
            OperationType.DEPOSIT -> stringResource(R.string.deposit)
            OperationType.WITHDRAW -> stringResource(R.string.withdraw)
            OperationType.TAKE_LOAN -> stringResource(R.string.take_loan_message)
            OperationType.REPAY_LOAN -> stringResource(R.string.repay_loan)
        }
        TextWithDescription(mainText, operation.additionalInformation)
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                val isDeposite = remember(operation.transactionType) {
                    (
                            (operation.transactionType == OperationType.DEPOSIT) ||
                                    operation.transactionType == OperationType.TAKE_LOAN
                            )
                }

                val prefixText = remember(isDeposite) { if (isDeposite) "+" else "" }
                val color = remember(isDeposite) {
                    if (isDeposite) Color(0xFF18A558) else Color.Unspecified
                }

                Text(
                    text = prefixText + operation.amount.toString() + stringResource(R.string.rubbles_with_whitespace),
                    style = MaterialTheme.typography.bodyMedium,
                    color = color
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = accountName,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOperationsScreen() {
    MobileBankTheme {
        OperationsStateless(
            operations = mapOf(
                "9 марта" to listOf(
                    OperationUI(
                        id = "1",
                        transactionDate = "9 марта",
                        amount = 100,
                        additionalInformation = "Additional information",
                        transactionType = OperationType.DEPOSIT,
                        bankAccountId = "1"
                    )
                )
            ),
            loading = false,
            accountName = "Account",
            onRefresh = {},
            onNavigateBack = {},
            onCompleteClick = { _, _ -> }
        )
    }
}
