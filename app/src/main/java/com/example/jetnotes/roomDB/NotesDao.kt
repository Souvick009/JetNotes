package com.example.jetnotes.roomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Upsert
    suspend fun insert(note: Note)

    @Query(value = "Delete FROM NOTE WHERE id = :noteID")
    suspend fun delete(noteID: Int)

    @Query(value = "SELECT * FROM NOTE")
    fun fetchNotes() : Flow<List<Note>>

    @Query(value = "SELECT * FROM NOTE WHERE id = :id")
    suspend fun fetchNotes(id: Int) : Note?
}