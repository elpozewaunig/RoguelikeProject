package com.gruppe5.roguelike.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import com.gruppe5.roguelike.GameConfig

@Composable
fun InventoryDisplay(
    inventory: List<InventoryItem>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    slots: Int = GameConfig.INVENTORY_SLOTS

) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xAA111111)),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(slots) { index ->
            val item = inventory.getOrNull(index)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(1.dp, Color(0x66FFFFFF))
                    .background(Color(0x66111111))
                    .clickable { onItemClick(index) },
                contentAlignment = Alignment.Center
            ) {
                if (item != null) {
                    InventoryItemView(item)
                }
            }
        }
    }
}

@Composable
fun InventoryItemView(item: InventoryItem) { //Damits nid derselbe Name wie die Klasse ist
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = item.imageResId),
            contentDescription = item.label,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = item.usages.toString(),
            color = Color.White,
            fontSize = 10.sp
        )
    }
}
