package com.gruppe5.roguelike.inventory

class HealAbsoluteItem(
    label: String,
    imageResId: Int,
    var healthAmount: Int
) : InventoryItem(label, imageResId)
