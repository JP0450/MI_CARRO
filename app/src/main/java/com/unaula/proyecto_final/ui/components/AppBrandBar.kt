package com.unaula.proyecto_final.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unaula.proyecto_final.ui.theme.ColorBorderMint
import com.unaula.proyecto_final.ui.theme.ColorMintAqua
import com.unaula.proyecto_final.ui.theme.ColorTextSecondary
import com.unaula.proyecto_final.ui.theme.ColorDeepGreen
import com.unaula.proyecto_final.ui.theme.ColorTextPrimary

@Composable
fun AppBrandBar(
    onBack: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(64.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = ColorDeepGreen
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(10.dp),
            color = ColorMintAqua
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = "MI CARRO",
                tint = ColorDeepGreen,
                modifier = Modifier.padding(7.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "MI CARRO",
                color = ColorTextPrimary,
                fontSize = 16.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "TRANSPORTES ANDINA",
                color = ColorTextSecondary,
                fontSize = 10.sp,
                lineHeight = 12.sp
            )
        }
        if (onSettingsClick != null) {
            IconButton(onClick = onSettingsClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuración",
                    tint = ColorDeepGreen
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
        Surface(
            shape = CircleShape,
            color = ColorMintAqua,
            border = BorderStroke(1.dp, ColorBorderMint)
        ) {
            Text(
                text = "AM",
                color = ColorDeepGreen,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 8.dp)
            )
        }
    }
}
