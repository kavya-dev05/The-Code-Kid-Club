package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: AppDao) {
    val allUsers: Flow<List<User>> = dao.getAllUsers()
    val allEvents: Flow<List<Event>> = dao.getAllEvents()
    val allAnnouncements: Flow<List<Announcement>> = dao.getAllAnnouncements()
    val allTasks: Flow<List<Task>> = dao.getAllTasks()

    suspend fun insertUser(user: User) = dao.insertUser(user)
    suspend fun insertEvent(event: Event) = dao.insertEvent(event)
    suspend fun updateEvent(event: Event) = dao.updateEvent(event)
    suspend fun insertAnnouncement(announcement: Announcement) = dao.insertAnnouncement(announcement)
    
    suspend fun insertTask(task: Task) = dao.insertTask(task)
    fun getSubmissions(taskId: Int) = dao.getSubmissionsForTask(taskId)
    suspend fun insertSubmission(submission: Submission) = dao.insertSubmission(submission)
    
    fun getAttendance(eventId: Int) = dao.getAttendanceForEvent(eventId)
    suspend fun insertAttendance(attendance: Attendance) = dao.insertAttendance(attendance)
}
