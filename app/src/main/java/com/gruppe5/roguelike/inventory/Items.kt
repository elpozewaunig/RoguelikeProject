package com.gruppe5.roguelike.inventory

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.inventory.item_types.HealAbsoluteItem
import com.gruppe5.roguelike.inventory.item_types.StatBuffItem
import com.gruppe5.roguelike.property.Buff
import com.gruppe5.roguelike.property.StatModifier

object Items {
    fun sword() = ItemInstance(
        StatBuffItem(
            label = "Sword",
            imageResId = R.drawable.item_leek,
            buff = Buff(duration = 3, statsMod = StatModifier(attack = 10)),
            isPermanent = true,
        ),
        usages = 1,
    )

    fun mediumHealthPotion() = ItemInstance(
        HealAbsoluteItem(
            label = "Health Potion",
            imageResId = R.drawable.item_health_potion,
            healthAmount = 75,
        ),
        usages = 3,
    )

    fun opStrengthPotion() = ItemInstance(
        StatBuffItem(
            label = "Strength Potion",
            imageResId = R.drawable.item_strength_potion,
            buff = Buff(duration = 3, statsMod = StatModifier(attack = 100)), //3 Turns viel zu stark zum testen
        ),
        usages = 1,
    )
}
