package com.example.nomadcompass.util.extractor

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.InputStream

/**
 * 100% On-Device local file ingestion and text extraction coordinator.
 * Supports digital PDFs, HTML files, images/screenshots (via ML Kit OCR), and unformatted text.
 */
object OfflineDocumentExtractor {

    private var isPdfBoxInitialized = false

    private fun ensurePdfBox(context: Context) {
        if (!isPdfBoxInitialized) {
            try {
                PDFBoxResourceLoader.init(context.applicationContext)
                isPdfBoxInitialized = true
            } catch (_: Exception) {
                // Ignore if already initialized
            }
        }
    }

    /**
     * Extracts text streams from digital PDF documents on-device using PDFBox-Android.
     */
    suspend fun extractFromPdf(context: Context, uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            ensurePdfBox(context)
            context.contentResolver.openInputStream(uri)?.use { inputStream: InputStream ->
                val document = PDDocument.load(inputStream)
                try {
                    val stripper = PDFTextStripper().apply {
                        sortByPosition = true
                    }
                    val extractedText = stripper.getText(document)
                    if (extractedText.isNotBlank()) {
                        Result.success(extractedText.trim())
                    } else {
                        Result.failure(IllegalStateException("No readable digital text found in PDF. If it's a scanned document, try ingesting it as an image/screenshot."))
                    }
                } finally {
                    document.close()
                }
            } ?: Result.failure(IllegalArgumentException("Unable to open PDF file stream from URI."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Extracts text nodes and structured travel blocks from HTML files on-device using Jsoup.
     */
    suspend fun extractFromHtml(context: Context, uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream: InputStream ->
                val doc = Jsoup.parse(inputStream, "UTF-8", "")
                // Remove scripts, styles, and hidden meta elements
                doc.select("script, style, meta, link, noscript").remove()
                val extractedText = doc.body()?.text() ?: doc.text()
                if (extractedText.isNotBlank()) {
                    Result.success(extractedText.trim())
                } else {
                    Result.failure(IllegalStateException("No readable text found in HTML file."))
                }
            } ?: Result.failure(IllegalArgumentException("Unable to open HTML file stream."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Extracts text from screenshots, photos, and scanned booking receipts on-device using Google ML Kit OCR.
     */
    suspend fun extractFromImage(context: Context, uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val inputImage = InputImage.fromFilePath(context, uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val visionText = recognizer.process(inputImage).awaitResult()
            val text = visionText.text
            if (text.isNotBlank()) {
                Result.success(text.trim())
            } else {
                Result.failure(IllegalStateException("No text detected in screenshot. Please ensure the image is clear and well-lit."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Ingests unformatted raw copied text or pasted emails.
     */
    fun extractFromRawText(rawText: String): Result<String> {
        val trimmed = rawText.trim()
        return if (trimmed.isNotBlank()) {
            Result.success(trimmed)
        } else {
            Result.failure(IllegalArgumentException("Pasted text is empty."))
        }
    }
}
