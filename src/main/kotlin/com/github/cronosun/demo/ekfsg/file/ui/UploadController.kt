package com.github.cronosun.demo.ekfsg.file.ui

import com.github.cronosun.demo.ekfsg.file.FileContentService
import com.github.cronosun.demo.ekfsg.file.FileStorageService
import com.github.cronosun.demo.ekfsg.file.SavedFile
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class UploadController(val fileStorageService: FileStorageService, val fileContentService: FileContentService) {
    private val internalState: BehaviorSubject<UploadState> =
        BehaviorSubject.createDefault(UploadState.NoFileUploadedYet)

    val observableState: Observable<UploadState> = internalState

    var state: UploadState
        set(value) = internalState.onNext(value)
        get() = internalState.value!!

    fun reset() {
        internalState.onNext(UploadState.NoFileUploadedYet)
    }
}

/**
 * NOTE: Demonstriert die Verwendung von typsicheren ADTs.
 */
sealed class UploadState {
    object NoFileUploadedYet : UploadState()
    data class FileUploaded(val file: SavedFile) : UploadState()
}

