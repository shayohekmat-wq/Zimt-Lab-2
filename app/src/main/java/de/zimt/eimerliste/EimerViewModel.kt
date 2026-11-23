package de.zimt.eimerliste

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.zimt.eimerliste.data.EimerDatabase
import de.zimt.eimerliste.data.EimerRepository
import de.zimt.eimerliste.domain.BucketMaterial
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

class EimerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = EimerRepository(EimerDatabase.get(application).dao())

    val buckets = repository.buckets.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )
    val counts = repository.counts.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    val selectedBucketId = MutableStateFlow<String?>(null)

    val selectedNotes = selectedBucketId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.notes(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectBucket(id: String) {
        selectedBucketId.value = id
    }

    suspend fun addBucket(name: String, material: BucketMaterial) {
        selectBucket(repository.createBucket(name, material))
    }

    suspend fun renameBucket(id: String, name: String) {
        repository.renameBucket(id, name)
    }

    suspend fun addNote(bucketId: String, text: String, drawingPayload: String? = null): String =
        repository.createNote(bucketId, text, drawingPayload)

    suspend fun moveNote(noteId: String, sourceBucketId: String, destinationBucketId: String) =
        repository.moveNote(noteId, sourceBucketId, destinationBucketId)

    suspend fun randomNote(bucketId: String) = repository.randomNote(bucketId)
}
