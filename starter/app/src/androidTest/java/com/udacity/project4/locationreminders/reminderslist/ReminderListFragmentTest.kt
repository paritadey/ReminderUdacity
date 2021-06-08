package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.

    @Before
    fun setUp() {
        ActivityScenario.launch(RemindersActivity::class.java)
            .onActivity { it.supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment,ReminderListFragment())}
    }
    @Test
    fun errorMessageTest(){

        onView(withId(R.id.saveReminder)).perform(click())
        onView(withId(R.id.reminderTitle)).check(ViewAssertions.matches(ViewMatchers.hasErrorText("Field cannot be left empty.")))
        onView(withId(R.id.reminderDescription)).check(
            ViewAssertions.matches(
                ViewMatchers.hasErrorText(
                    "Field cannot be left empty."
                )
            )
        )
    }

}