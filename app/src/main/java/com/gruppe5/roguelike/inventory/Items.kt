package com.gruppe5.roguelike.inventory

import com.gruppe5.roguelike.R
import com.gruppe5.roguelike.inventory.item_types.BuffPotion
import com.gruppe5.roguelike.inventory.item_types.EquipmentItem
import com.gruppe5.roguelike.inventory.item_types.HealItem
import com.gruppe5.roguelike.inventory.item_types.TrinketItem
import com.gruppe5.roguelike.property.StatModifier

object Items {
    fun sword() = ItemInstance(
        EquipmentItem(
            label = "Sword",
            imageResId = R.drawable.item_leek,
            slot = EquipSlot.SWORD,
            statsMod = StatModifier(attack = 10),
        ),
        usages = 1,
    )

    fun ironHelmet() = ItemInstance(
        EquipmentItem(
            label = "Iron Helmet",
            imageResId = R.drawable.helmet_iron,
            slot = EquipSlot.HELMET,
            statsMod = StatModifier(defense = 1),
        ),
        usages = 1,
    )

    fun ironChestplate() = ItemInstance(
        EquipmentItem(
            label = "Iron Chestplate",
            imageResId = R.drawable.chestplate_iron,
            slot = EquipSlot.CHESTPLATE,
            statsMod = StatModifier(defense = 3),
        ),
        usages = 1,
    )

    fun ironLeggings() = ItemInstance(
        EquipmentItem(
            label = "Iron Leggings",
            imageResId = R.drawable.leggings_iron,
            slot = EquipSlot.LEGGINGS,
            statsMod = StatModifier(defense = 2),
        ),
        usages = 1,
    )

    fun ironBoots() = ItemInstance(
        EquipmentItem(
            label = "Iron Boots",
            imageResId = R.drawable.boots_iron,
            slot = EquipSlot.BOOTS,
            statsMod = StatModifier(defense = 1),
        ),
        usages = 1,
    )

    fun mediumHealthPotion() = ItemInstance(
        HealItem(
            label = "Health Potion",
            imageResId = R.drawable.item_health_potion,
            amount = 75,
        ),
        usages = 3,
    )

    fun opStrengthPotion() = ItemInstance(
        BuffPotion(
            label = "Strength Potion",
            imageResId = R.drawable.item_strength_potion,
            statsMod = StatModifier(attack = 100), //3 Turns viel zu stark zum testen
            duration = 3,
            rarity = Rarity.RARE,
        ),
        usages = 1,
    )

    //no effect for now, placeholder (aber schön legendary fürs backdrop)
    fun totemOfUndying() = ItemInstance(
        TrinketItem(
            label = "Totem of Undying",
            imageResId = R.drawable.item_totemofundying,
            rarity = Rarity.LEGENDARY,
        ),
        usages = 1,
    )

    fun patriotenherz() = ItemInstance(
        TrinketItem(
            label = "Patriotenherz",
            imageResId = R.drawable.item_patriotenherz,
            rarity = Rarity.EPIC,
        ),
        usages = 1,
    )
}
