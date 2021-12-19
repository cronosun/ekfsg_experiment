package com.github.cronosun.demo.ekfsg.file

object TestFiles {
    val simpleTextDocument by lazy {
        TestFile(Filename("my text.txt"), MimeType("text/plain"), "Dies ist ein Inhalt".toByteArray())
    }
}