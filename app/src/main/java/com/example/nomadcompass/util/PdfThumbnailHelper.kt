package com.example.nomadcompass.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object PdfThumbnailHelper {

    suspend fun getPdfThumbnail(filePath: String?, targetWidth: Int = 300, targetHeight: Int = 400): Bitmap? {
        if (filePath.isNullOrBlank()) return null
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filePath)
                if (!file.exists() || !file.canRead()) return@withContext null
                val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY) ?: return@withContext null
                val renderer = PdfRenderer(pfd)
                if (renderer.pageCount > 0) {
                    val page = renderer.openPage(0)
                    val width = if (page.width > 0) targetWidth else 300
                    val height = if (page.height > 0 && page.width > 0) {
                        (targetWidth.toFloat() * page.height / page.width).toInt().coerceAtLeast(100)
                    } else targetHeight

                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    canvas.drawColor(Color.WHITE)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                    renderer.close()
                    pfd.close()
                    bitmap
                } else {
                    renderer.close()
                    pfd.close()
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun getPdfPageBitmaps(filePath: String?, maxWidth: Int = 800): List<Bitmap> {
        if (filePath.isNullOrBlank()) return emptyList()
        return withContext(Dispatchers.IO) {
            val bitmaps = mutableListOf<Bitmap>()
            try {
                val file = File(filePath)
                if (!file.exists() || !file.canRead()) return@withContext emptyList()
                val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY) ?: return@withContext emptyList()
                val renderer = PdfRenderer(pfd)
                val count = renderer.pageCount.coerceAtMost(10) // Render up to 10 pages for preview
                for (i in 0 until count) {
                    val page = renderer.openPage(i)
                    val width = maxWidth
                    val height = (maxWidth.toFloat() * page.height / page.width).toInt().coerceAtLeast(200)
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    canvas.drawColor(Color.WHITE)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                    bitmaps.add(bitmap)
                }
                renderer.close()
                pfd.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            bitmaps
        }
    }
}
