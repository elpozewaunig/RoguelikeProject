package com.gruppe5.roguelike.inventory.items

import com.gruppe5.roguelike.inventory.item_types.StatBuffItem
import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.property.Buff
import com.gruppe5.roguelike.property.StatModifier

class OPStrengthPotion: StatBuffItem(
    label = "Strength Potion",
    imageResId = R.drawable.entity_miku,
    buff = Buff(
        duration = 3,
        statsMod = StatModifier(attack = 100) //3 Turns viel zu stark zum testen
    )
)

