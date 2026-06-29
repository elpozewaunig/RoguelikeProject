package com.gruppe5.roguelike.utility

import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_element.entity.Entity
import com.gruppe5.roguelike.property.Position
import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.max

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
 * and claude for adding L movement
 *
 * ^^ the beauty of open source collaboration (diebstahl + LLM + LLM + autismus)
 */

object Pathfinding {

    // up down left right
    val ORTHOGONAL_MOVES: List<Position> = listOf(
        Position(0, -1), // North
        Position(0, 1),  // South
        Position(1, 0),  // East
        Position(-1, 0)  // West
    )

    // schach L
    val KNIGHT_MOVES: List<Position> = listOf(
        Position(1, 2), Position(2, 1),
        Position(-1, 2), Position(-2, 1),
        Position(1, -2), Position(2, -1),
        Position(-1, -2), Position(-2, -1)
    )

    // schach Läufer (ein Feld)
    val DIAGONAL_MOVES: List<Position> = listOf(
        Position(1, 1), Position(1, -1),
        Position(-1, 1), Position(-1, -1)
    )

    // Admissible heuristics, dependency-injected per movement profile (every hop costs 1).
    val MANHATTAN: (Position, Position) -> Double = { a, b -> a.distanceTo(b).toDouble() }
    val KNIGHT: (Position, Position) -> Double = { a, b -> a.distanceTo(b) / 3.0 }
    val CHEBYSHEV: (Position, Position) -> Double = { a, b -> max(abs(a.x - b.x), abs(a.y - b.y)).toDouble() }

    fun findPath(
        map: List<List<MapTile>>,
        entities: List<Entity>,
        start: Position,
        goal: Position,
        moves: List<Position> = ORTHOGONAL_MOVES,
        heuristic: (Position, Position) -> Double = MANHATTAN,
        ignoreWalls: Boolean = false
    ): List<Position> {
        val rowCount = map.size
        val colCount = if (rowCount > 0) map[0].size else 0

        if (!isValid(start.y, start.x, rowCount, colCount) ||
            !isValid(goal.y, goal.x, rowCount, colCount)
        ) {
            return emptyList()
        }

        if (!isUnBlocked(map, listOf(), start.y, start.x, ignoreWalls) ||
            !isUnBlocked(map, listOf(), goal.y, goal.x, ignoreWalls)
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
        cellDetails[i][j].h = heuristic(Position(j, i), goal)
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

            // Expand every square one hop away under this movement profile.
            for (move in moves) {
                val newRow = i + move.y
                val newCol = j + move.x

                if (!isValid(newRow, newCol, rowCount, colCount)) continue

                if (isDestination(newRow, newCol, goal)) {
                    cellDetails[newRow][newCol].parentI = i
                    cellDetails[newRow][newCol].parentJ = j
                    return tracePath(cellDetails, goal)
                }

                if (closedList[newRow][newCol] ||
                    !isUnBlocked(map, entities, newRow, newCol, ignoreWalls)
                ) {
                    continue
                }

                val gNew = cellDetails[i][j].g + 1.0
                val hNew = heuristic(Position(newCol, newRow), goal)
                val fNew = gNew + hNew

                if (cellDetails[newRow][newCol].f == Double.POSITIVE_INFINITY ||
                    cellDetails[newRow][newCol].f > fNew
                ) {
                    openList.add(Pair(fNew, Position(newCol, newRow)))

                    cellDetails[newRow][newCol].f = fNew
                    cellDetails[newRow][newCol].g = gNew
                    cellDetails[newRow][newCol].h = hNew
                    cellDetails[newRow][newCol].parentI = i
                    cellDetails[newRow][newCol].parentJ = j
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
        col: Int,
        ignoreWalls: Boolean
    ): Boolean {
        return (ignoreWalls || !map[row][col].type.isWall)
            && entities.firstOrNull { it.position == Position(col, row) } == null
    }

    private fun isDestination(row: Int, col: Int, dest: Position): Boolean {
        return row == dest.y && col == dest.x
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
