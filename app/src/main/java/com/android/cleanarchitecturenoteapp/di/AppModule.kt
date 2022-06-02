package com.android.cleanarchitecturenoteapp.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.cleanarchitecturenoteapp.feature_note.data.data_source.NoteDatabase
import com.android.cleanarchitecturenoteapp.feature_note.data.repository.NoteRepositoryImp
import com.android.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository
import com.android.cleanarchitecturenoteapp.feature_note.domain.use_case.*
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
    fun provideNoteDatabase(
        app: Application
    ): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(
        db: NoteDatabase
    ): NoteRepository {
        return NoteRepositoryImp(db.noteDao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(
        noteRepository: NoteRepository
    ): NoteUseCases {
        return NoteUseCases(
            getNotesUseCase = GetNotesUseCase(noteRepository),
            getNoteUseCase = GetNoteUseCase(noteRepository),
            addNoteUseCase = AddNoteUseCase(noteRepository),
            deleteNoteUseCase = DeleteNoteUseCase(noteRepository)
        )
    }
}