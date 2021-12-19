package com.github.cronosun.demo.ekfsg.file

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.schema.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.*

@Service
class FileContentService(@Autowired var database: Database) {

    fun saveContent(): FileOutputStream {
        return FileOutputStreamImpl(this)
    }

    fun writeContentTo(id: FileContentId, destination: OutputStream) {
        val result = database.fileContents.find { it.id eq id.id } ?: throw RuntimeException("Entity not found")
        val content = result.content
        destination.write(content)
    }

    fun exists(id: FileContentId): Boolean {
        return database.fileContents.count { it.id eq id.id } > 0
    }

    internal fun saveContent(byteArray: ByteArray): FileContentId {
        val fileContent = FileContent()
        val id = UUID.randomUUID()
        fileContent.id = id
        fileContent.content = byteArray
        database.fileContents.add(fileContent)
        return FileContentId(id)
    }

    /**
     * NOTE: Muss nicht zwingend public sein: Gut so, sollte nirgends direkt verwendet werden,
     * auch nicht in tests (sind interne details).
     */
    private interface FileContent : Entity<FileContent> {
        companion object : Entity.Factory<FileContent>()

        var id: UUID
        var content: ByteArray
    }

    private object FileContents : Table<FileContent>("file_content") {
        val id = uuid("id").primaryKey().bindTo { it.id }
        val content = blob("content").bindTo { it.content }
    }

    private val Database.fileContents get() = this.sequenceOf(FileContents)
}

/**
 * That's just a simple implementation for this demo. Real implementation would cache the file content on the
 * filesystem or write the file content chunked to DB (this implementation keeps the entire file content in memory).
 */
private class FileOutputStreamImpl(private val service: FileContentService) : FileOutputStream() {

    private val buffer = ByteArrayOutputStream()

    override fun finish(): FileContentId {
        return service.saveContent(buffer.toByteArray())
    }

    override fun write(b: Int) {
        buffer.write(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        buffer.write(b, off, len)
    }
}
