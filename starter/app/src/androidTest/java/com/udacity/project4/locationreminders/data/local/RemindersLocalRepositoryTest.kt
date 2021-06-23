package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
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
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminder() = runBlockingTest {
        val reminder =
            ReminderDTO("title", "description", "22.87412,88.974512", 22.87412, 88.974512)
        database.reminderDao().saveReminder(reminder)
        val loaded = database.reminderDao().getReminderById(reminder.id)
        MatcherAssert.assertThat<ReminderDTO>(loaded as ReminderDTO, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.id, CoreMatchers.`is`(reminder.id))
        MatcherAssert.assertThat(loaded.title, CoreMatchers.`is`(reminder.title))
        MatcherAssert.assertThat(loaded.description, CoreMatchers.`is`(reminder.description))
    }

}