package com.gruppe5.roguelike.inventory

import com.gruppe5.roguelike.R

//i werd most definitely noch gambling adden später
enum class Rarity(val backdropResId: Int) {
    COMMON(R.drawable.item_frame_backdrop_rarity_common),
    RARE(R.drawable.item_frame_backdrop_rarity_rare),
    EPIC(R.drawable.item_frame_backdrop_rarity_epic),
    LEGENDARY(R.drawable.item_frame_backdrop_rarity_legendary),
}
