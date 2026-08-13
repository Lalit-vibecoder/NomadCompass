package com.example.nomadcompass.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.nomadcompass.domain.model.AttachmentType
import com.example.nomadcompass.domain.model.TripAttachment

@Entity(
    tableName = "trip_attachments",
    indices = [Index(value = ["tripId"])]
)
data class TripAttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Int,
    val type: String, // "PDF", "IMAGE", "NOTE"
    val title: String,
    val filePath: String? = null,
    val content: String? = null,
    val fileSize: Long = 0L,
    val mimeType: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

fun TripAttachmentEntity.toDomain(): TripAttachment = TripAttachment(
    id = id,
    tripId = tripId,
    type = when (type.uppercase()) {
        "PDF" -> AttachmentType.PDF
        "IMAGE" -> AttachmentType.IMAGE
        else -> AttachmentType.NOTE
    },
    title = title,
    filePath = filePath,
    content = content,
    fileSize = fileSize,
    mimeType = mimeType,
    createdAt = createdAt,
)

fun TripAttachment.toEntity(): TripAttachmentEntity = TripAttachmentEntity(
    id = id,
    tripId = tripId,
    type = type.name,
    title = title,
    filePath = filePath,
    content = content,
    fileSize = fileSize,
    mimeType = mimeType,
    createdAt = createdAt,
)
