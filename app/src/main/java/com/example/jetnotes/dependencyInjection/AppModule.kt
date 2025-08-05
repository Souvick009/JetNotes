package com.example.jetnotes.dependencyInjection

import android.app.Application
import androidx.room.Room
import com.example.jetnotes.roomDB.NotesDao
import com.example.jetnotes.roomDB.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application) : NotesDatabase {
        return Room.databaseBuilder(
            app,
            NotesDatabase::class.java,
            "note_db"
        ).build()
    }

    @Provides
    fun provideNoteDao(db: NotesDatabase) : NotesDao = db.dao
}