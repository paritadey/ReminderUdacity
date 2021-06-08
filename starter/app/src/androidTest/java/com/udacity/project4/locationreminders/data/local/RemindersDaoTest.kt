package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.NoteFactory
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    //    TODO: Add testing implementation to the RemindersDao.kt
    private lateinit var noteDatabase: RemindersDatabase
    private lateinit var dao: RemindersDao

    @Before
    fun initDb() {
        noteDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = noteDatabase.reminderDao()
    }

    @After
    fun closeDb() {
        noteDatabase.close()
    }

    @Test
    suspend fun insertNotesSaveData() {
        val cachedNotes = NoteFactory.makeNoteEntity()
        noteDatabase.reminderDao().saveReminder(cachedNotes)
        val notes = noteDatabase.reminderDao().getReminders()
        assert(notes.isNotEmpty())
    }

    @Test
    suspend fun getNotesRetrieveData() {
        val notes = NoteFactory.makeNoteList(5)
        notes.forEach { noteDatabase.reminderDao().saveReminder(it) }
        val retrievedNotes = noteDatabase.reminderDao().getReminders()
        assert(retrievedNotes == notes.sortedWith(compareBy({ it.id }, { it.id })))
    }

    @Test
    suspend fun clearNotesClearsData() {
        val notes = NoteFactory.makeNote()
        noteDatabase.reminderDao().saveReminder(notes)
        noteDatabase.reminderDao().deleteAllReminders()
        assert(noteDatabase.reminderDao().getReminders().isEmpty())
    }
}