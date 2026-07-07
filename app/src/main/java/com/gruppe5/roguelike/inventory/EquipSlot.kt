package com.gruppe5.roguelike.inventory

import com.gruppe5.roguelike.R

//ghost solang slot leer
enum class EquipSlot(val iconResId: Int) {
    HELMET(R.drawable.sloticon_helmet),
    CHESTPLATE(R.drawable.sloticon_chestplate),
    LEGGINGS(R.drawable.sloticon_leggings),
    BOOTS(R.drawable.sloticon_boots),
    SWORD(R.drawable.sloticon_sword),
    SHIELD(R.drawable.sloticon_shield),
}
