package com.gruppe5.roguelike

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.gruppe5.roguelike.map_element.entity.Player
import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_generators.BasicMapGenerator
import com.gruppe5.roguelike.map_generators.MapGenerator
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.property.StatModifier

class RoguelikeViewModel: ViewModel() {
    private val mapGenerator: MapGenerator = BasicMapGenerator()
    val currentMap: List<List<MapTile>> = mapGenerator.getMap()
    val player: Player = Player(
        StatModifier(

        ),
        position = mapGenerator.getStartPos()
    )
    var turn by mutableIntStateOf(0)

    private fun move(x: Int, y: Int) {
        val newPosition = Position(player.position.x + x, player.position.y + y)
        if(
            newPosition.y >= 0 &&
            newPosition.x >= 0 &&
            newPosition.y < currentMap.size &&
            newPosition.x < currentMap[newPosition.y].size
            ) {
            val targetTile: MapTile = currentMap[newPosition.y][newPosition.x]
            if(!targetTile.isWall) {
                player.position = newPosition
                // Perform enemy movement logic here
                turn++
            }
        }
    }

    fun moveRight() {
        move(1, 0)
    }

    fun moveLeft() {
        move(-1, 0)
    }

    fun moveDown() {
        move(0, 1)
    }

    fun moveUp() {
        move(0, -1)
    }
}
