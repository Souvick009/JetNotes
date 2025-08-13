package com.example.jetnotes.fragments

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeletionAlertDialog(
    onDismiss: () -> Unit, onConfirm: () -> Unit, title: String
) {
    AlertDialog(
        onDismissRequest = {},
        dismissButton = {
            TextButton(
                content = { Text(text = "Cancel") },
                onClick = { onDismiss() })
        },
        confirmButton = {
            TextButton(
                content = { Text(text = "Delete") },
                onClick = { onConfirm() })
        },
        title = { Text(text = "Deletion of the note") },
        text = {
            Column {
                Text(
                    text = "Are you sure, you want to delete the note?"
                )
                Text(
                    text = "Title: $title"
                )
            }
        },

        )
}