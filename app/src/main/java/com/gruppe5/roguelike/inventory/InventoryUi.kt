package com.gruppe5.roguelike.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gruppe5.roguelike.GameConfig
import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.inventory.item_types.Consumable

//frame-assets sind 30x30, items 25x25 und sitzen +2px in x und y drin
private const val FRAME_PX = 30f
private const val ITEM_PX = 25f
private const val ITEM_OFFSET_PX = 2f

@Composable
fun InventoryDisplay(
    inventory: List<ItemInstance>,
    trinkets: List<ItemInstance>,
    equipment: Map<EquipSlot, ItemInstance>,
    onItemClick: (Int) -> Unit,
    onTrinketClick: (Int) -> Unit,
    onEquipClick: (EquipSlot) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            EquipSlot.entries.forEach { slot ->
                ItemSlot(
                    item = equipment[slot],
                    emptyIconResId = slot.iconResId,
                    onClick = { onEquipClick(slot) },
                    modifier = Modifier.weight(1f, false),
                )
            }
        }
        SlotRow(inventory, GameConfig.INVENTORY_SLOTS, R.drawable.sloticon_item, onItemClick)
        SlotRow(trinkets, GameConfig.TRINKET_SLOTS, R.drawable.sloticon_trinket, onTrinketClick)
    }
}

@Composable
private fun SlotRow(items: List<ItemInstance>, slots: Int, emptyIconResId: Int, onClick: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        repeat(slots) { index ->
            ItemSlot(
                item = items.getOrNull(index),
                emptyIconResId = emptyIconResId,
                onClick = { onClick(index) },
                modifier = Modifier.weight(1f, false),
            )
        }
    }
}

@Composable
private fun ItemSlot(
    item: ItemInstance?,
    emptyIconResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .width(70.dp) // acts as maximum width
            .aspectRatio(1f)
            .padding(4.dp)
            .clickable { onClick() },
    ) {
        val itemOffset = maxWidth * (ITEM_OFFSET_PX / FRAME_PX)
        val itemSize = maxWidth * (ITEM_PX / FRAME_PX)

        SlotImage(item?.rarity?.backdropResId ?: R.drawable.item_frame_slotbackdrop, Modifier.fillMaxSize())
        SlotImage(
            item?.imageResId ?: emptyIconResId,
            Modifier
                .offset(itemOffset, itemOffset)
                .size(itemSize),
        )
        SlotImage(R.drawable.item_frame, Modifier.fillMaxSize())

        if (item != null && item.definition is Consumable && item.usages > 1) {
            Text(
                text = item.usages.toString(),
                color = Color.White,
                fontSize = 10.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 6.dp, bottom = 4.dp),
            )
        }
    }
}

@Composable
private fun SlotImage(resId: Int, modifier: Modifier = Modifier) {
    Image(
        bitmap = ImageBitmap.imageResource(id = resId),
        contentDescription = null,
        filterQuality = FilterQuality.None, //pixel art ohne matsch
        modifier = modifier,
    )
}
