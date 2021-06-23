package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers
import com.udacity.project4.NoteFactory
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.*

@ExperimentalCoroutinesApi
class RemindersLocalRepositoryTest {
    private lateinit var localDataSource: ReminderLocalDataSource
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource =
            ReminderLocalDataSource(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @Test
    fun saveReminder_retrievesReminder() = runBlocking {
        val newTask = ReminderDTO("title", "description", "22.8745, 88.6971", 22.8745, 88.6971)
        localDataSource.saveReminder(newTask)
        val result = localDataSource.getReminder(newTask.id)
        result as Result.Success
        ViewMatchers.assertThat(result.data.title, CoreMatchers.`is`("title"))
        ViewMatchers.assertThat(result.data.description, CoreMatchers.`is`("description"))
    }

    @Test
    fun completeReminder_retrievedReminderIsComplete() = runBlocking {
        val newTask = ReminderDTO("title", "description", "22.8745, 88.6971", 22.8745, 88.6971)
        localDataSource.saveReminder(newTask)
        val result = localDataSource.getReminder(newTask.id)
        result as Result.Success
        ViewMatchers.assertThat(result.data.title, CoreMatchers.`is`(newTask.title))
    }

    @After
    fun cleanUp() {
        database.close()
    }
}