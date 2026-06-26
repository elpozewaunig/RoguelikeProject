package com.gruppe5.roguelike.inventory

class HealRelativeItem(
    label: String,
    imageResId: Int,
    var healthPercentage: Float
) : InventoryItem(label, imageResId)
