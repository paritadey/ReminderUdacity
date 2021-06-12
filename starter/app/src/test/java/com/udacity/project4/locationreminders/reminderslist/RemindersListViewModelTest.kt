package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.lifecycle.*
import com.udacity.project4.locationreminders.data.ReminderDataSource
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
/*
    @Rule
    var instantExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()
*/

    private var viewModel: RemindersListViewModel? = null

    @Mock
    var dataSource: ReminderDataSource? = null

//    @Mock
//    var observer: Observer<List<ReminderDataItem>?>? = null

    @Mock
    lateinit var lifecycleOwner: LifecycleOwner
    lateinit var lifecycle: Lifecycle

    @Before
    @Throws(Exception::class)
    fun setUp() {
//        MockitoAnnotations.initMocks(this)
      //  lifecycle = LifecycleRegistry(lifecycleOwner)
        MockitoAnnotations.initMocks(this@RemindersListViewModelTest)
        lifecycle = LifecycleRegistry(lifecycleOwner)
        viewModel = dataSource?.let { RemindersListViewModel(Application(), it) }
//        viewModel?.remindersList?.observeForever(observer)
    }

    @Test
    fun testNull() {
        runBlockingTest {
            Mockito.`when`(
                dataSource?.getReminders()).thenReturn(null)
            Assert.assertNotNull(viewModel?.remindersList)
           //Assert.assertTrue(viewModel?.remindersList?.hasObservers()==true)
           //viewModel?.remindersList?.let { Assert.assertTrue(it.hasObservers()) }

        }
    }

    @Test
    fun testFetchDataSuccess() {
        runBlockingTest {
            Mockito.`when`(
                viewModel?.loadReminders()).thenReturn(null)
        }
    }

    @After
    @Throws(java.lang.Exception::class)
    fun tearDown() {
        dataSource = null
        viewModel = null
    }

}