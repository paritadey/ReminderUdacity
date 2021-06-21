package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.NoteFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    //    TODO: Add testing implementation to the RemindersDao.kt
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

   @Test
    suspend fun insertNotesSaveData() {
        val cachedNotes = NoteFactory.makeNoteEntity()
       database.reminderDao().saveReminder(cachedNotes)
        val notes = database.reminderDao().getReminders()
        assert(notes.isNotEmpty())
    }

    @Test
    suspend fun getNotesRetrieveData() {
        val notes = NoteFactory.makeNoteList(5)
        notes.forEach { database.reminderDao().saveReminder(it) }
        val retrievedNotes = database.reminderDao().getReminders()
        assert(retrievedNotes == notes.sortedWith(compareBy({ it.id }, { it.id })))
    }

    @Test
    suspend fun clearNotesClearsData() {
        val notes = NoteFactory.makeNote()
        database.reminderDao().saveReminder(notes)
        database.reminderDao().deleteAllReminders()
        assert(database.reminderDao().getReminders().isEmpty())
    }
}