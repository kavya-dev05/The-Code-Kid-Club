package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Role
import com.example.viewmodel.AppViewModel

@Composable
fun AttendanceTab(viewModel: AppViewModel) {
    val role by viewModel.currentUserRole.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    
    // Only published events should have attendance marked generally
    val publishedEvents = events.filter { it.isPublished }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Event Attendance",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (role == Role.STUDENT) {
                    Text(
                        text = "Mark your attendance for events here.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text(
                        text = "Review attendance records here.",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            if (publishedEvents.isEmpty()) {
                item {
                    Text("No events available for attendance.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            }

            items(publishedEvents) { event ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = "${event.date} • ${event.venue}", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(16.dp))

                        if (role == Role.STUDENT) {
                            var studentName by remember { mutableStateOf("") }
                            var isMarked by remember { mutableStateOf(false) }
                            
                            if (!isMarked) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = studentName,
                                        onValueChange = { studentName = it },
                                        label = { Text("Your Name") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = { 
                                            viewModel.markAttendance(event.id, studentName, true)
                                            isMarked = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
                                    ) {
                                        Text("Present")
                                    }
                                }
                            } else {
                                Text("✓ Attendance Marked", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // Teacher or Admin view
                            var showRecords by remember { mutableStateOf(false) }
                            OutlinedButton(
                                onClick = { showRecords = !showRecords },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(if (showRecords) "Hide Records" else "View Records")
                            }
                            if (showRecords) {
                                val attendances by viewModel.getAttendance(event.id).collectAsStateWithLifecycle(initialValue = emptyList())
                                Spacer(modifier = Modifier.height(8.dp))
                                if (attendances.isEmpty()) {
                                    Text("No attendance marked.", style = MaterialTheme.typography.bodySmall)
                                } else {
                                    attendances.forEach { att ->
                                        Text("• ${att.studentName}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
