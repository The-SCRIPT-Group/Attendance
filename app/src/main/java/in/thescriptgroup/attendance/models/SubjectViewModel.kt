package `in`.thescriptgroup.attendance.models

import `in`.thescriptgroup.attendance.repo.SubjectRepo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(private val subjectRepo: SubjectRepo) : ViewModel() {

    val apiData = MutableLiveData<List<Subject>?>()


    fun getSubject(username: String, password: String) {
        viewModelScope.launch {
            apiData.value = subjectRepo.fetchSubjects(username, password)
        }
    }
}