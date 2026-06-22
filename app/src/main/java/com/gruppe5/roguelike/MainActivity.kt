package com.gruppe5.roguelike

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gruppe5.roguelike.ui.theme.RoguelikeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoguelikeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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

    Box(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .horizontalScroll(rememberScrollState())
        )
    {
        Column(modifier = Modifier) {
            map.forEach { mapRow ->
                Row(modifier = Modifier) {
                    mapRow.forEach { tile ->
                        MapTileComposable(tile)
                    }
                }
            }
        }
    }

}

@Composable
fun MapTileComposable(tile: MapTile) {
    Image(
        modifier = Modifier.width(50.dp).height(50.dp),
        bitmap = ImageBitmap.imageResource(id = tile.resId),
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
