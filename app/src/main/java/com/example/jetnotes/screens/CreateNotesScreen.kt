package com.example.jetnotes.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.jetnotes.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
//@Preview(showSystemUi = true)
@Composable
fun createNotesScreen(
    navController: NavHostController,
    viewModel: NotesViewModel,
    noteID: Int? = null,
    title: String? = null,
    desc: String? = null
) {

    val context = LocalContext.current

    val toastMessage = viewModel.toastMessage.collectAsState().value
    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearToastMessage()
        }
    }

    LaunchedEffect(noteID, title, desc) {
        noteID?.let {
            if (title != null && desc != null) {
                viewModel.setTitleText(title)
                viewModel.setDescText(desc)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Create Note")
                },
                navigationIcon = {
                    IconButton({
                        if (noteID != null) {
                            viewModel.clearDataFromViewModel()
                        }
                        navController.popBackStack()
                        viewModel.cardClickable(true)
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "BackButton"
                        )
                    }
                },
                actions = {
                    IconButton({
                        viewModel.saveData(
                            id = noteID
                        ) {
                            navController.popBackStack()
                            viewModel.cardClickable(true)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "SaveButton"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            TextField(
                value = viewModel.titleText.value,
                onValueChange = { inputString ->
                    viewModel.setTitleText(inputString)
                },
                textStyle = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold),
                placeholder = { Text("Enter the title", color = Color.Gray, fontSize = 25.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                colors = TextFieldDefaults.colors(
                    // Background
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,

                    // Bottom Border
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            TextField(
                value = viewModel.descText.value,
                onValueChange = { inputString ->
                    viewModel.setDescText(inputString)
                },
                modifier = Modifier.fillMaxSize(),
                placeholder = { Text("Start typing", fontSize = 18.sp, color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    // Background
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,

                    // Bottom Border
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )

        }
    }
}