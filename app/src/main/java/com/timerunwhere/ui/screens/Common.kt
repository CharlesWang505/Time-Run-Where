package com.timerunwhere.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.timerunwhere.ui.theme.Panel
import com.timerunwhere.ui.theme.PanelStroke
import com.timerunwhere.ui.theme.ProductiveCyan
import com.timerunwhere.ui.theme.PureBlack
import com.timerunwhere.ui.theme.SoftWhite

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable Column.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Panel),
        border = BorderStroke(1.dp, PanelStroke)
    ) {
        Column(modifier = Modifier.padding(18.dp), content = content)
    }
}

@Composable
fun ScreenTitle(title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = SoftWhite,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = subtitle,
            color = Color(0xFF9EA3AA),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ProductiveCyan,
            contentColor = PureBlack,
            disabledContainerColor = Color(0xFF334044),
            disabledContentColor = Color(0xFFB2B7BA)
        )
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GhostButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, ProductiveCyan)
    ) {
        Text(text)
    }
}

@Composable
fun GradientBar(progress: Float, colors: List<Color>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
            .background(Color(0xFF333333), RoundedCornerShape(8.dp))
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(10.dp)
                .background(Brush.horizontalGradient(colors), RoundedCornerShape(8.dp))
        )
    }
}
