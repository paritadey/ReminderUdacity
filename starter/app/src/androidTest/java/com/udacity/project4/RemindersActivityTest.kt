package com.udacity.project4

import android.R
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.InstrumentationRegistry
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.ReminderListFragment
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import com.udacity.project4.locationreminders.savereminder.SaveReminderFragment
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.android.synthetic.main.activity_reminders.*
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito


@RunWith(AndroidJUnit4::class)
@LargeTest
//END TO END test to black box test the app
class RemindersActivityTest :
    AutoCloseKoinTest() {// Extended Koin Test - embed autoclose @after method to close Koin after every test

    private lateinit var repository: ReminderDataSource
    private lateinit var appContext: Application

    /**
     * As we use Koin as a Service Locator Library to develop our code, we'll also use Koin to test our code.
     * at this step we will initialize Koin related code to be able to use it in out testing.
     */
    @Before
    fun init() {
        stopKoin()//stop the original app koin
        appContext = getApplicationContext()
        val myModule = module {
            viewModel {
                RemindersListViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single {
                SaveReminderViewModel(
                    appContext,
                    get() as ReminderDataSource
                )
            }
            single { RemindersLocalRepository(get()) as ReminderDataSource }
            single { LocalDB.createRemindersDao(appContext) }
        }
        //declare a new koin module
        startKoin {
            modules(listOf(myModule))
        }
        //Get our real repository
        repository = get()

        //clear the data to start fresh
        runBlocking {
            repository.deleteAllReminders()
        }
        ActivityScenario.launch(RemindersActivity::class.java)
        onView(ViewMatchers.withId(com.udacity.project4.R.id.addReminderFAB)).perform(ViewActions.click())
    }


    //    TODO: add End to End testing to the app
    @Test
    fun snackBarTest(){
        onView(ViewMatchers.withId(com.udacity.project4.R.id.saveReminder)).perform(ViewActions.click())
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("Please enter title")))
    }

    @Test
    fun toastTest() {
        onView(ViewMatchers.withId(com.udacity.project4.R.id.reminderTitle)).perform(ViewActions.typeText("Test"))
        onView(ViewMatchers.withId(com.udacity.project4.R.id.reminderDescription)).perform(ViewActions.typeText("Description"))
        onView(ViewMatchers.withId(com.udacity.project4.R.id.selectLocation)).perform(ViewActions.click())
        onView(isRoot()).perform(waitFor(5000))
        onView(ViewMatchers.withId(com.udacity.project4.R.id.proceed)).perform(ViewActions.click())
        onView(isRoot()).perform(waitFor(2000))
        onView(ViewMatchers.withId(com.udacity.project4.R.id.saveReminder)).perform(ViewActions.click())
        onView(isRoot()).perform(waitFor(2000))
        onView(withText("Reminder Saved !")).inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
    }

    @Test
    fun shouldReturnError(){
        onView(withId(com.udacity.project4.R.id.saveReminder)).perform(ViewActions.click())
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("Please enter title")
            )
        )
        onView(ViewMatchers.withId(com.udacity.project4.R.id.reminderTitle)).perform(ViewActions.typeText("Test"))
        onView(ViewMatchers.isRoot()).perform(ViewActions.closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(8000))
        onView(withId(com.udacity.project4.R.id.saveReminder)).perform(ViewActions.click())
        onView(withId(com.google.android.material.R.id.snackbar_text)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("Please select location")
            )
        )
    }

    fun waitFor(delay: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}
