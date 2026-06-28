package com.gruppe5.roguelike.inventory.items

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.inventory.ItemInstance
import com.gruppe5.roguelike.inventory.item_types.StatBuffItem
import com.gruppe5.roguelike.property.Buff
import com.gruppe5.roguelike.property.StatModifier

fun Sword(): ItemInstance = ItemInstance(
    definition = StatBuffItem(
        label = "Sword",
        imageResId = R.drawable.entity_ranged,
        buff = Buff(
            duration = 3,
            statsMod = StatModifier(attack = 10)
        ),
        isPermanent = true
    ),
    initialUsages = 1
)
