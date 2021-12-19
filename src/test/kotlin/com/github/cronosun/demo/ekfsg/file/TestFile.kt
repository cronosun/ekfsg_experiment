package com.github.cronosun.demo.ekfsg.file

import com.github.cronosun.demo.ekfsg.file.ui.UploadController
import com.github.cronosun.demo.ekfsg.file.ui.UploadState

class TestFile(private val filename: Filename, private val mimeType: MimeType, private val content: ByteArray) {
    fun uploadToController(controller: UploadController) {
        val contentId = controller.fileContentService.saveContent(content)
        val newFile = NewFile(
            contentId = contentId,
            filename = filename,
            mimeType = mimeType
        )
        val fileId = controller.fileStorageService.save(newFile)
        // file is now in the database, inform the controller.
        controller.state = UploadState.FileUploaded(fileId)
    }
}