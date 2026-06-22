package com.gruppe5.roguelike

import androidx.lifecycle.ViewModel
import com.gruppe5.roguelike.map_generators.BasicMapGenerator

class RoguelikeViewModel: ViewModel() {
    var currentMap: List<List<MapTile>> = BasicMapGenerator().getMap()
}
