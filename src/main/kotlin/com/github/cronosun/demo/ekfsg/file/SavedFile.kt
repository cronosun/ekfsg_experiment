package com.github.cronosun.demo.ekfsg.file

import com.github.cronosun.demo.ekfsg.user.UserId
import java.time.Instant

/**
 * NOTE: Was zu sehen ist: Wir haben hier `SavedFile` und `NewFile`, man kann also direkt
 * am Code sehen, wenn es sich um eine gespeicherte oder um eine neue Datei handelt.
 * Wir sehen auch direkt, welche methode welche Art von File entgegen nimmt. Wir müssen
 * nun auch nicht mehr irgendwelche Abfragen wie (if id==null then insert else update)
 * machen.
 */
data class SavedFile(
    val id: FileId,
    val createdAt: Instant,
    val createdBy: UserId,
    override val contentId: FileContentId,
    override val filename: Filename,
    override val mimeType: MimeType,
) : File