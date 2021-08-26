package `in`.thescriptgroup.attendance.repo

import `in`.thescriptgroup.attendance.api.AttendanceService
import `in`.thescriptgroup.attendance.models.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SubjectRepo @Inject constructor(private val attendanceService: AttendanceService) {

    suspend fun fetchSubjects(username: String, password: String): List<Subject>? =
        withContext(Dispatchers.IO) {
            var list: List<Subject>?
            try {
                list = attendanceService.getAttendance(username, password)

            } catch (e: Exception) {
                list = null
                e.printStackTrace()
            }
            return@withContext list
        }
}
