package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Announcement
import com.example.data.AppRepository
import com.example.data.Attendance
import com.example.data.Event
import com.example.data.Role
import com.example.data.Submission
import com.example.data.Task
import com.example.data.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    val events: StateFlow<List<Event>> = repository.allEvents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val announcements: StateFlow<List<Announcement>> = repository.allAnnouncements.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val tasks: StateFlow<List<Task>> = repository.allTasks.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _currentUserRole = MutableStateFlow<Role?>(null)
    val currentUserRole: StateFlow<Role?> = _currentUserRole.asStateFlow()

    fun login(role: Role) {
        _currentUserRole.value = role
    }

    fun logout() {
        _currentUserRole.value = null
    }

    fun addEvent(title: String, date: String, time: String, venue: String, agenda: String, targetGroup: String, eventType: String) {
        viewModelScope.launch {
            val role = _currentUserRole.value
            val isPublished = role == Role.ADMIN || role == Role.TEACHER
            repository.insertEvent(Event(title = title, date = date, time = time, venue = venue, agenda = agenda, targetGroup = targetGroup, eventType = eventType, isPublished = isPublished))
        }
    }

    fun approveEvent(event: Event) {
        viewModelScope.launch {
            repository.updateEvent(event.copy(isPublished = true))
        }
    }

    fun addAnnouncement(title: String, content: String) {
        viewModelScope.launch {
            repository.insertAnnouncement(Announcement(title = title, content = content))
        }
    }

    fun addTask(title: String, description: String, dueDate: String) {
        viewModelScope.launch {
            repository.insertTask(Task(title = title, description = description, dueDate = dueDate))
        }
    }

    fun addSubmission(taskId: Int, studentName: String, content: String) {
        viewModelScope.launch {
            repository.insertSubmission(Submission(taskId = taskId, studentName = studentName, content = content))
        }
    }

    fun markAttendance(eventId: Int, studentName: String, isPresent: Boolean) {
        viewModelScope.launch {
            repository.insertAttendance(Attendance(eventId = eventId, studentName = studentName, isPresent = isPresent))
        }
    }

    fun getSubmissions(taskId: Int): Flow<List<Submission>> = repository.getSubmissions(taskId)
    fun getAttendance(eventId: Int): Flow<List<Attendance>> = repository.getAttendance(eventId)
}

class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
