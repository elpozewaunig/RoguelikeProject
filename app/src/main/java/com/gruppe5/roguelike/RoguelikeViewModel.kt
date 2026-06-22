package com.gruppe5.roguelike

import androidx.lifecycle.ViewModel
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_generators.BasicMapGenerator
import com.gruppe5.roguelike.map_generators.MapGenerator
import com.gruppe5.roguelike.property.StatModifier

class RoguelikeViewModel: ViewModel() {
    private val mapGenerator: MapGenerator = BasicMapGenerator()
    var currentMap: List<List<MapTile>> = mapGenerator.getMap()
    val player: Player = Player(
        StatModifier(

        ),
        position = mapGenerator.getStartPos()
    )
}
