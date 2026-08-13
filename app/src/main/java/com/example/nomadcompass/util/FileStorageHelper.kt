package com.example.nomadcompass.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object FileStorageHelper {

    fun saveUriToInternalStorage(context: Context, uri: Uri, targetFolder: String = "trip_attachments"): SavedFileInfo? {
        return try {
            val contentResolver = context.contentResolver
            var fileName = "file_${System.currentTimeMillis()}"

            // Determine file name from content resolver
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex) ?: fileName
                    }
                }
            }

            val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"
            val dir = File(context.filesDir, targetFolder)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            // Generate unique local file to prevent overwriting
            val destinationFile = File(dir, "${System.currentTimeMillis()}_$fileName")
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            SavedFileInfo(
                title = fileName,
                filePath = destinationFile.absolutePath,
                fileSize = destinationFile.length(),
                mimeType = mimeType
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun openFile(context: Context, filePath: String, mimeType: String = "*/*") {
        try {
            val file = File(filePath)
            if (!file.exists()) return

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType.ifBlank { "*/*" })
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Open with"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun formatFileSize(sizeBytes: Long): String {
        if (sizeBytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(sizeBytes.toDouble()) / Math.log10(1024.0)).toInt()
        val index = digitGroups.coerceIn(0, units.size - 1)
        val value = sizeBytes / Math.pow(1024.0, index.toDouble())
        return String.format(Locale.US, "%.1f %s", value, units[index])
    }
}

data class SavedFileInfo(
    val title: String,
    val filePath: String,
    val fileSize: Long,
    val mimeType: String,
)
