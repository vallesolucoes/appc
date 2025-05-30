package com.example.httpserver.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.httpserver.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class NavTab {
    USB, WEB, TANK
}

@Composable
fun DiscreteNavBar(
    currentTab: NavTab,
    onTabChange: (NavTab) -> Unit,
    onUnlockPressed: () -> Unit,
    onAdminPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dClickCount by remember { mutableIntStateOf(0) }
    var adminClickCount by remember { mutableIntStateOf(0) }
    var showAdmin by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            // Tabs de navegação
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tab USB
                TabButton(
                    text = "USB",
                    isSelected = currentTab == NavTab.USB,
                    onClick = { onTabChange(NavTab.USB) }
                )
                
                // Tab Web
                TabButton(
                    text = "WEB",
                    isSelected = currentTab == NavTab.WEB,
                    onClick = { onTabChange(NavTab.WEB) }
                )
                
                // Tab Tank
                TabButton(
                    text = "TANK",
                    isSelected = currentTab == NavTab.TANK,
                    onClick = { onTabChange(NavTab.TANK) }
                )
            }

            // Área central com botões de controle
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                
                // Botão D para desbloqueio
                Button(
                    onClick = { 
                        dClickCount++
                        if (dClickCount >= 4) {
                            onUnlockPressed()
                            dClickCount = 0
                        } else {
                            // Reset counter after 2 seconds if not completed
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(2000)
                                dClickCount = 0
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    modifier = Modifier.size(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "D",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Indicador de progresso do D
                if (dClickCount > 0) {
                    Text(
                        text = "${dClickCount}/4",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }

                // Botão admin escondido
                if (showAdmin) {
                    Button(
                        onClick = onAdminPressed,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        modifier = Modifier.size(30.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "A",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Área para ativar admin (invisível)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        adminClickCount++
                        if (adminClickCount >= 10) {
                            showAdmin = true
                        }
                    }
            )
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isSelected) Color(0xFF2E7D32) else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
} 