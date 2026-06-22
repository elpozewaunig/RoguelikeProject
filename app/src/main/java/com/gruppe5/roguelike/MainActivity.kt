package com.gruppe5.roguelike

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gruppe5.roguelike.map_element.MapElement
import com.gruppe5.roguelike.map_element.MapTile
import com.gruppe5.roguelike.map_element.entity.Entity
import com.gruppe5.roguelike.property.Position
import com.gruppe5.roguelike.ui.theme.RoguelikeTheme
import kotlin.math.abs

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

@Composable
fun MainScreen(modifier: Modifier = Modifier, model: RoguelikeViewModel = viewModel()) {
    val map = model.currentMap
    val player = model.player
    val turn = model.turn

    val windowInfo = LocalWindowInfo.current
    val screenWidth = windowInfo.containerDpSize.width
    val screenHeight = windowInfo.containerDpSize.height

    // Map Container Box
    Box(modifier = modifier
        .pointerInput(Unit) {
            val boxSize = this.size

            val centerPoint = Offset((
                boxSize.width / 2).toFloat(),
                (boxSize.height / 2).toFloat()
            )

            detectTapGestures(onTap = { offset ->
                val centerOffset = offset - centerPoint
                Log.i("Roguelike", "centerOffset: $centerOffset")

                // Offset along x-axis more dominant
                if(abs(centerOffset.x) > abs(centerOffset.y)) {
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

                Log.i("Roguelike", "boxSize: $boxSize")
                Log.i("Roguelike", "offset: $offset")
            })
        }
    ) {
        Box(modifier = Modifier
            .offset( // center player character
                screenWidth/2 - (player.position.x * TILE_SIZE).dp,
                screenHeight/2 - (player.position.y * TILE_SIZE).dp
            )
            .wrapContentSize(Alignment.TopStart, true) // otherwise view is "cut off" at the size of the parent container (screen size)
        ) {
            Column(modifier = Modifier) {
                for(y in map.indices) {
                    Row(modifier = Modifier) {
                        for(x in map[y].indices) {
                            val tile = map[y][x]

                            val tilePos = Position(x, y)
                            var tileEntity: Entity? = null
                            if(player.position == tilePos) {
                                tileEntity = player
                            }

                            MapTileComposable(tile, tileEntity)
                        }
                    }
                }
            }
        }

    }

    // UI Overlay Box
    Box(modifier = modifier.padding(16.dp)) {
        Row {
            Text(
                text = "$turn",
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
        }
    }

}

@Composable
fun MapTileComposable(tile: MapTile, entity: Entity? = null) {
    Box {
        MapTileImage(tile)
        if(entity != null) {
            MapTileImage(entity)
        }
    }
}

@Composable
fun MapTileImage(element: MapElement) {
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
