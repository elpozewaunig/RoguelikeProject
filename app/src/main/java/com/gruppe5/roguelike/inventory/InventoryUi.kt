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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import com.gruppe5.roguelike.GameConfig

@Composable
fun InventoryDisplay(
    inventory: List<ItemInstance>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    slots: Int = GameConfig.INVENTORY_SLOTS

) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(slots) { index ->
            val item = inventory.getOrNull(index)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .border(1.dp, Color.Gray)
                    .background(Color.DarkGray)
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
fun InventoryItemView(item: ItemInstance) { //Damits nid derselbe Name wie die Klasse ist
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            bitmap = ImageBitmap.imageResource(id = item.imageResId),
            filterQuality = FilterQuality.None,
            contentDescription = item.label,
            modifier = Modifier.fillMaxSize(0.5f),
        )
        Text(
            text = if(item.isPermanent) "Permanent" else item.usages.toString(),
            color = Color.White,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxHeight(0.5f)
        )
    }
}
