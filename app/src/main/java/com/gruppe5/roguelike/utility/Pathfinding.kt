package com.gruppe5.roguelike.utility

import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.property.Position
import java.util.ArrayList
import java.util.Collections
import java.util.HashMap
import java.util.LinkedHashMap
import kotlin.math.sqrt

class Cell {
    var parent_i: Int = -1
    var parent_j: Int = -1
    var f: Double = Double.POSITIVE_INFINITY
    var g: Double = Double.POSITIVE_INFINITY
    var h: Double = Double.POSITIVE_INFINITY
}

/**
 * Credit: https://www.geeksforgeeks.org/dsa/a-search-algorithm/ (the java impl. one)
 */

object Pathfinding {

    fun findPath(map: List<List<MapTile>>, start: Position, goal: Position): List<Position> {
        val rowCount = map.size
        val colCount = if (rowCount > 0) map[0].size else 0

        if (!isValid(start.y, start.x, rowCount, colCount) || !isValid(goal.y, goal.x, rowCount, colCount)) {
            return emptyList()
        }

        if (!isUnBlocked(map, start.y, start.x) || !isUnBlocked(map, goal.y, goal.x)) {
            return emptyList()
        }

        if (isDestination(start.y, start.x, goal)) {
            return emptyList()
        }

        val closedList = Array(rowCount) { BooleanArray(colCount) }
        val cellDetails = Array(rowCount) { Array(colCount) { Cell() } }

        var i = start.y
        var j = start.x
        cellDetails[i][j].f = 0.0
        cellDetails[i][j].g = 0.0
        cellDetails[i][j].h = 0.0
        cellDetails[i][j].parent_i = i
        cellDetails[i][j].parent_j = j

        val openList = HashMap<Double, Position>()
        openList[0.0] = Position(j, i)

        var foundDest = false

        while (openList.isNotEmpty()) {
            var p = openList.entries.iterator().next()
            for (q in openList.entries) {
                if (q.key < p.key) {
                    p = q
                }
            }

            openList.remove(p.key)

            i = p.value.y
            j = p.value.x
            closedList[i][j] = true

            var gNew: Double
            var hNew: Double
            var fNew: Double

            // 1st Successor (North)
            if (isValid(i - 1, j, rowCount, colCount)) {
                if (isDestination(i - 1, j, goal)) {
                    cellDetails[i - 1][j].parent_i = i
                    cellDetails[i - 1][j].parent_j = j
                    foundDest = true
                    return tracePath(cellDetails, goal)
                } else if (!closedList[i - 1][j] && isUnBlocked(map, i - 1, j)) {
                    gNew = cellDetails[i][j].g + 1.0
                    hNew = calculateHValue(i - 1, j, goal)
                    fNew = gNew + hNew

                    if (cellDetails[i - 1][j].f == Double.POSITIVE_INFINITY || cellDetails[i - 1][j].f > fNew) {
                        openList[fNew] = Position(j, i - 1)

                        cellDetails[i - 1][j].f = fNew
                        cellDetails[i - 1][j].g = gNew
                        cellDetails[i - 1][j].h = hNew
                        cellDetails[i - 1][j].parent_i = i
                        cellDetails[i - 1][j].parent_j = j
                    }
                }
            }

            // 2nd Successor (South)
            if (isValid(i + 1, j, rowCount, colCount)) {
                if (isDestination(i + 1, j, goal)) {
                    cellDetails[i + 1][j].parent_i = i
                    cellDetails[i + 1][j].parent_j = j
                    foundDest = true
                    return tracePath(cellDetails, goal)
                } else if (!closedList[i + 1][j] && isUnBlocked(map, i + 1, j)) {
                    gNew = cellDetails[i][j].g + 1.0
                    hNew = calculateHValue(i + 1, j, goal)
                    fNew = gNew + hNew

                    if (cellDetails[i + 1][j].f == Double.POSITIVE_INFINITY || cellDetails[i + 1][j].f > fNew) {
                        openList[fNew] = Position(j, i + 1)

                        cellDetails[i + 1][j].f = fNew
                        cellDetails[i + 1][j].g = gNew
                        cellDetails[i + 1][j].h = hNew
                        cellDetails[i + 1][j].parent_i = i
                        cellDetails[i + 1][j].parent_j = j
                    }
                }
            }

            // 3rd Successor (East)
            if (isValid(i, j + 1, rowCount, colCount)) {
                if (isDestination(i, j + 1, goal)) {
                    cellDetails[i][j + 1].parent_i = i
                    cellDetails[i][j + 1].parent_j = j
                    foundDest = true
                    return tracePath(cellDetails, goal)
                } else if (!closedList[i][j + 1] && isUnBlocked(map, i, j + 1)) {
                    gNew = cellDetails[i][j].g + 1.0
                    hNew = calculateHValue(i, j + 1, goal)
                    fNew = gNew + hNew

                    if (cellDetails[i][j + 1].f == Double.POSITIVE_INFINITY || cellDetails[i][j + 1].f > fNew) {
                        openList[fNew] = Position(j + 1, i)

                        cellDetails[i][j + 1].f = fNew
                        cellDetails[i][j + 1].g = gNew
                        cellDetails[i][j + 1].h = hNew
                        cellDetails[i][j + 1].parent_i = i
                        cellDetails[i][j + 1].parent_j = j
                    }
                }
            }

            // 4th Successor (West)
            if (isValid(i, j - 1, rowCount, colCount)) {
                if (isDestination(i, j - 1, goal)) {
                    cellDetails[i][j - 1].parent_i = i
                    cellDetails[i][j - 1].parent_j = j
                    foundDest = true
                    return tracePath(cellDetails, goal)
                } else if (!closedList[i][j - 1] && isUnBlocked(map, i, j - 1)) {
                    gNew = cellDetails[i][j].g + 1.0
                    hNew = calculateHValue(i, j - 1, goal)
                    fNew = gNew + hNew

                    if (cellDetails[i][j - 1].f == Double.POSITIVE_INFINITY || cellDetails[i][j - 1].f > fNew) {
                        openList[fNew] = Position(j - 1, i)

                        cellDetails[i][j - 1].f = fNew
                        cellDetails[i][j - 1].g = gNew
                        cellDetails[i][j - 1].h = hNew
                        cellDetails[i][j - 1].parent_i = i
                        cellDetails[i][j - 1].parent_j = j
                    }
                }
            }
        }

        return emptyList()
    }

    private fun isValid(row: Int, col: Int, rowCount: Int, colCount: Int): Boolean {
        return (row >= 0) && (row < rowCount) && (col >= 0) && (col < colCount)
    }

    private fun isUnBlocked(map: List<List<MapTile>>, row: Int, col: Int): Boolean {
        return !map[row][col].type.isWall
    }

    private fun isDestination(row: Int, col: Int, dest: Position): Boolean {
        return row == dest.y && col == dest.x
    }

    private fun calculateHValue(row: Int, col: Int, dest: Position): Double {
        return sqrt(((row - dest.y) * (row - dest.y) + (col - dest.x) * (col - dest.x)).toDouble())
    }

    private fun tracePath(cellDetails: Array<Array<Cell>>, dest: Position): List<Position> {
        var row = dest.y
        var col = dest.x

        val pathMap = LinkedHashMap<Position, Boolean>()

        while (!(cellDetails[row][col].parent_i == row && cellDetails[row][col].parent_j == col)) {
            pathMap[Position(col, row)] = true
            val temp_row = cellDetails[row][col].parent_i
            val temp_col = cellDetails[row][col].parent_j
            row = temp_row
            col = temp_col
        }

        pathMap[Position(col, row)] = true
        val pathList = ArrayList(pathMap.keys)
        Collections.reverse(pathList)

        return pathList
    }
}
