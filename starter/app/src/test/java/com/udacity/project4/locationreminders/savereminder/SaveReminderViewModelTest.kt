package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.FakeReminderTestRepository
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.EspressoIdlingResource
import junit.framework.Assert.assertEquals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock

@ExperimentalCoroutinesApi
class SaveReminderViewModelTest {


    // provide testing to the SaveReminderView and its live data objects
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var tasksRepository: FakeReminderTestRepository

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        // We initialise the repository with no tasks
        tasksRepository = FakeReminderTestRepository()
        saveReminderViewModel = SaveReminderViewModel(Application(), tasksRepository)
    }

    @Test
    fun check_loading() {
        val data = ReminderDataItem(
            "title",
            "description",
            "location",
            22.8869,
            88.657,
            "id"
        )
        saveReminderViewModel?.validateAndSaveReminder(data)
        saveReminderViewModel?.showLoading?.observeForever {}
        assertEquals(saveReminderViewModel?.showLoading?.value, null)
    }

    @Test
    fun shouldReturnError() {
        val data = ReminderDataItem(
            "title",
            "description",
            "location",
            null,
            null,
            "id"
        )
        saveReminderViewModel?.validateAndSaveReminder(data)
        saveReminderViewModel?.showLoading?.observeForever {}
        assertEquals(saveReminderViewModel?.showErrorMessage?.value, null)
    }
}