package com.example.appifood_movil.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(15.dp),
        color = if (isSelected) Color(0xFFFF4B3A) else Color.White,
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) Color(0xFFFF4B3A) else Color(0xFFFEECEB)
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp),
            color = if (isSelected) Color.White else Color(0xFFFF4B3A),
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}