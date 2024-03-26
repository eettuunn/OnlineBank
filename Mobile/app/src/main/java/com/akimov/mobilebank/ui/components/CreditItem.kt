package com.akimov.mobilebank.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.akimov.mobilebank.R
import com.akimov.mobilebank.ui.accounts.CommonIcon
import com.akimov.mobilebank.ui.accounts.TextWithDescription
import com.akimov.mobilebank.ui.theme.MobileBankTheme

@Preview
@Composable
private fun PreviewCreditsList() {
    MobileBankTheme(darkTheme = false) {
        CreditItem(
            id = "2343",
            debt = "100000",
            monthlyPayment = "41.56",
            bankAccountName = "Основной счет"
        )
    }
}

@Composable
fun CreditItem(
    modifier: Modifier = Modifier,
    id: String,
    debt: String,
    monthlyPayment: String,
    bankAccountName: String
) {
    Surface(
        modifier = modifier,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Row(modifier = Modifier.padding(bottom = 16.dp)) {
                CommonIcon(
                    imageVector = ImageVector.vectorResource(
                        id = R.drawable.loan_ic
                    ),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp)
                        .size(24.dp),
                    isHidden = false
                )

                TextWithDescription(
                    stringResource(R.string.credit_in_sum) +
                            debt + stringResource(R.string.rubbles_short),
                    bankAccountName
                )
            }
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = R.drawable.take_money
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(24.dp)
                        .offset(y = (-8).dp)
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = monthlyPayment + stringResource(R.string.rubbles_short),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        platformStyle = PlatformTextStyle(includeFontPadding = false),
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }
    }
}