package com.akimov.mobilebank.bankAccounts

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akimov.mobilebank.R
import com.akimov.mobilebank.data.models.BankAccountNetwork
import com.akimov.mobilebank.ui.theme.MobileBankTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

@Composable
fun AccountsScreen() {
    val viewModel = koinInject<AccountsViewModel>()
    val state by viewModel.state.collectAsState()

    val hostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = true) {
        viewModel.actions.collect { action ->
            when (action) {
                is ViewAction.ShowError -> hostState.showSnackbar(message = action.message)
            }
        }
    }

    AccountsScreenStateless(
        state = state,
        hostState = hostState,
        onChangeThemeClicked = remember(viewModel) {
            {
                viewModel.onIntent(UIIntent.UpdateTheme)
            }
        },
        reduce = remember(viewModel) {
            { intent ->
                viewModel.onIntent(intent)
            }
        }
    )
}

@Composable
private fun AccountsScreenStateless(
    state: AccountsScreenState,
    onChangeThemeClicked: () -> Unit,
    hostState: SnackbarHostState? = null,
    reduce: (UIIntent) -> Unit,
) {
    when (state) {
        is AccountsScreenState.Content -> AccountsContent(
            state = state,
            onChangeThemeClicked = onChangeThemeClicked,
            hostState = hostState,
            reduce = reduce
        )

        AccountsScreenState.Loading -> LoadingScreen()
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color(0xFF0990cb))
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AccountsContent(
    state: AccountsScreenState.Content,
    onChangeThemeClicked: () -> Unit,
    hostState: SnackbarHostState?,
    reduce: (UIIntent) -> Unit,
) {
    val scrollState = rememberScrollState()

    val sheetState: SheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    Scaffold(topBar = {
        TopBar(
            userName = state.userName,
            isDarkTheme = state.isDarkTheme,
            onChangeThemeClicked = onChangeThemeClicked,
        )
    }, content = { paddingValues ->
        AccountsList(
            paddingValues = paddingValues,
            scrollState = scrollState,
            accountsList = state.accountsList
        )
    }, floatingActionButton = {
        FloatingActionButton(shape = CircleShape, containerColor = Color(0xFF0990cb), onClick = {
            showBottomSheet = true
        }) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = Color.White
            )
        }
    }, snackbarHost = {
        hostState?.let {
            SnackbarHost(hostState = it) {
                CommonSnackBar(it)
            }
        }
    })

    if (showBottomSheet) {
        BottomSheetActions(
            sheetState = sheetState,
            onDismissRequest = {
                showBottomSheet = false
            },
            reduce = reduce
        )
    }
}

@Composable
private fun CommonSnackBar(it: SnackbarData) {
    Snackbar(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = it.visuals.message)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetActions(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    contentSheetState: ContentSheetState = ContentSheetState.ACTIONS,
    reduce: (UIIntent) -> Unit
) {
    ModalBottomSheet(sheetState = sheetState, onDismissRequest = onDismissRequest) {
        var currentState by remember(contentSheetState) {
            mutableStateOf(contentSheetState)
        }
        when (currentState) {
            ContentSheetState.ACTIONS -> ActionsContent(onAddAccountClicked = {
                currentState = ContentSheetState.CREATE_ACCOUNT
            })

            ContentSheetState.CREATE_ACCOUNT -> CreateAccountContent(reduce = {
                onDismissRequest()
                reduce(it)
            })

            ContentSheetState.CHANGE_BALANCE -> TODO()
            ContentSheetState.GET_LOAN -> TODO()
        }
    }
}

@Composable
private fun CreateAccountContent(reduce: (UIIntent) -> Unit) {
    var text by remember {
        mutableStateOf("")
    }
    Spacer(modifier = Modifier.height(16.dp))

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(key1 = true) {
        focusRequester.requestFocus()
    }

    TextField(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .focusRequester(focusRequester),
        value = text,
        onValueChange = {
            text = it
        },
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.tertiary,
            unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color(0xFF0990cb),
        ),
        trailingIcon = {
            if (text.isNotEmpty()) {
                Icon(
                    modifier = Modifier.clickable {
                        text = ""
                    },
                    imageVector = Icons.Filled.Clear,
                    contentDescription = null,
                )
            }
        },
        placeholder = {
            Text(text = stringResource(R.string.account_name))
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        onClick = { reduce.invoke(UIIntent.OpenAccount(text)) },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0990cb),
            contentColor = Color.White
        )
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = stringResource(R.string.create_account),
        )
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Preview
@Composable
private fun CreateAccountContentPreview() {
    MobileBankTheme {
        Column {
            CreateAccountContent(reduce = {})
        }
    }
}

enum class ContentSheetState {
    ACTIONS, CREATE_ACCOUNT, CHANGE_BALANCE, GET_LOAN
}

@Composable
private fun ActionsContent(
    onAddAccountClicked: () -> Unit,
) {
    SheetItem(
        headerIconResId = R.drawable.baseline_account_balance_24,
        messageResId = R.string.add_account,
        onClick = onAddAccountClicked
    )
    Divider(modifier = Modifier.padding(top = 8.dp, start = 56.dp))
    Spacer(modifier = Modifier.height(8.dp))
    SheetItem(
        headerIconResId = R.drawable.baseline_attach_money_24,
        messageResId = R.string.request_money,
        onClick = {}
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun SheetItem(headerIconResId: Int, messageResId: Int, onClick: () -> Unit) {
    Row(modifier = Modifier.clickable { onClick() }) {
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
private fun AccountsList(
    paddingValues: PaddingValues,
    scrollState: ScrollState,
    accountsList: ImmutableList<BankAccountNetwork>
) {
    Column(
        modifier = Modifier
            .padding(top = paddingValues.calculateTopPadding())
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        accountsList.forEach {
            AccountInfoCard(
                name = it.name,
                balance = it.balance.toString(),
                modifier = Modifier
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        HiddenAccounts(
            modifier = Modifier.padding(top = 8.dp),
            hiddenAccounts = accountsList
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun HiddenAccounts(
    modifier: Modifier = Modifier,
    hiddenAccounts: ImmutableList<BankAccountNetwork>
) {
    var visible by remember {
        mutableStateOf(false)
    }
    val expandTime by remember {
        mutableIntStateOf(450)
    }
    val enterTransition = remember {
        expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(expandTime),
        ) + fadeIn(
            initialAlpha = 0.3f,
            animationSpec = tween(expandTime),
        )
    }
    val exitTransition = remember {
        shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(expandTime),
        ) + fadeOut(
            animationSpec = tween(expandTime),
        )
    }
    val transitionState =
        remember {
            MutableTransitionState(visible).apply {
                targetState = !visible
            }
        }

    val transition = updateTransition(transitionState, label = "")
    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = expandTime)
    }, label = "") {
        if (visible) 180f else 0f
    }

    Column {
        Row(
            modifier
                .padding(start = 16.dp, bottom = 16.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    visible = !visible
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_visibility_off_24),
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = stringResource(R.string.hidden),
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .rotate(arrowRotationDegree),
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null
            )
        }
        AnimatedVisibility(
            visibleState = MutableTransitionState(visible),
            enter = enterTransition,
            exit = exitTransition,
        ) {
            Column {
                hiddenAccounts.forEach {
                    AccountInfoCard(
                        name = it.name,
                        balance = it.balance.toString(),
                        modifier = Modifier
                            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                            .fillMaxWidth(),
                        isHidden = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

enum class DragAnchors {
    Start, End
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun AccountInfoCard(
    modifier: Modifier = Modifier,
    balance: String,
    name: String,
    isHidden: Boolean = false
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
                        DragAnchors.End at -48.dp.toPx()
                    }
                }
            )
        }
    }

    val actionsVisibility by remember(dragState.progress, dragState.targetValue) {
        derivedStateOf {
            (dragState.targetValue == DragAnchors.End) && (dragState.progress == 1f)
        }
    }
    Box {
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
                    imageVector = ImageVector.vectorResource(
                        id = R.drawable.baseline_currency_ruble_24
                    ),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp)
                        .size(24.dp),
                    isHidden = isHidden
                )

                TextWithDescription(balance, name)
            }
        }
        AnimatedVisibility(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    end = 16.dp
                )
                .align(Alignment.TopEnd),
            visible = actionsVisibility
        ) {
            ActionIcons(isHidden = isHidden)
        }
    }
}

@Composable
fun CommonIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    isHidden: Boolean = false
) {
    Box(
        modifier = modifier.then(
            Modifier.background(
                color = if (!isHidden) Color(color = 0xFF0990cb) else Color(0xFFA0A2A7),
                shape = CircleShape
            )
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
private fun ActionIcons(modifier: Modifier = Modifier, isHidden: Boolean = false) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        CommonIcon(imageVector = Icons.Filled.Edit, modifier = Modifier.size(24.dp))
        CommonIcon(
            imageVector = ImageVector.vectorResource(
                id = if (!isHidden) R.drawable.baseline_visibility_off_24 else R.drawable.baseline_visibility_24
            ),
            modifier = Modifier
                .padding(start = 16.dp)
                .size(24.dp)
        )
        CommonIcon(
            imageVector = Icons.Filled.Delete,
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
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Preview(showBackground = true, device = "id:pixel")
@Composable
fun AccountItemPreview() {
    MobileBankTheme {
        AccountInfoCard(
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
        modifier = Modifier.shadow(elevation = 4.dp),
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
        AccountsContent(
            state = AccountsScreenState.Content(
                userName = "Максим",
                isDarkTheme = true,
                accountsList = persistentListOf()
            ),
            onChangeThemeClicked = {},
            hostState = null,
            reduce = {}
        )
    }
}

@Preview(showBackground = true, device = "id:pixel")
@Composable
fun AccountsScreenPreviewDark() {
    MobileBankTheme(darkTheme = true) {
        AccountsContent(
            state = AccountsScreenState.Content(
                userName = "Максим",
                isDarkTheme = true,
                accountsList = persistentListOf()
            ),
            onChangeThemeClicked = {},
            hostState = null,
            reduce = {}
        )
    }
}
