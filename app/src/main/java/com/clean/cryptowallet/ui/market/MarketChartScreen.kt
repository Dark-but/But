package com.clean.cryptowallet.ui.market

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clean.cryptowallet.data.market.MarketDataGenerator
import com.clean.cryptowallet.data.market.TraderTool

@Composable
fun MarketChartScreen() {
    val marketEngine = remember { MarketDataGenerator() }
    val candles = remember { marketEngine.getButtCoinMockHistory() }
    
    var selectedTool by remember { mutableStateOf(TraderTool.NONE) }
    var currentPrice by remember { mutableStateOf(1.52f) }
    
    // ट्रेडर द्वारा स्क्रीन पर मैनुअल खींची गई प्रेडिक्शन लाइन के कोऑर्डिनेट्स
    var lineStart by remember { mutableStateOf(Offset.Zero) }
    var lineEnd by remember { mutableStateOf(Offset.Zero) }
    
    val activeCandle = candles.last()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. लाइव टिकर हेडर
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("BUT / USD Pro Terminal", color = Color(0xFF94A3B8), fontSize = 12.sp)
                Text("$${String.format("%.2f", currentPrice)}", color = Color(0xFF10B981), fontSize = 26.sp, fontWeight = FontWeight.Black)
            }
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))) {
                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.End) {
                    Text("24h High: $1.55", color = Color(0xFF10B981), fontSize = 11.sp)
                    Text("24h Low: $1.18", color = Color(0xFFEF4444), fontSize = 11.sp)
                }
            }
        }

        // 2. ट्रेडर ड्राइंग टूलबॉक्स (मैनुअल प्रेडिक्शन फीचर्स)
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("Manual Analysis & Drawing Tools", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TraderTool.values().forEach { tool ->
                        Button(
                            onClick = { selectedTool = tool },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTool == tool) Color(0xFF0EA5E9) else Color(0xFF020617)
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(tool.name.replace("_", " "), fontSize = 9.sp)
                        }
                    }
                }
            }
        }

        // 3. प्रो कैंडलस्टिक इंटरएक्टिव चार्ट कैनवास (मैनुअल एडिट सपोर्ट के साथ)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(Color(0xFF020617), shape = RoundedCornerShape(8.dp))
                .pointerInput(selectedTool) {
                    // अगर कोई टूल सिलेक्टेड है, तो ट्रेडर स्क्रीन पर टच करके अपनी खुद की ट्रेंड लाइन बना सकता है
                    if (selectedTool != TraderTool.NONE) {
                        detectDragGestures(
                            onDragStart = { offset -> lineStart = offset },
                            onDrag = { change, dragAmount -> 
                                change.consume()
                                lineEnd = lineStart + dragAmount
                            }
                        )
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val candleWidth = canvasWidth / (candles.size * 2f)

                candles.forEachIndexed { index, candle ->
                    val x = (index * 2f + 0.5f) * candleWidth
                    val isBullish = candle.close >= candle.open
                    val candleColor = if (isBullish) Color(0xFF10B981) else Color(0xFFEF4444)

                    // डमी स्केलिंग फैक्टर्स (कैंडल को फिट करने के लिए)
                    val highY = canvasHeight * (1.6f - candle.high)
                    val lowY = canvasHeight * (1.6f - candle.low)
                    val openY = canvasHeight * (1.6f - candle.open)
                    val closeY = canvasHeight * (1.6f - candle.close)

                    // 1. विक (Wick/ठंडा) ड्रा करना
                    drawLine(color = candleColor, start = Offset(x, highY), end = Offset(x, lowY), strokeWidth = 2f)
                    // 2. बॉडी ड्रा करना
                    drawRect(
                        color = candleColor,
                        topLeft = Offset(x - (candleWidth / 2), Math.min(openY, closeY)),
                        size = androidx.compose.ui.geometry.Size(candleWidth, Math.abs(openY - closeY))
                    )
                }

                // ट्रेडर की खुद की प्रेडिक्शन मैनुअल लाइन लाइव ड्रा करना
                if (lineStart != Offset.Zero && lineEnd != Offset.Zero) {
                    drawLine(
                        color = when (selectedTool) {
                            TraderTool.TREND_LINE -> Color(0xFFF59E0B)
                            TraderTool.SUPPORT_RESISTANCE -> Color(0xFFEC4899)
                            else -> Color(0xFF38BDF8)
                        },
                        start = lineStart,
                        end = lineEnd,
                        strokeWidth = 4f
                    )
                }
            }
        }

        // 4. लाइव आर्डर फ्लो (बायर्स बनाम सेलर्स का लाइव डेटा मीटर)
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Live Order Flow Analysis", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Vol: ${activeCandle.volume} BUT", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
                
                // बायर vs सेलर लाइव बार
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(Color(0xFFEF4444), shape = RoundedCornerShape(7.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(activeCandle.buyVolumePercentage)
                            .background(Color(0xFF10B981), shape = RoundedCornerShape(topLeft = 7.dp, bottomLeft = 7.dp))
                    )
                    Box(modifier = Modifier.weight(100f - activeCandle.buyVolumePercentage))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Buyers (Inflows): ${activeCandle.buyVolumePercentage.toInt()}%", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text("Sellers (Outflows): ${(100f - activeCandle.buyVolumePercentage).toInt()}%", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
        
        // क्विक रीसेट बटन
        if (lineStart != Offset.Zero) {
            Text(
                text = "Clear Manual Drawings",
                color = Color(0xFF64748B),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { 
                        lineStart = Offset.Zero
                        lineEnd = Offset.Zero
                        selectedTool = TraderTool.NONE
                    }
            )
        }
    }
}
