package com.gruppe5.roguelike.inventory

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.inventory.item_types.HealAbsoluteItem
import com.gruppe5.roguelike.inventory.item_types.StatBuffItem
import com.gruppe5.roguelike.property.Buff
import com.gruppe5.roguelike.property.StatModifier

object Items {
    fun sword() = ItemInstance(
        definition = StatBuffItem(
            label = "Sword",
            imageResId = R.drawable.item_leek,
            buff = Buff(
                duration = 3,
                statsMod = StatModifier(attack = 10)
            ),
            isPermanent = true
        ),
        initialUsages = 1
    )
    fun mediumHealthPotion() = ItemInstance(
        definition = HealAbsoluteItem(
            label = "Health Potion",
            imageResId = R.drawable.item_health_potion,
            healthAmount = 75,
            isPermanent = false
        ),
        initialUsages = 3
    )
    fun opStrengthPotion() = ItemInstance(
        definition = StatBuffItem(
            label = "Strength Potion",
            imageResId = R.drawable.item_strength_potion,
            buff = Buff(
                duration = 3,
                statsMod = StatModifier(attack = 100) //3 Turns viel zu stark zum testen
            ),
            isPermanent = false
        ),
        initialUsages = 1
    )
}
