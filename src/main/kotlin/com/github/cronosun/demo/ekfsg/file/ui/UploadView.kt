package com.github.cronosun.demo.ekfsg.file.ui

import com.github.cronosun.demo.ekfsg.file.FileOutputStream
import com.github.cronosun.demo.ekfsg.file.Filename
import com.github.cronosun.demo.ekfsg.file.MimeType
import com.github.cronosun.demo.ekfsg.file.NewFile
import com.github.mvysny.kaributools.label
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.upload.Receiver
import com.vaadin.flow.component.upload.Upload
import java.io.OutputStream

class UploadView(val controller: UploadController, val label: String = "") : Composite<Upload>() {

    private var currentStream: FileOutputStream? = null
    private var fileName: String? = null
    private var mimeType: String? = null

    override fun initContent(): Upload {
        val upload = Upload()
        upload.receiver = LocalReceiver(this)
        upload.isDropAllowed = true
        upload.addSucceededListener { onSucceeded() }
        upload.label = label
        return upload
    }

    private fun onSucceeded() {
        // TODO: No error handling in this demo
        val fileContentId = currentStream!!.finish()
        val fileName = Filename(fileName!!)
        val mimeType = MimeType.fromStringOrOctetStream(mimeType)
        val file = NewFile(fileContentId, fileName, mimeType)
        val savedFile = controller.fileStorageService.save(file)
        controller.state = UploadState.FileUploaded(savedFile)
    }

    private fun receiveUpload(fileName: String?, mimeType: String?): OutputStream {
        val stream = controller.fileContentService.saveContent()
        this.fileName = fileName
        this.mimeType = mimeType
        this.currentStream = stream
        return stream
    }

    private class LocalReceiver(val view: UploadView) : Receiver {
        override fun receiveUpload(fileName: String?, mimeType: String?): OutputStream {
            return view.receiveUpload(fileName, mimeType)
        }
    }
}