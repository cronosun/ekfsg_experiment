package com.github.cronosun.demo.ekfsg.file

data class NewFile(
    override val contentId: FileContentId,
    override val filename: Filename,
    override val mimeType: MimeType
) : File