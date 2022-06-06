package com.android.cleanarchitecturenoteapp.feature_note.domain.use_case

import com.android.cleanarchitecturenoteapp.feature_note.data.repository.FakeRepository
import com.android.cleanarchitecturenoteapp.feature_note.domain.model.InvalidNoteException
import com.android.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test


class AddNoteUseCaseTest {

    private lateinit var addNoteUseCase: AddNoteUseCase
    private lateinit var getNoteUseCase: GetNoteUseCase
    private lateinit var fakeRepository: FakeRepository

    @Before
    fun setUp() {
        fakeRepository = FakeRepository()
        addNoteUseCase = AddNoteUseCase(fakeRepository)
        getNoteUseCase = GetNoteUseCase(fakeRepository)
    }

    @Test(expected = InvalidNoteException::class)
    fun `add note with blank title, throw invalid note exception`() = runBlocking {
        val noteToAdd = Note(
            title = "",
            content = "content",
            timeStamp = 1L,
            color = 1,
            id = 1
        )
        addNoteUseCase(noteToAdd)
    }

    @Test(expected = InvalidNoteException::class)
    fun `add note with blank content, throw invalid note exception`() = runBlocking {
        val noteToAdd = Note(
            title = "title",
            content = "",
            timeStamp = 1L,
            color = 1,
            id = 1
        )
        addNoteUseCase(noteToAdd)
    }

    @Test
    fun `add note with, note is added in DB`() = runBlocking {
        val noteToAdd = Note(
            title = "title",
            content = "content",
            timeStamp = 1L,
            color = 1,
            id = 1
        )
        addNoteUseCase(noteToAdd)
        assertThat(getNoteUseCase(1)).isEqualTo(noteToAdd)
    }
}