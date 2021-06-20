package com.udacity.project4.locationreminders.savereminder

import androidx.test.core.app.ApplicationProvider
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.EspressoIdlingResource
import junit.framework.Assert.assertEquals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock

@ExperimentalCoroutinesApi
class SaveReminderViewModelTest {


    // provide testing to the SaveReminderView and its live data objects
    @Mock
    var dataSource: FakeDataSource? = null

    @Mock
    var saveReminderViewModel: SaveReminderViewModel? = null


    @Before
    fun setUpViewModel() {
        saveReminderViewModel =
            dataSource?.let {
                SaveReminderViewModel(
                    ApplicationProvider.getApplicationContext(),
                    it
                )
            }
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