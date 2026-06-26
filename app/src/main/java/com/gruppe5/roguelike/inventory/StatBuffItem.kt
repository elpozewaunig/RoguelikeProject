package com.gruppe5.roguelike.inventory

import com.gruppe5.roguelike.property.Buff

class StatBuffItem(
    label: String,
    imageResId: Int,
    val buff: Buff
) : InventoryItem(label, imageResId)
