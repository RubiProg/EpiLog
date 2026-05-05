package com.epilog.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SeccionTitulo(titulo: String, modifier: Modifier = Modifier) {
    Text(
        text = titulo,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SelectorBoton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    subtext: String? = null,
    isSmall: Boolean = false,
    onClick: () -> Unit
) {
    val verdePrimary = Color(0xFF0C6445) // Consistent with the app's green
    Surface(
        modifier = modifier
            .clickable { onClick() }
            .then(if (isSmall) Modifier.wrapContentWidth() else Modifier.fillMaxWidth()),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) verdePrimary else Color.White,
        border = if (selected) null else BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(vertical = if (isSmall) 8.dp else 10.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = if (selected) Color.White else Color.DarkGray,
                fontSize = if (isSmall) 13.sp else 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
            if (subtext != null) {
                Text(
                    text = subtext,
                    color = if (selected) Color(0xFFB0D5C1) else Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}
