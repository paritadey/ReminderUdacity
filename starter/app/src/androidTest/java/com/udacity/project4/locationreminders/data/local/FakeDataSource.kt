package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.Success
import java.lang.Exception

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var remindertask: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

    //  Create a fake data source to act as a double to the real data source

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        remindertask?.let { return Success(ArrayList(it)) }
        return Result.Error(Exception("Reminder not found").toString())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindertask?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        TODO("return the reminder with the id")
    }

    override suspend fun deleteAllReminders() {
        remindertask?.clear()
    }


}