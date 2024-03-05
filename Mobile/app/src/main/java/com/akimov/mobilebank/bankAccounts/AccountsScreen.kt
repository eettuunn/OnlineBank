package com.akimov.mobilebank.bankAccounts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akimov.mobilebank.R
import com.akimov.mobilebank.ui.theme.MobileBankTheme
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun AccountsScreen() {
    val viewModel = koinInject<AccountsViewModel>()
    val state by viewModel.state.collectAsState()
    AccountsScreenStateless(
        state = state,
        onChangeThemeClicked = remember(viewModel) {
            {
                viewModel.onIntent(UIIntent.UpdateTheme)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountsScreenStateless(
    state: AcountsScreenState,
    onChangeThemeClicked: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val isButtonVisible by remember {
        derivedStateOf {
            scrollState.value <= 0
        }
    }

    val sheetState: SheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopBar(
                userName = state.userName,
                isDarkTheme = state.isDarkTheme,
                onChangeThemeClicked = onChangeThemeClicked,
            )
        },
        content = { paddingValues ->
            AccountsList(paddingValues = paddingValues, scrollState = scrollState)
        },
        floatingActionButton = {
            if (isButtonVisible) {
                FloatingActionButton(
                    shape = CircleShape,
                    containerColor = Color(0xFF0990cb),
                    onClick = {
                        showBottomSheet = true
                    }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    )

    if (showBottomSheet) {
        BottomSheetActions(sheetState = sheetState, onDismissRequest = {
            showBottomSheet = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetActions(sheetState: SheetState, onDismissRequest: () -> Unit) {
    ModalBottomSheet(sheetState = sheetState, onDismissRequest = onDismissRequest) {
        SheetItem(
            headerIconResId = R.drawable.baseline_account_balance_24,
            messageResId = R.string.add_account
        )
        Divider(modifier = Modifier.padding(top = 8.dp, start = 56.dp))
        Spacer(modifier = Modifier.height(8.dp))
        SheetItem(
            headerIconResId = R.drawable.baseline_attach_money_24,
            messageResId = R.string.request_money
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SheetItem(headerIconResId: Int, messageResId: Int) {
    Row() {
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = ImageVector.vectorResource(id = headerIconResId),
            contentDescription = stringResource(messageResId),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = stringResource(id = messageResId),
            style = MaterialTheme.typography.headlineSmall
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(32.dp),
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = stringResource(R.string.arrow_right),
            )
        }
    }
}

@Composable
private fun AccountsList(paddingValues: PaddingValues, scrollState: ScrollState) {
    Column(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding())
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        repeat(10) {
            AccountItem(
                name = "Основной", balance = "100000 ₽",
                modifier = Modifier
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

enum class DragAnchors {
    Start, End
}

@Composable
private fun AccountItem(name: String, balance: String, modifier: Modifier = Modifier) {

    Row() {
        InfoCard(modifier = modifier, balance = balance, name = name)
        ActionIcons(
            modifier = Modifier
                .padding(top = 16.dp)
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun InfoCard(
    modifier: Modifier,
    balance: String,
    name: String
) {
    val density = LocalDensity.current

    val dragState: AnchoredDraggableState<DragAnchors> = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Start,
            positionalThreshold = { distanceBetweenAnchors: Float -> distanceBetweenAnchors * 0.5f },
            velocityThreshold = { with(density) { 1.dp.toPx() } },
            animationSpec = tween(durationMillis = 250)
        ).apply {
            updateAnchors(
                newAnchors = DraggableAnchors {
                    with(density) {
                        DragAnchors.Start at 0f
                        DragAnchors.End at -32.dp.toPx()
                    }
                }
            )
        }
    }
    Surface(
        modifier = modifier.then(
            Modifier
                .offset(x = dragState.requireOffset().dp, y = 0.dp)
                .anchoredDraggable(
                    orientation = Orientation.Horizontal,
                    state = dragState,
                )
        ),
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(bottom = 16.dp)) {
            CommonIcon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_currency_ruble_24),
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
                    .size(24.dp)
            )

            TextWithDescription(balance, name)

        }
    }
}

@Composable
fun CommonIcon(imageVector: ImageVector, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.then(
            Modifier.background(Color(0xFF0990cb), shape = CircleShape)
        ),
    ) {
        Icon(
            modifier = Modifier
                .padding(all = 4.dp)
                .size(24.dp),
            imageVector = imageVector,
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
private fun ActionIcons(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        CommonIcon(imageVector = Icons.Filled.Edit, modifier = Modifier.size(24.dp))
        CommonIcon(
            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_visibility_off_24),
            modifier = Modifier
                .padding(start = 16.dp)
                .size(24.dp)
        )
    }
}

@Composable
private fun TextWithDescription(balance: String, name: String) {
    Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp)) {
        Text(
            text = balance,
            style = MaterialTheme.typography.bodyMedium.copy(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                fontWeight = FontWeight.Bold,
            ),
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(
            ),
        )
    }
}

@Preview(showBackground = true, device = "id:pixel")
@Composable
fun AccountItemPreview() {
    MobileBankTheme {
        AccountItem(
            name = "Основной",
            balance = "100000 ₽",
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    userName: String,
    isDarkTheme: Boolean,
    onChangeThemeClicked: () -> Unit,
) {
    TopAppBar(
        modifier = Modifier
            .shadow(elevation = 4.dp),
        title = {
            NameRow(userName = userName)
        },
        actions = {
            ChangeThemeIcon(onChangeThemeClicked, isDarkTheme)
        },
    )
}

@Composable
private fun ChangeThemeIcon(onChangeThemeClicked: () -> Unit, isDarkTheme: Boolean) {

    var isVisible by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = isDarkTheme) {
        delay(500L)
        isVisible = true
    }

    AnimatedVisibility(visible = isVisible) {
        Icon(
            modifier = Modifier
                .padding(end = 16.dp)
                .size(24.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    isVisible = false
                    onChangeThemeClicked()
                },
            imageVector = if (isDarkTheme) {
                ImageVector.vectorResource(id = R.drawable.ic_sun_32)
            } else {
                ImageVector.vectorResource(
                    id = R.drawable.ic_moon_32,
                )
            },
            contentDescription = stringResource(R.string.theme_switcher),
        )
    }
}

@Composable
private fun NameRow(userName: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .padding(start = 16.dp)
                .size(24.dp),
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = stringResource(R.string.user_icon),
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = userName,
            style = MaterialTheme.typography.headlineSmall,
        )
        Icon(
            modifier = Modifier
                .padding(start = 4.dp)
                .size(24.dp),
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = stringResource(R.string.arrow_right),
        )
    }
}

@Preview
@Composable
fun TopBarPreview() {
    MobileBankTheme {
        TopBar(
            "Максим",
            onChangeThemeClicked = {},
            isDarkTheme = true,
        )
    }
}

@Preview(showBackground = true, device = "id:pixel")
@Composable
fun AccountsScreenPreview() {
    MobileBankTheme {
        AccountsScreenStateless(
            state = AcountsScreenState(
                userName = "Максим",
                isDarkTheme = true,
            ),
            onChangeThemeClicked = {},
        )
    }
}

@Preview(showBackground = true, device = "id:pixel")
@Composable
fun AccountsScreenPreviewDark() {
    MobileBankTheme(darkTheme = true) {
        AccountsScreenStateless(
            state = AcountsScreenState(
                userName = "Максим",
                isDarkTheme = true,
            ),
            onChangeThemeClicked = {},
        )
    }
}
