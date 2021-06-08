package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.TestCoroutineRule
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import io.mockk.MockKAnnotations
import junit.framework.Assert.assertNotNull

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    //TODO: provide testing to the SaveReminderView and its live data objects
    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var dataSource: ReminderDataSource

    @Mock
    private lateinit var showLoading: Observer<Boolean>
    @Mock
    private lateinit var tasksViewModel:SaveReminderViewModel

    @Before
    fun setupViewModel() {
         tasksViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @Test
    fun saveReminder_shouldReturnSuceess() {
        val data = ReminderDataItem(
            "title",
            "description",
            "location",
            22.8869,
            88.657,
            "id"
        )
        testCoroutineRule.runBlockingTest {
            tasksViewModel.validateAndSaveReminder(data)
            tasksViewModel.showLoading.observeForever(showLoading)
        }

    }

}