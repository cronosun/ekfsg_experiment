package com.github.cronosun.demo.ekfsg.file

import com.github.cronosun.demo.ekfsg.user.UserId
import com.github.cronosun.demo.ekfsg.user.UserService
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.schema.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.util.*

@Service
class FileStorageService(
    @Autowired var database: Database,
    @Autowired var fileContentService: FileContentService,
    @Autowired var clock: Clock,
    @Autowired var userService: UserService
) {

    fun save(file: NewFile): SavedFile {
        assertExists(file.contentId)

        val id = UUID.randomUUID()
        val createdAt = clock.instant()
        val userId = userService.requireCurrentUser().id
        val entity = FileEntity()
        entity.id = id
        entity.contentId = file.contentId.id
        entity.filename = file.filename.name
        entity.mimeType = file.mimeType.mimeType
        entity.createdAt = createdAt
        entity.userId = userId.id

        database.files.add(entity)

        return SavedFile(FileId(id), createdAt, userId, file.contentId, file.filename, file.mimeType)
    }

    fun listSortedByDateDescending(limit: Int): Iterable<SavedFile> {
        return database.from(FileEntities).select().orderBy(FileEntities.createdAt.desc()).limit(limit)
            .map { row -> FileEntities.createEntity(row) }.map { entityToSavedFile(it) }
    }

    private fun entityToSavedFile(entity: FileEntity): SavedFile {
        val id = FileId(entity.id)
        val createdAt = entity.createdAt
        val createdBy = UserId(entity.userId)
        val contentId = FileContentId(entity.contentId)
        val filename = Filename(entity.filename)
        val mimeType = MimeType(entity.mimeType)
        return SavedFile(id, createdAt, createdBy, contentId, filename, mimeType)
    }

    private fun assertExists(contentId: FileContentId) {
        if (!fileContentService.exists(contentId)) {
            throw RuntimeException("File $contentId does not exist.")
        }
    }

    /**
     * NOTE: Kann private sein: Gut so, sollte nirgends direkt verwendet werden, ausser hier im
     * service. Soll auch nicht in tests verwendet werden (sind interne details).
     */
    private interface FileEntity : Entity<FileEntity> {
        companion object : Entity.Factory<FileEntity>()

        var id: UUID
        var contentId: UUID
        var filename: String
        var mimeType: String
        var createdAt: Instant
        var userId: String
    }

    private object FileEntities : Table<FileEntity>("file") {
        val id = uuid("id").primaryKey().bindTo { it.id }
        val contentId = uuid("content_id").bindTo { it.contentId }
        val filename = varchar("filename").bindTo { it.filename }
        val mimeType = varchar("mime_type").bindTo { it.mimeType }
        val createdAt = timestamp("created_at").bindTo { it.createdAt }
        val userId = varchar("user_id").bindTo { it.userId }
    }

    private val Database.files get() = this.sequenceOf(FileEntities)
}