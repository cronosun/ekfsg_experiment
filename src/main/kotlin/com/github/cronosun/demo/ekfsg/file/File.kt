package com.github.cronosun.demo.ekfsg.file

interface File {
    val contentId: FileContentId
    val filename: Filename
    val mimeType: MimeType
}