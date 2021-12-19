package com.github.cronosun.demo.ekfsg.file

import java.io.OutputStream

abstract class FileOutputStream : OutputStream() {
    abstract fun finish(): FileContentId
}