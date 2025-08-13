package com.example.jetnotes.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import com.example.jetnotes.fragments.DeletionAlertDialog
import com.example.jetnotes.fragments.NoteAlertDialog
import com.example.jetnotes.navigation.CreateNoteScreenRoute
import com.example.jetnotes.viewmodel.NotesViewModel
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun homeScreen(navController: NavHostController, viewModel: NotesViewModel) {
    val notesList by viewModel.allNotes.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Notes") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(CreateNoteScreenRoute(title = "", desc = ""))
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
                        clickable = viewModel.cardClickable,
                        progress = note.progress,
                        color = note.color?.let { hexColorCode ->
                            Color(hexColorCode.toColorInt())
                        } ?: Color.Gray,
                        onProgressBarClick = {
                            viewModel.setShowDialog(note.id)
                        },
                        onDelete = {
                            viewModel.setDeletionDialog(note.id)
                        },
                        onClick = {
                            viewModel.setCardClickable(false)
                            navController.navigate(
                                CreateNoteScreenRoute(
                                    id = note.id,
                                    title = note.title,
                                    desc = note.desc,
                                    progress = note.progress,
                                    color = note.color
                                )
                            )
                        }
                    )

                    if (note.id == viewModel.showDialog.value) {
                        val colorPickerController = rememberColorPickerController()
                        NoteAlertDialog(
                            colorPickerController = colorPickerController,
                            initialProgressValue = note.progress / 100f,
                            onApply = { newProgress, colorEnvelope ->
                                viewModel.saveProgress(
                                    note = note,
                                    newProgress = (newProgress * 100).toInt(),
                                    newColor = colorEnvelope.hexCode
                                )
                            },
                            initialColorInt = note.color?.toColorInt(),
                            onCancel = {
                                viewModel.setShowDialog(null)
                            }
                        )
                    } else if (note.id == viewModel.deletionDialog.value) {
                        DeletionAlertDialog(
                            onDismiss = {
                                viewModel.setDeletionDialog(null)
                            },
                            onConfirm = {
                                viewModel.deleteData(note.id)
                            },
                            title = note.title
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoteCard(
    title: String = "title",
    desc: String = "desc",
    clickable: State<Boolean>,
    progress: Int = 0,
    color: Color,
    onProgressBarClick: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .pointerInput(clickable.value) {
                detectTapGestures(
                    onTap = {
                        if (clickable.value) onClick()
                    },
                    onLongPress = {
                        if (clickable.value) onDelete()
                    }
                )
            },
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

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(top = 6.dp, start = 8.dp, end = 8.dp)
                    .clickable(onClick = onProgressBarClick)
            ) {
                Surface(
                    modifier = Modifier.size(13.dp),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color.White),
                    color = Color.Transparent,
                    content = {}
                )
                VerticalFillProgress(
                    progress = progress, // 25% filled
                )
                Surface(
                    modifier = Modifier.size(39.dp),
                    shape = CircleShape,
                    border = BorderStroke(2.dp, Color.White),
                    color = Color.Transparent,
                    content = {}
                )
            }

        }
    }
}

@Composable
fun VerticalFillProgress(
    progress: Int, // value from 0 to 100
) {
    CircularProgressIndicator(
        progress = { progress / 100f },
        strokeWidth = 7.dp,
        color = Color.White,
        trackColor = Color.LightGray,
        modifier = Modifier
            .size(30.dp)
    )
}
