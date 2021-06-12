package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.ReminderDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
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
    @Rule
    var instantExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    private var viewModel: RemindersListViewModel? = null
    @Mock
    var dataSource: ReminderDataSource? = null

    @Mock
    lateinit var observer: Observer<List<ReminderDataItem>>

    @Mock
    lateinit var lifecycleOwner: LifecycleOwner
    lateinit var lifecycle: Lifecycle

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        lifecycle = LifecycleRegistry(lifecycleOwner)
        viewModel = dataSource?.let { RemindersListViewModel(Application(), it) }
        viewModel?.remindersList?.observeForever(observer)
    }

    @Test
    fun testNull() {
        viewModel?.viewModelScope?.launch {
            Mockito.`when`(
                dataSource?.getReminders()).thenReturn(null)
            Assert.assertNotNull(viewModel?.loadReminders())
            viewModel?.remindersList?.let { Assert.assertTrue(it.hasObservers()) }
        }
    }

    @After
    @Throws(java.lang.Exception::class)
    fun tearDown() {
        dataSource = null
        viewModel = null
    }

}