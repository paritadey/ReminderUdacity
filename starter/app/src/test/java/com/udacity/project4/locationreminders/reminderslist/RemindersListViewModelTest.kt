package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import com.google.common.truth.ExpectFailure.assertThat
import com.udacity.project4.MyApp
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    // provide testing to the RemindersListViewModel and its live data objects
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    private var viewModel: RemindersListViewModel? = null
    var fakeDataSource: FakeDataSource? = null
    lateinit var data: ReminderDTO
    lateinit var reminderListViewModel: RemindersListViewModel
    lateinit var reminderRepository: RemindersLocalRepository
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

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

    @Mock
    var dataSource: ReminderDataSource? = null

    @Mock
    var remindersDao: RemindersDao? = null


    @Mock
    lateinit var lifecycleOwner: LifecycleOwner
    lateinit var lifecycle: Lifecycle

    @Before
    @Throws(Exception::class)
    fun setUp() {

        MockitoAnnotations.initMocks(this@RemindersListViewModelTest)
        data = ReminderDTO(
            id = "abcs",
            title = "hello",
            description = "world",
            latitude = 22.8758,
            longitude = 88.975,
            location = "location"
        )

        lifecycle = LifecycleRegistry(lifecycleOwner)
        viewModel = RemindersListViewModel(MyApp(), FakeDataSource())
        reminderRepository = remindersDao?.let {
            RemindersLocalRepository(
                it,
                Dispatchers.Main
            )
        }!!
        insertNote()
    }

    private fun insertNote() {
        fakeDataSource = FakeDataSource(MutableList<ReminderDTO>(5) {
            data
        })
        reminderListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource!!
        )
        mainCoroutineRule.runBlockingTest {
            reminderRepository.saveReminder(data)
        }
    }

    @Test
    fun testNull() {
        mainCoroutineRule.runBlockingTest {
            Mockito.`when`(
                dataSource?.getReminders()
            ).thenReturn(null)
            Assert.assertNotNull(viewModel?.remindersList)

        }
    }

    @Test
    fun testFetchDataSuccess() {
        mainCoroutineRule.runBlockingTest {
            Mockito.`when`(
                viewModel?.loadReminders()
            ).thenReturn(null)
        }
    }

    @After
    @Throws(java.lang.Exception::class)
    fun tearDown() {
        dataSource = null
        viewModel = null
    }

}