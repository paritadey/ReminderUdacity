package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.MyApp
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.utils.EspressoIdlingResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private var viewModel: RemindersListViewModel? = null
    var fakeDataSource: FakeDataSource? = null
    lateinit var data: ReminderDTO
    lateinit var reminderListViewModel: RemindersListViewModel
    lateinit var reminderRepository: RemindersLocalRepository


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
                Dispatchers.Unconfined
            )
        }!!
        insertNote()
    }

    private fun insertNote() {
        EspressoIdlingResource.wrapEspressoIdlingResource {
            fakeDataSource = FakeDataSource(MutableList<ReminderDTO>(5) {
                data
            })
            reminderListViewModel = RemindersListViewModel(
                ApplicationProvider.getApplicationContext(),
                fakeDataSource!!
            )
            runBlockingTest {
                reminderRepository.saveReminder(data)
            }
        }
    }

    @Test
    fun testNull() {
        runBlockingTest {
            Mockito.`when`(
                dataSource?.getReminders()
            ).thenReturn(null)
            Assert.assertNotNull(viewModel?.remindersList)

        }
    }

    @Test
    fun testFetchDataSuccess() {
        EspressoIdlingResource.wrapEspressoIdlingResource {
            runBlockingTest {
                Mockito.`when`(
                    viewModel?.loadReminders()
                ).thenReturn(null)
            }
        }
    }

    @After
    @Throws(java.lang.Exception::class)
    fun tearDown() {
        dataSource = null
        viewModel = null
    }

}