package com.gruppe5.roguelike

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_element.VisualMapElement
import com.gruppe5.roguelike.map_element.entity.Entity
import com.gruppe5.roguelike.ui.theme.RoguelikeTheme
import kotlin.math.abs

const val DEBUG_MODE = false
const val TILE_SIZE = 50

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoguelikeTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = (Color.Black),
                ) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

fun dpAnimate(current: Dp, target: Dp, delta: Double, speed: Double): Dp {
    var change: Dp = ((target - current).value * speed * delta).dp
    if(
        (current > target && current + change < target) ||
        (current < target && current + change > target) ||
        abs(change.value) < 0.1 * delta
    ) {
        change = target - current
    }
    return change
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, model: RoguelikeViewModel = viewModel()) {
    val map = model.currentMap
    val player = model.player
    val enemies = model.enemies
    val turn = model.turn

    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerDpSize.width
    val screenHeight = windowInfo.containerDpSize.height

    // Map Container Box
    Box(modifier = modifier
        .pointerInput(Unit) {
            val centerPoint = Offset((
                screenWidth / 2).toPx(),
                (screenHeight / 2).toPx()
            )

            detectTapGestures(onTap = { offset ->
                val centerOffset = offset - centerPoint

                // Tap is on player itself
                if(
                    abs(centerOffset.x) < 0.5 * TILE_SIZE.dp.toPx() &&
                    abs(centerOffset.y) < 0.5 * TILE_SIZE.dp.toPx())
                {
                    model.moveSkip()
                }
                // Offset along x-axis more dominant
                else if(abs(centerOffset.x) > abs(centerOffset.y)) {
                    if(centerOffset.x > 0) {
                        model.moveRight()
                    }
                    else {
                        model.moveLeft()
                    }
                }
                // Offset along y-axis more dominant
                else {
                    if(centerOffset.y > 0) {
                        model.moveDown()
                    }
                    else {
                        model.moveUp()
                    }
                }
            })
        }
    ) {
        val targetOffset = DpOffset(
            screenWidth/2 - (player.position.x * TILE_SIZE).dp - (TILE_SIZE / 2).dp,
            screenHeight/2 - (player.position.y * TILE_SIZE).dp - (TILE_SIZE / 2).dp
        )
        var currentOffsetX by remember {
            mutableStateOf(targetOffset.x)
        }
        var currentOffsetY by remember {
            mutableStateOf(targetOffset.y)
        }

        val elapsedTime = SystemClock.elapsedRealtime()
        var lastElapsedTime by remember { mutableLongStateOf(elapsedTime) }

        // Smoothly change offset of camera
        var firstAnimationFrame by remember { mutableStateOf(true) }
        if(currentOffsetX != targetOffset.x ||currentOffsetY != targetOffset.y) {
            if(firstAnimationFrame) {
                lastElapsedTime = elapsedTime
                firstAnimationFrame = false
            }
            val deltaTime: Double = (elapsedTime - lastElapsedTime) / 1000.0
            lastElapsedTime = elapsedTime
            if(currentOffsetX != targetOffset.x) {
                currentOffsetX += dpAnimate(currentOffsetX, targetOffset.x, deltaTime, 3.0)
            }
            if(currentOffsetY != targetOffset.y) {
                currentOffsetY += dpAnimate(currentOffsetY, targetOffset.y, deltaTime, 3.0)
            }
        }
        else {
            firstAnimationFrame = true
        }

        Box(modifier = Modifier
            .offset {
                IntOffset(
                currentOffsetX.toPx().toInt(),
                currentOffsetY.toPx().toInt()
                )
            }
            .wrapContentSize(Alignment.TopStart, true) // otherwise view is "cut off" at the size of the parent container (screen size)
        ) {
            Column(modifier = Modifier) {
                for(y in map.indices) {
                    Row(modifier = Modifier) {
                        for(x in map[y].indices) {
                            val tile = map[y][x]
                            val tileEntity: Entity? =
                                if(player.position == tile.position) {
                                    player
                                }
                                else {
                                    enemies.firstOrNull { it.position == tile.position }
                                }

                            //TODO  des is so disgusting atm, (muss btw health hier fetched werden damit recompose geht :055:)
                            MapTileComposable(
                                tile = tile,
                                entity = tileEntity,
                                health = tileEntity?.stats?.health ?: 0,
                                maxHealth = tileEntity?.stats?.maxHealth ?: 0
                            )
                        }
                    }
                }

            }
            if (DEBUG_MODE) {
                DebugPathOverlay(enemies, map[0].size, map.size)
            }
        }

    }

    Box(modifier = modifier.padding(16.dp)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "HP ${player.stats.health}/${player.stats.maxHealth}",
                    color = Color.White
                )
                Text(
                    text = "$turn",
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Right
                )
            }
            if (model.isGameOver) { //leitner-approved
                Text(
                    text = "Game Over",
                    color = Color.Red,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp
                )
            }
        }
    }

}

@Composable
fun MapTileComposable(tile: MapTile, entity: Entity? = null, health: Int = 0, maxHealth: Int = 0) {
    Box {
        MapTileImage(tile)
        if(entity != null) {
            MapTileImage(entity)
            HealthBar(
                current = health,
                max = maxHealth,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-2).dp)
            )
        }
    }
}

// schiach
@Composable
fun HealthBar(current: Int, max: Int, modifier: Modifier = Modifier) {
    if(max <= 0) return
    val fraction = (current.toFloat() / max).coerceIn(0f, 1f)
    Box(
        modifier
            .width(TILE_SIZE.dp)
            .height(5.dp)
            .background(Color.Red)
    ) {
        Box(
            Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .background(Color.Green)
        )
    }
}

@Composable
fun MapTileImage(element: VisualMapElement) {
    Image(
        modifier = Modifier.width(TILE_SIZE.dp).height(TILE_SIZE.dp),
        bitmap = ImageBitmap.imageResource(id = element.resId),
        contentDescription = null,
        filterQuality = FilterQuality.None
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    RoguelikeTheme {
        MainScreen()
    }
}

@Composable // [!] slop
fun DebugPathOverlay(enemies: List<com.gruppe5.roguelike.map_element.entity.Enemy>, mapWidth: Int, mapHeight: Int) {
    Canvas(
        modifier = Modifier
            .width((mapWidth * TILE_SIZE).dp)
            .height((mapHeight * TILE_SIZE).dp)
    ) {
        enemies.forEachIndexed { index, enemy ->
            val path = enemy.path
            if (path.isEmpty()) return@forEachIndexed

            val hue = (240f + index * 36f) % 360f
            val hsvColor = android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
            val pathColor = Color(hsvColor)

            val paint = android.graphics.Paint().apply {
                color = hsvColor
                textSize = 30f
                textAlign = android.graphics.Paint.Align.CENTER
            }

            for (i in 0 until path.size - 1) {
                val startPos = path[i]
                val endPos = path[i+1]

                val startX = startPos.x * TILE_SIZE.dp.toPx() + TILE_SIZE.dp.toPx() / 2
                val startY = startPos.y * TILE_SIZE.dp.toPx() + TILE_SIZE.dp.toPx() / 2

                val endX = endPos.x * TILE_SIZE.dp.toPx() + TILE_SIZE.dp.toPx() / 2
                val endY = endPos.y * TILE_SIZE.dp.toPx() + TILE_SIZE.dp.toPx() / 2

                drawLine(
                    color = pathColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 5f
                )
            }

            for (i in path.indices) {
                val pos = path[i]
                val cx = pos.x * TILE_SIZE.dp.toPx() + TILE_SIZE.dp.toPx() / 2
                val cy = pos.y * TILE_SIZE.dp.toPx() + TILE_SIZE.dp.toPx() / 2

                drawCircle(
                    color = pathColor,
                    radius = 10f,
                    center = Offset(cx, cy)
                )

                drawContext.canvas.nativeCanvas.drawText(
                    i.toString(),
                    cx,
                    cy - 15f,
                    paint
                )
            }
        }
    }
}
