package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.NoteFactory
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.*

@ExperimentalCoroutinesApi
class RemindersLocalRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    private lateinit var database: RemindersDatabase

    @Before
    fun setUpDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @Test
    fun insertReminder() = runBlockingTest {
        val reminder = NoteFactory.makeNote()
        database.reminderDao().saveReminder(reminder)
        val reminderData = database.reminderDao().getReminders()
        assert(reminderData.isNotEmpty())
    }

    @Test
    fun clearRemindersClearsData() = runBlockingTest {
        val reminder = NoteFactory.makeNote()
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().deleteAllReminders()
        assert(database.reminderDao().getReminders().isEmpty())
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun checkReminder() = runBlockingTest {
        val reminder = NoteFactory.makeNote()
        database.reminderDao().saveReminder(reminder)
        val loaded = database.reminderDao().getReminderById(reminder.id)
        MatcherAssert.assertThat<ReminderDTO>(loaded as ReminderDTO, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.id, CoreMatchers.`is`(reminder.id))
        MatcherAssert.assertThat(loaded.title, CoreMatchers.`is`(reminder.title))
        MatcherAssert.assertThat(loaded.description, CoreMatchers.`is`(reminder.description))
    }

}