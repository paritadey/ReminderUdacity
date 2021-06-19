package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest

class ReminderLocalDataSourceTest {
    private lateinit var localDataSource: ReminderLocalDataSource
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()
    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
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
    fun saveTask_retrievesTask() = runBlocking {
        // GIVEN - a new task saved in the database
        val newTask = ReminderDTO("title", "description", "22.8745, 88.6971",22.8745, 88.6971)
        localDataSource.saveReminder(newTask)

        // WHEN  - Task retrieved by ID
        val result = localDataSource.getReminder(newTask.id)

        // THEN - Same task is returned
        Assert.assertThat(result.succeeded, CoreMatchers.`is`(true))
        result as Result.Success
        Assert.assertThat(result.data.title, CoreMatchers.`is`("title"))
        Assert.assertThat(result.data.description, CoreMatchers.`is`("description"))
    }

    @After
    fun cleanUp() {
        database.close()
    }

}