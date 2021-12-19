package com.github.cronosun.demo.ekfsg.file.ui

import com.github.cronosun.demo.ekfsg.file.FileStorageService
import com.github.cronosun.demo.ekfsg.file.SavedFile
import io.reactivex.rxjava3.core.Observable

class UploadsController(
    private val fileStorageService: FileStorageService,
) {

    val filesInTable: Observable<List<FileInTable>>
        get() {
            return Observable.create {
                val fromDatabase = loadFromDatabase()
                it.onNext(fromDatabase)
            }
        }

    private fun loadFromDatabase(): List<FileInTable> {
        return fileStorageService.listSortedByDateDescending(100).map { toFileInTable(it) }.toList()
    }

    private fun toFileInTable(file: SavedFile): FileInTable {
        return FileInTable(file.filename.name, file.createdBy.id)
    }
}

data class FileInTable(val name: String, val uploadedBy: String)