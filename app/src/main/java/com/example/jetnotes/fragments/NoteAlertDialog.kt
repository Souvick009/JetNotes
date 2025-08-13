package com.example.jetnotes.fragments

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker

@Composable
fun NoteAlertDialog(
    colorPickerController: ColorPickerController,
    initialProgressValue: Float,
    initialColorInt: Int? = null,
    onApply: (Float, ColorEnvelope) -> Unit,
    onCancel: () -> Unit
) {
    var firstCompose = true

    val initialColor = initialColorInt?.let {
        Color(initialColorInt)
    } ?: Color.LightGray
    val sliderState = rememberSaveable { mutableFloatStateOf(initialProgressValue) }
    val initialHex = initialColorInt?.toHexString() ?: "#FFFFFFFF"

    var _colorEnvelope by remember {
        mutableStateOf(ColorEnvelope(initialColor, initialHex, false))
    }

    var hexCode by remember {
        mutableStateOf(_colorEnvelope.hexCode)
    }
    var isError by rememberSaveable { mutableStateOf(false) }
    var supportingText by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = "Configure the note",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Progress: ${(sliderState.value * 100).toInt()}%",
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
                Slider(
                    value = sliderState.value,
                    steps = 9,
                    onValueChange = { sliderState.value = it }
                )
                Spacer(modifier = Modifier.height(10.dp))
                HsvColorPicker(
                    controller = colorPickerController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    onColorChanged = {
                        if (!firstCompose) {
                            _colorEnvelope = it
                            hexCode = "#${it.hexCode}"
                        }
                        firstCompose = false
                    },
                    initialColor = initialColor
                )
                Spacer(modifier = Modifier.height(18.dp))
                AlphaSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    controller = colorPickerController,
                )
                Spacer(modifier = Modifier.height(25.dp))
                Row {
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1F)
                            .fillMaxWidth(),
                        value = hexCode,
                        onValueChange = {
                            hexCode = it

                            val cleanedHex = it.removePrefix("#")
                            if (cleanedHex.length == 6 || cleanedHex.length == 8) {
                                try {
                                    val parsedColor = Color("#$cleanedHex".toColorInt())
                                    colorPickerController.selectByColor(parsedColor, true)
                                    isError = false
                                    supportingText = ""
                                } catch (_: IllegalArgumentException) {
                                    // Ignore invalid colors
                                    isError = true
                                    supportingText = "invalid color"
                                }
                            } else {
                                isError = true
                                supportingText = "invalid color"
                            }
                        },
                        singleLine = true,
                        label = { Text(text = "Hex color (#RRGGBB or #AARRGGBB)") },
                        isError = isError,
                        supportingText = {
                            if (isError)
                                Text(supportingText)
                            else
                                null
                        }
                    )
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .background(_colorEnvelope.color)
                    )
                }

            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(sliderState.value, _colorEnvelope) }) {
                Text(text = "Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = "Cancel")
            }
        }
    )
}

