package com.example.jetnotes.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import com.example.jetnotes.fragments.NoteAlertDialog
import com.example.jetnotes.navigation.CreateNoteScreenRoute
import com.example.jetnotes.viewmodel.NotesViewModel
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showSystemUi = true)
@Composable
fun homeScreen(navController: NavHostController, viewModel: NotesViewModel) {
    val context = LocalContext.current
    val notesList by viewModel.allNotes.collectAsState(initial = emptyList())
    val colorController = rememberColorPickerController()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Notes") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(CreateNoteScreenRoute())
            }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Grid List
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notesList) { note ->
                    NoteCard(
                        title = note.title,
                        desc = note.desc,
                        clickable = viewModel.cardClickable.value,
                        progress = note.progress,
                        color = note.color?.let { hexColorCode ->
                            Color(hexColorCode.toColorInt())
                        } ?: Color.Gray,
                        onProgressBarClick = {
                            viewModel.showDialog(note.id)
                        }
                    ) {
                        Log.d("ColorCheck","yes1")
                        viewModel.cardClickable(false)
                        navController.navigate(
                            CreateNoteScreenRoute(
                                id = note.id,
                                title = note.title,
                                desc = note.desc
                            )
                        )
                    }

                    if (note.id == viewModel.showDialog.value) {
                        Log.w("progressCheck", note.title)
                        NoteAlertDialog(
                            initialProgressValue = note.progress / 100f,
                            onApply = { newProgress, colorEnvelope ->
                                viewModel.saveProgress(
                                    note = note,
                                    newProgress = (newProgress * 100).toInt(),
                                    newColor = colorEnvelope.hexCode
                                )
                            },
                            hsvController = colorController,
                            initialColor = note.color?.let { hexColorCode ->
                                Color(hexColorCode.toColorInt())
                            } ?: Color.Gray,
                            onCancel = {
                                viewModel.showDialog(null)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun NoteCard(
    title: String = "title",
    desc: String = "desc",
    clickable: Boolean = true,
    progress: Int = 0,
    color: Color = Color.Gray,
    onProgressBarClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick, enabled = clickable),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = desc)
            }

            VerticalFillProgress(
                progress = progress, // 25% filled
                onClick = onProgressBarClick
            )
        }
    }
}

@Composable
fun VerticalFillProgress(
    progress: Int, // value from 0 to 100
    onClick: () -> Unit
) {
    CircularProgressIndicator(
        progress = { progress / 100f },
        strokeWidth = 7.dp,
        color = Color.LightGray,
        trackColor = Color.White,
        modifier = Modifier
            .padding(top = 6.dp, start = 8.dp, end = 8.dp)
            .size(30.dp)
            .clickable(onClick = onClick)
    )
}
