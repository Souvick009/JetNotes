package com.example.jetnotes.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetnotes.roomDB.Note
import com.example.jetnotes.roomDB.NotesDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val dao: NotesDao
) : ViewModel() {

    private val _titleText = mutableStateOf("")
    val titleText: State<String> = _titleText

    private val _descText = mutableStateOf("")
    val descText: State<String> = _descText

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _cardClickable = mutableStateOf(true)
    val cardClickable: State<Boolean> = _cardClickable

    private val _showDialog = mutableStateOf<Int?>(null)
    val showDialog: State<Int?> = _showDialog

    val allNotes: Flow<List<Note>> = dao.fetchNotes()

    fun setTitleText(titleText: String) {
        _titleText.value = titleText
    }

    fun setDescText(descText: String) {
        _descText.value = descText
    }

    fun cardClickable(state: Boolean) {
        _cardClickable.value = state
    }

    fun showDialog(noteID: Int?) {
        _showDialog.value = noteID
    }

    fun saveProgress(
        note: Note,
        newProgress: Int,
        newColor: String
    ) {
        viewModelScope.launch {
            dao.insert(
                Note(
                    id = note.id,
                    title = note.title,
                    desc = note.desc,
                    progress = newProgress,
                    color = "#$newColor"
                )
            )
            showDialog(null)
        }
    }

    fun saveData(
        id: Int?, onSuccess: () -> Unit
    ) {
        when {
            _titleText.value.trim().isEmpty() -> {
                _toastMessage.value = "Enter the title"
            }

            _descText.value.trim().isEmpty() -> {
                _toastMessage.value = "Enter the description"
            }

            else -> {
                viewModelScope.launch {
                    if (id != null) {
                        dao.insert(
                            Note(
                                id = id,
                                title = _titleText.value,
                                desc = _descText.value
                            )
                        )
                    } else {
                        dao.insert(Note(title = _titleText.value, desc = _descText.value))
                    }

                    onSuccess()
                    clearDataFromViewModel()
                }
            }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun clearDataFromViewModel() {
        _titleText.value = ""
        _descText.value = ""
    }
}