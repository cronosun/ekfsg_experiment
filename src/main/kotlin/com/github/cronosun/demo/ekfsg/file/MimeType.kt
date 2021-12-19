package com.github.cronosun.demo.ekfsg.file

@JvmInline
value class MimeType(val mimeType: String) {
    companion object {
        val OCTET_STREAM = MimeType("application/octet-stream")

        fun fromStringOrOctetStream(mimeType: String?): MimeType {
            if (mimeType == null || mimeType.isBlank()) {
                return OCTET_STREAM
            } else {
                return MimeType(mimeType)
            }
        }
    }
}