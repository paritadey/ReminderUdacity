package com.udacity.project4.locationreminders.data.local

import android.app.Application
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
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
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
import org.junit.*
import org.junit.runner.RunWith
import java.util.LinkedHashMap
import java.util.function.Predicate.isEqual

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest : ReminderDataSource {

    var reminderData: LinkedHashMap<String, ReminderDTO> = LinkedHashMap()

    private lateinit var tasksRemoteDataSource: ReminderDataSource
    private val task1 =
        ReminderDTO("Title1", "Description1", "location1", 22.578, 88.6048, "ID1")
    private val task2 =
        ReminderDTO("Title2", "Description2", "location2", 22.59878, 88.7848, "ID2")
    private val remoteReminder = listOf(task1, task2).sortedBy { it.id }
    private lateinit var viewModel: SaveReminderViewModel

    @Before
    fun createRepository() {
        tasksRemoteDataSource = FakeDataSource(remoteReminder.toMutableList())
        viewModel = SaveReminderViewModel(Application(), tasksRemoteDataSource)
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

    @Test
    fun getReminder_requestsAllRemindersFromDataSource() = runBlockingTest {
        // When tasks are requested from the tasks repository
        val tasks = tasksRemoteDataSource.getReminders() as Result.Success
        // Then tasks are loaded from the remote data source
        Assert.assertThat(tasks.data, IsEqual(remoteReminder))
    }

}