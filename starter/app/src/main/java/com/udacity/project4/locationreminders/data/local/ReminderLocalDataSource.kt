package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.udacity.project4.locationreminders.data.dto.Result

class ReminderLocalDataSource internal constructor(
    private val remindersDao: RemindersDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ReminderDataSource {
    override suspend fun getReminders(): Result<List<ReminderDTO>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(remindersDao.getReminders())
        } catch (e: Exception) {
            Result.Error(e.toString())
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersDao.saveReminder(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> = withContext(ioDispatcher) {
        try {
            val task = remindersDao.getReminderById(id)
            if (task != null) {
                return@withContext Result.Success(task)
            } else {
                return@withContext Result.Error("Task not found!")
            }
        } catch (e: Exception) {
            return@withContext Result.Error(e.toString())
        }
    }

    override suspend fun deleteAllReminders() {
        remindersDao.deleteAllReminders()
    }
}