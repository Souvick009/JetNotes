package com.example.jetnotes.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
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

    private val _progress = mutableIntStateOf(0)

    val progress: State<Int> = _progress

    private val _hexColor = mutableStateOf<String?>(null)

    val hexColor: State<String?> = _hexColor

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    private val _cardClickable = mutableStateOf(true)
    val cardClickable: State<Boolean> = _cardClickable

    private val _showDialog = mutableStateOf<Int?>(null)
    val showDialog: State<Int?> = _showDialog

    private val _deletionDialog = mutableStateOf<Int?>(null)
    val deletionDialog: State<Int?> = _deletionDialog

    val allNotes: Flow<List<Note>> = dao.fetchNotes()

    fun setData(titleText: String, descText: String, progress: Int, hexColor: String? = null) {
        _titleText.value = titleText
        _descText.value = descText
        _progress.value = progress
        _hexColor.value = hexColor
    }

    fun setTitleText(titleText: String) {
        _titleText.value = titleText
    }

    fun setDescText(descText: String) {
        _descText.value = descText
    }

    fun setCardClickable(state: Boolean) {
        _cardClickable.value = state
    }

    fun setShowDialog(noteID: Int?) {
        _showDialog.value = noteID
    }

    fun setDeletionDialog(noteID: Int?) {
        _deletionDialog.value = noteID
    }

    fun saveProgress(
        note: Note,
        newProgress: Int,
        newColor: String
    ) {
        viewModelScope.launch {
            insertIntoDatabase(
                noteID = note.id,
                title = note.title,
                desc = note.desc,
                progress = newProgress,
                color = "#$newColor"
            )
//            dao.insert(
//                Note(
//                    id = note.id,
//                    title = note.title,
//                    desc = note.desc,
//                    progress = newProgress,
//                    color = "#$newColor"
//                )
//            )
            setShowDialog(null)
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
                        insertIntoDatabase(
                            noteID = id,
                            title = _titleText.value,
                            desc = _descText.value,
                            progress = _progress.value,
                            color = _hexColor.value
                        )
//                        dao.insert(
//                            Note(
//                                id = id,
//                                title = _titleText.value,
//                                desc = _descText.value
//                            )
//                        )
                    } else {
                        insertIntoDatabase(
                            title = _titleText.value,
                            desc = _descText.value,
                        )
//                        dao.insert(Note(title = _titleText.value, desc = _descText.value))
                    }

                    onSuccess()
                    clearDataFromViewModel()
                }
            }
        }
    }

    fun deleteData(noteID: Int) {
        viewModelScope.launch {
            dao.delete(noteID)
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun clearDataFromViewModel() {
        _titleText.value = ""
        _descText.value = ""
    }

    suspend fun insertIntoDatabase(
        noteID: Int? = null,
        title: String,
        desc: String,
        progress: Int = 0,
        color: String? = null
    ) {

        if (noteID != null) { // If Note already exists
            dao.insert(
                Note(
                    id = noteID,
                    title = title,
                    desc = desc,
                    progress = progress,
                    color = color
                )
            )
        } else { // if a new note is going to be created
            dao.insert(
                Note(
                    title = title,
                    desc = desc,
                )
            )
        }

    }
}