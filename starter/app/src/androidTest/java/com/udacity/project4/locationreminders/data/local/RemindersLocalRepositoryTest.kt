package com.udacity.project4.locationreminders.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.succeeded
import com.udacity.project4.util.DataBindingIdlingResource
import com.udacity.project4.util.monitorActivity
import com.udacity.project4.utils.EspressoIdlingResource
import com.udacity.project4.utils.EspressoIdlingResource.wrapEspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.LinkedHashMap
import java.util.function.Predicate.isEqual

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest : ReminderDataSource{

    //    Add testing implementation to the RemindersLocalRepository.kt
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
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
    var reminderData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()
    private val observableReminders = MutableLiveData<kotlin.Result<List<ReminderDTO>>>()

    @Before
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Before
    fun createRepository() {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        tasksRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        // Get a reference to the class under test
        tasksRepository = RemindersLocalRepository(
            tasksLocalDataSource, Dispatchers.Main
        )
        activityScenario.close()
    }

    @Test
    fun saveReminder_retrievesReminder() = runBlockingTest {
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        // GIVEN - A new task saved in the database.
        val newTask = ReminderDTO("title", "description", "22.87451, 88.69471", 22.87451, 88.69471)
        tasksRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        // Get a reference to the class under test
        tasksRepository = RemindersLocalRepository(
            tasksLocalDataSource, Dispatchers.Main
        )
        tasksRepository.saveReminder(newTask)
        // WHEN  - Task retrieved by ID.
        val result = tasksRepository.getReminder(newTask.id)

        // THEN - Same task is returned.
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat(result.data.title, `is`("title"))
        assertThat(result.data.description, `is`("description"))
        activityScenario.close()
    }

    @Test
    fun getTasks_requestsAllTasks() = runBlockingTest {
        // When tasks are requested from the tasks repository
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)
        val tasks = tasksRepository.getReminders() as Result.Success
        assertThat(tasks.data, IsEqual(remoteTasks))
        activityScenario.close()
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return Result.Success(reminderData.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderData[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        reminderData[id]?.let {
            return Result.Success(it)
        }
        return Result.Error("Could not find reminder")
    }

    override suspend fun deleteAllReminders() {
        reminderData.clear()
    }
}