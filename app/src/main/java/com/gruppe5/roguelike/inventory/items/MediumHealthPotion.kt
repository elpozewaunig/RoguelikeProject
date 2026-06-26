package com.gruppe5.roguelike.inventory.items

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.inventory.item_types.HealAbsoluteItem

class MediumHealthPotion : HealAbsoluteItem(
    label = "Health Potion",
    imageResId = R.drawable.entity_miku,
    healthAmount = 75
)
