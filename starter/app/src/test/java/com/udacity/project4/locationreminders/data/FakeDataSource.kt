package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersDao

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    //    TODO: Create a fake data source to act as a double to the real data source
    private lateinit var dao: RemindersDao


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        TODO("Return the reminders")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        val note = ReminderDTO(
            "12gduncjd",
            "Test",
            "Testing checking using espresso",
            22.874548,
            88.65741289,
            "2021-05-29, 17:48:55"
        )
        dao.saveReminder(note)

    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        TODO("return the reminder with the id")
    }

    override suspend fun deleteAllReminders() {
        TODO("delete all the reminders")
    }


}