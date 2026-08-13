package com.example.nomadcompass.domain.model

enum class AttachmentType {
    PDF,
    IMAGE,
    NOTE
}

data class TripAttachment(
    val id: Long = 0,
    val tripId: Int,
    val type: AttachmentType,
    val title: String,
    val filePath: String? = null,
    val content: String? = null,
    val fileSize: Long = 0L,
    val mimeType: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val displayOrder: Int = 0,
)
