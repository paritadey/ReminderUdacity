package com.udacity.project4.locationreminders.data.local

import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.utils.EspressoIdlingResource.wrapEspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    //    TODO: Add testing implementation to the RemindersLocalRepository.kt
    private val task1 =
        ReminderDTO("Title1", "Description1", "location1", 22.578, 88.6548, "ID1")
    private val task2 =
        ReminderDTO("Title2", "Description2", "location2", 22.578, 88.6548, "ID2")
    private val task3 =
        ReminderDTO("Title3", "Description3", "location3", 22.578, 88.6548, "ID3")
    private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
    private val localTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3).sortedBy { it.id }
    private lateinit var tasksRemoteDataSource: FakeDataSource
    private lateinit var tasksLocalDataSource: RemindersDao
    private lateinit var tasksRepository: RemindersLocalRepository

    @Before
    fun createRepository() {
        wrapEspressoIdlingResource{
            tasksRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
            // Get a reference to the class under test
            tasksRepository = RemindersLocalRepository(
                tasksLocalDataSource, Dispatchers.Unconfined
            )
        }
    }
    @Test
    fun getTasks_requestsAllTasksFromRemoteDataSource() = runBlockingTest {
        // When tasks are requested from the tasks repository
        wrapEspressoIdlingResource {
            val tasks = tasksRepository.getReminders() as Result.Success
            assertThat(tasks.data, IsEqual(remoteTasks))
        }
    }

}