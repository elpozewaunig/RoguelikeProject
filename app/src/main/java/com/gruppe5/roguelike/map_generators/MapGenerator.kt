package com.gruppe5.roguelike.map_generators

import com.gruppe5.roguelike.MapTile

interface MapGenerator {
    fun getMap(): List<List<MapTile>>
}
