package com.gruppe5.roguelike.utility

import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_element.entity.Entity
import com.gruppe5.roguelike.property.Position
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.PriorityQueue

class Cell {
    var parentI: Int = -1
    var parentJ: Int = -1
    var f: Double = Double.POSITIVE_INFINITY
    var g: Double = Double.POSITIVE_INFINITY
    var h: Double = Double.POSITIVE_INFINITY
}

/**
 * Credit: https://www.geeksforgeeks.org/dsa/a-search-algorithm/ (the java impl. one)
 * and gemini for fixing their hash map bug -.-
 */

object Pathfinding {

    fun findPath(
        map: List<List<MapTile>>,
        entities: List<Entity>,
        start: Position,
        goal: Position
    ): List<Position> {
        val rowCount = map.size
        val colCount = if (rowCount > 0) map[0].size else 0

        if (!isValid(start.y, start.x, rowCount, colCount) || !isValid(
                goal.y,
                goal.x,
                rowCount,
                colCount
            )
        ) {
            return emptyList()
        }

        if (!isUnBlocked(map, listOf(), start.y, start.x) || !isUnBlocked(
                map,
                listOf(),
                goal.y,
                goal.x
            )
        ) {
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
        cellDetails[i][j].h = calculateHValue(i, j, goal)
        cellDetails[i][j].parentI = i
        cellDetails[i][j].parentJ = j

        var bestEffortNode = start
        var bestEffortH = cellDetails[i][j].h

        // We use a PriorityQueue, NOT a HashMap. A HashMap mapping F-scores to Positions
        // will silently overwrite and delete valid nodes that happen to share the same F-score,
        // breaking the A* algorithm and causing it to frequently fail to find a path.
        val openList = PriorityQueue<Pair<Double, Position>>(compareBy { it.first })
        openList.add(Pair(0.0, Position(j, i)))

        while (openList.isNotEmpty()) {
            val p = openList.remove()

            i = p.second.y
            j = p.second.x

            if (closedList[i][j]) continue
            closedList[i][j] = true

            if (cellDetails[i][j].h < bestEffortH) {
                bestEffortH = cellDetails[i][j].h
                bestEffortNode = p.second
            }

            var gNew: Double
            var hNew: Double
            var fNew: Double

            // 1st Successor (North)
            if (isValid(i - 1, j, rowCount, colCount)) {
                if (isDestination(i - 1, j, goal)) {
                    cellDetails[i - 1][j].parentI = i
                    cellDetails[i - 1][j].parentJ = j
                    return tracePath(cellDetails, goal)
                } else if (!closedList[i - 1][j] && isUnBlocked(map, entities, i - 1, j)) {
                    gNew = cellDetails[i][j].g + 1.0
                    hNew = calculateHValue(i - 1, j, goal)
                    fNew = gNew + hNew

                    if (cellDetails[i - 1][j].f == Double.POSITIVE_INFINITY || cellDetails[i - 1][j].f > fNew) {
                        openList.add(Pair(fNew, Position(j, i - 1)))

                        cellDetails[i - 1][j].f = fNew
                        cellDetails[i - 1][j].g = gNew
                        cellDetails[i - 1][j].h = hNew
                        cellDetails[i - 1][j].parentI = i
                        cellDetails[i - 1][j].parentJ = j
                    }
                }
            }

            // 2nd Successor (South)
            if (isValid(i + 1, j, rowCount, colCount)) {
                if (isDestination(i + 1, j, goal)) {
                    cellDetails[i + 1][j].parentI = i
                    cellDetails[i + 1][j].parentJ = j
                    return tracePath(cellDetails, goal)
                } else if (!closedList[i + 1][j] && isUnBlocked(map, entities, i + 1, j)) {
                    gNew = cellDetails[i][j].g + 1.0
                    hNew = calculateHValue(i + 1, j, goal)
                    fNew = gNew + hNew

                    if (cellDetails[i + 1][j].f == Double.POSITIVE_INFINITY || cellDetails[i + 1][j].f > fNew) {
                        openList.add(Pair(fNew, Position(j, i + 1)))

                        cellDetails[i + 1][j].f = fNew
                        cellDetails[i + 1][j].g = gNew
                        cellDetails[i + 1][j].h = hNew
                        cellDetails[i + 1][j].parentI = i
                        cellDetails[i + 1][j].parentJ = j
                    }
                }
            }

            // 3rd Successor (East)
            if (isValid(i, j + 1, rowCount, colCount)) {
                if (isDestination(i, j + 1, goal)) {
                    cellDetails[i][j + 1].parentI = i
                    cellDetails[i][j + 1].parentJ = j
                    return tracePath(cellDetails, goal)
                } else if (!closedList[i][j + 1] && isUnBlocked(map, entities, i, j + 1)) {
                    gNew = cellDetails[i][j].g + 1.0
                    hNew = calculateHValue(i, j + 1, goal)
                    fNew = gNew + hNew

                    if (cellDetails[i][j + 1].f == Double.POSITIVE_INFINITY || cellDetails[i][j + 1].f > fNew) {
                        openList.add(Pair(fNew, Position(j + 1, i)))

                        cellDetails[i][j + 1].f = fNew
                        cellDetails[i][j + 1].g = gNew
                        cellDetails[i][j + 1].h = hNew
                        cellDetails[i][j + 1].parentI = i
                        cellDetails[i][j + 1].parentJ = j
                    }
                }
            }

            // 4th Successor (West)
            if (isValid(i, j - 1, rowCount, colCount)) {
                if (isDestination(i, j - 1, goal)) {
                    cellDetails[i][j - 1].parentI = i
                    cellDetails[i][j - 1].parentJ = j
                    return tracePath(cellDetails, goal)
                } else if (!closedList[i][j - 1] && isUnBlocked(map, entities, i, j - 1)) {
                    gNew = cellDetails[i][j].g + 1.0
                    hNew = calculateHValue(i, j - 1, goal)
                    fNew = gNew + hNew

                    if (cellDetails[i][j - 1].f == Double.POSITIVE_INFINITY || cellDetails[i][j - 1].f > fNew) {
                        openList.add(Pair(fNew, Position(j - 1, i)))

                        cellDetails[i][j - 1].f = fNew
                        cellDetails[i][j - 1].g = gNew
                        cellDetails[i][j - 1].h = hNew
                        cellDetails[i][j - 1].parentI = i
                        cellDetails[i][j - 1].parentJ = j
                    }
                }
            }
        }

        // If no full path is found (e.g. goal is blocked), return best-effort path to the closest reachable tile
        if (bestEffortNode != start) {
            return tracePath(cellDetails, bestEffortNode)
        }

        return emptyList()
    }

    private fun isValid(row: Int, col: Int, rowCount: Int, colCount: Int): Boolean {
        return (row >= 0) && (row < rowCount) && (col >= 0) && (col < colCount)
    }

    private fun isUnBlocked(
        map: List<List<MapTile>>,
        entities: List<Entity>,
        row: Int,
        col: Int
    ): Boolean {
        return !map[row][col].type.isWall
            && entities.firstOrNull { it.position == Position(col, row) } == null
    }

    private fun isDestination(row: Int, col: Int, dest: Position): Boolean {
        return row == dest.y && col == dest.x
    }

    private fun calculateHValue(row: Int, col: Int, dest: Position): Double {
        return (kotlin.math.abs(row - dest.y) + kotlin.math.abs(col - dest.x)).toDouble()
    }

    /**
     * Backtracks from the destination node to the start node by following the parent pointers.
     * The resulting path is then reversed to provide the sequence of steps from start to destination.
     */
    private fun tracePath(cellDetails: Array<Array<Cell>>, dest: Position): List<Position> {
        var row = dest.y
        var col = dest.x

        val pathMap = LinkedHashMap<Position, Boolean>()

        while (!(cellDetails[row][col].parentI == row && cellDetails[row][col].parentJ == col)) {
            pathMap[Position(col, row)] = true
            val cell = cellDetails[row][col]
            row = cell.parentI
            col = cell.parentJ
        }

        pathMap[Position(col, row)] = true
        val pathList = ArrayList(pathMap.keys)
        pathList.reverse()

        return pathList
    }
}
