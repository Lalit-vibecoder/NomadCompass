package com.example.nomadcompass.ui.theme.icons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * NomadCompass Phosphor Icons System.
 * Clean, modern, geometric travel icons inspired by Phosphor Icons (256x256 coordinate space).
 * Designed to elevate the app's sage, frosted-glass, and claymorphic aesthetic.
 */
object PhosphorIcons {

    private fun phosphorVector(
        name: String,
        block: ImageVector.Builder.() -> Unit
    ): ImageVector {
        return ImageVector.Builder(
            name = "Phosphor.$name",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply(block).build()
    }

    private fun ImageVector.Builder.strokePath(
        strokeWidth: Float = 18f,
        pathData: PathBuilder.() -> Unit
    ) {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = strokeWidth,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.NonZero,
            pathBuilder = pathData
        )
    }

    private fun ImageVector.Builder.filledPath(
        pathData: PathBuilder.() -> Unit
    ) {
        path(
            fill = SolidColor(Color.White),
            pathFillType = PathFillType.NonZero,
            pathBuilder = pathData
        )
    }

    // ── Navigation & System ──

    val Compass: ImageVector = phosphorVector("Compass") {
        strokePath {
            // Outer circular compass rim
            moveTo(128f, 24f)
            arcTo(104f, 104f, 0f, true, true, 24f, 128f)
            arcTo(104f, 104f, 0f, false, true, 128f, 24f)
            close()
            // North-East compass needle pointer
            moveTo(168f, 88f)
            lineTo(136f, 160f)
            lineTo(88f, 168f)
            lineTo(120f, 96f)
            close()
        }
    }

    val GlobeSimple: ImageVector = phosphorVector("GlobeSimple") {
        strokePath {
            moveTo(128f, 24f)
            arcTo(104f, 104f, 0f, true, true, 24f, 128f)
            arcTo(104f, 104f, 0f, false, true, 128f, 24f)
            close()
            // Equator line
            moveTo(24f, 128f)
            lineTo(232f, 128f)
            // Meridian ellipse
            moveTo(128f, 24f)
            curveTo(88f, 60f, 72f, 94f, 72f, 128f)
            curveTo(72f, 162f, 88f, 196f, 128f, 232f)
            moveTo(128f, 24f)
            curveTo(168f, 60f, 184f, 94f, 184f, 128f)
            curveTo(184f, 162f, 168f, 196f, 128f, 232f)
        }
    }

    val CalendarBlank: ImageVector = phosphorVector("CalendarBlank") {
        strokePath {
            // Main calendar rounded card
            moveTo(40f, 80f)
            arcTo(16f, 16f, 0f, false, true, 56f, 64f)
            lineTo(200f, 64f)
            arcTo(16f, 16f, 0f, false, true, 216f, 80f)
            lineTo(216f, 208f)
            arcTo(16f, 16f, 0f, false, true, 200f, 224f)
            lineTo(56f, 224f)
            arcTo(16f, 16f, 0f, false, true, 40f, 208f)
            close()
            // Header dividing horizontal line
            moveTo(40f, 104f)
            lineTo(216f, 104f)
            // Binder pins
            moveTo(88f, 40f)
            lineTo(88f, 64f)
            moveTo(168f, 40f)
            lineTo(168f, 64f)
        }
    }

    val CalendarDots: ImageVector = phosphorVector("CalendarDots") {
        strokePath {
            moveTo(40f, 80f)
            arcTo(16f, 16f, 0f, false, true, 56f, 64f)
            lineTo(200f, 64f)
            arcTo(16f, 16f, 0f, false, true, 216f, 80f)
            lineTo(216f, 208f)
            arcTo(16f, 16f, 0f, false, true, 200f, 224f)
            lineTo(56f, 224f)
            arcTo(16f, 16f, 0f, false, true, 40f, 208f)
            close()
            moveTo(40f, 104f)
            lineTo(216f, 104f)
            moveTo(88f, 40f)
            lineTo(88f, 64f)
            moveTo(168f, 40f)
            lineTo(168f, 64f)
            // Event dots
            moveTo(92f, 144f)
            lineTo(92f, 148f)
            moveTo(128f, 144f)
            lineTo(128f, 148f)
            moveTo(164f, 144f)
            lineTo(164f, 148f)
            moveTo(92f, 180f)
            lineTo(92f, 184f)
            moveTo(128f, 180f)
            lineTo(128f, 184f)
            moveTo(164f, 180f)
            lineTo(164f, 184f)
        }
    }

    val CalendarCheck: ImageVector = phosphorVector("CalendarCheck") {
        strokePath {
            moveTo(40f, 80f)
            arcTo(16f, 16f, 0f, false, true, 56f, 64f)
            lineTo(200f, 64f)
            arcTo(16f, 16f, 0f, false, true, 216f, 80f)
            lineTo(216f, 208f)
            arcTo(16f, 16f, 0f, false, true, 200f, 224f)
            lineTo(56f, 224f)
            arcTo(16f, 16f, 0f, false, true, 40f, 208f)
            close()
            moveTo(40f, 104f)
            lineTo(216f, 104f)
            moveTo(88f, 40f)
            lineTo(88f, 64f)
            moveTo(168f, 40f)
            lineTo(168f, 64f)
            // Checkmark
            moveTo(104f, 164f)
            lineTo(120f, 180f)
            lineTo(156f, 144f)
        }
    }

    val Notebook: ImageVector = phosphorVector("Notebook") {
        strokePath {
            moveTo(56f, 40f)
            lineTo(192f, 40f)
            arcTo(16f, 16f, 0f, false, true, 208f, 56f)
            lineTo(208f, 200f)
            arcTo(16f, 16f, 0f, false, true, 192f, 216f)
            lineTo(56f, 216f)
            close()
            moveTo(88f, 40f)
            lineTo(88f, 216f)
            moveTo(120f, 88f)
            lineTo(168f, 88f)
            moveTo(120f, 128f)
            lineTo(168f, 128f)
        }
    }

    val SuitcaseRolling: ImageVector = phosphorVector("SuitcaseRolling") {
        strokePath {
            // Handle
            moveTo(104f, 56f)
            lineTo(104f, 28f)
            lineTo(152f, 28f)
            lineTo(152f, 56f)
            // Main body
            moveTo(64f, 56f)
            arcTo(16f, 16f, 0f, false, true, 80f, 40f)
            lineTo(176f, 40f)
            arcTo(16f, 16f, 0f, false, true, 192f, 56f)
            lineTo(192f, 200f)
            arcTo(16f, 16f, 0f, false, true, 176f, 216f)
            lineTo(80f, 216f)
            arcTo(16f, 16f, 0f, false, true, 64f, 200f)
            close()
            // Wheels
            moveTo(88f, 216f)
            lineTo(88f, 232f)
            moveTo(168f, 216f)
            lineTo(168f, 232f)
            // Front ridges
            moveTo(96f, 88f)
            lineTo(160f, 88f)
            moveTo(96f, 128f)
            lineTo(160f, 128f)
            moveTo(96f, 168f)
            lineTo(160f, 168f)
        }
    }

    val UserCircle: ImageVector = phosphorVector("UserCircle") {
        strokePath {
            moveTo(128f, 24f)
            arcTo(104f, 104f, 0f, true, true, 24f, 128f)
            arcTo(104f, 104f, 0f, false, true, 128f, 24f)
            close()
            // Head
            moveTo(128f, 84f)
            arcTo(32f, 32f, 0f, true, true, 96f, 116f)
            arcTo(32f, 32f, 0f, false, true, 128f, 84f)
            close()
            // Shoulders
            moveTo(60f, 196f)
            curveTo(76f, 164f, 100f, 156f, 128f, 156f)
            curveTo(156f, 156f, 180f, 164f, 196f, 196f)
        }
    }

    val User: ImageVector = phosphorVector("User") {
        strokePath {
            moveTo(128f, 32f)
            arcTo(40f, 40f, 0f, true, true, 88f, 72f)
            arcTo(40f, 40f, 0f, false, true, 128f, 32f)
            close()
            moveTo(48f, 216f)
            curveTo(48f, 168f, 84f, 144f, 128f, 144f)
            curveTo(172f, 144f, 208f, 168f, 208f, 216f)
        }
    }

    val Plus: ImageVector = phosphorVector("Plus") {
        strokePath(strokeWidth = 20f) {
            moveTo(128f, 48f)
            lineTo(128f, 208f)
            moveTo(48f, 128f)
            lineTo(208f, 128f)
        }
    }

    val PlusCircle: ImageVector = phosphorVector("PlusCircle") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 24f)
            arcTo(104f, 104f, 0f, true, true, 24f, 128f)
            arcTo(104f, 104f, 0f, false, true, 128f, 24f)
            close()
            moveTo(128f, 80f)
            lineTo(128f, 176f)
            moveTo(80f, 128f)
            lineTo(176f, 128f)
        }
    }

    val Check: ImageVector = phosphorVector("Check") {
        strokePath(strokeWidth = 20f) {
            moveTo(48f, 136f)
            lineTo(104f, 192f)
            lineTo(208f, 72f)
        }
    }

    val CheckCircle: ImageVector = phosphorVector("CheckCircle") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 24f)
            arcTo(104f, 104f, 0f, true, true, 24f, 128f)
            arcTo(104f, 104f, 0f, false, true, 128f, 24f)
            close()
            moveTo(88f, 132f)
            lineTo(116f, 160f)
            lineTo(172f, 100f)
        }
    }

    val Circle: ImageVector = phosphorVector("Circle") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 24f)
            arcTo(104f, 104f, 0f, true, true, 24f, 128f)
            arcTo(104f, 104f, 0f, false, true, 128f, 24f)
            close()
        }
    }

    val X: ImageVector = phosphorVector("X") {
        strokePath(strokeWidth = 20f) {
            moveTo(60f, 60f)
            lineTo(196f, 196f)
            moveTo(196f, 60f)
            lineTo(60f, 196f)
        }
    }

    val XCircle: ImageVector = phosphorVector("XCircle") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 24f)
            arcTo(104f, 104f, 0f, true, true, 24f, 128f)
            arcTo(104f, 104f, 0f, false, true, 128f, 24f)
            close()
            moveTo(96f, 96f)
            lineTo(160f, 160f)
            moveTo(160f, 96f)
            lineTo(96f, 160f)
        }
    }

    val Trash: ImageVector = phosphorVector("Trash") {
        strokePath(strokeWidth = 18f) {
            // Lid
            moveTo(40f, 64f)
            lineTo(216f, 64f)
            moveTo(96f, 64f)
            lineTo(96f, 40f)
            arcTo(8f, 8f, 0f, false, true, 104f, 32f)
            lineTo(152f, 32f)
            arcTo(8f, 8f, 0f, false, true, 160f, 40f)
            lineTo(160f, 64f)
            // Can
            moveTo(60f, 64f)
            lineTo(72f, 208f)
            arcTo(16f, 16f, 0f, false, false, 88f, 224f)
            lineTo(168f, 224f)
            arcTo(16f, 16f, 0f, false, false, 184f, 208f)
            lineTo(196f, 64f)
            // Vertical ribs
            moveTo(108f, 104f)
            lineTo(108f, 184f)
            moveTo(148f, 104f)
            lineTo(148f, 184f)
        }
    }

    val PencilSimple: ImageVector = phosphorVector("PencilSimple") {
        strokePath(strokeWidth = 18f) {
            moveTo(64f, 224f)
            lineTo(32f, 224f)
            lineTo(32f, 192f)
            lineTo(156f, 68f)
            lineTo(188f, 100f)
            close()
            moveTo(156f, 68f)
            lineTo(176f, 48f)
            arcTo(16f, 16f, 0f, false, true, 198f, 48f)
            lineTo(208f, 58f)
            arcTo(16f, 16f, 0f, false, true, 208f, 80f)
            lineTo(188f, 100f)
        }
    }

    val PencilLine: ImageVector = phosphorVector("PencilLine") {
        strokePath(strokeWidth = 18f) {
            moveTo(48f, 216f)
            lineTo(216f, 216f)
            moveTo(64f, 176f)
            lineTo(40f, 176f)
            lineTo(40f, 152f)
            lineTo(152f, 40f)
            lineTo(184f, 72f)
            close()
        }
    }

    val MagnifyingGlass: ImageVector = phosphorVector("MagnifyingGlass") {
        strokePath(strokeWidth = 18f) {
            moveTo(112f, 32f)
            arcTo(80f, 80f, 0f, true, true, 32f, 112f)
            arcTo(80f, 80f, 0f, false, true, 112f, 32f)
            close()
            moveTo(168f, 168f)
            lineTo(224f, 224f)
        }
    }

    val SlidersHorizontal: ImageVector = phosphorVector("SlidersHorizontal") {
        strokePath(strokeWidth = 18f) {
            moveTo(32f, 80f)
            lineTo(112f, 80f)
            moveTo(152f, 80f)
            lineTo(224f, 80f)
            moveTo(112f, 56f)
            lineTo(112f, 104f)

            moveTo(32f, 176f)
            lineTo(72f, 176f)
            moveTo(112f, 176f)
            lineTo(224f, 176f)
            moveTo(72f, 152f)
            lineTo(72f, 200f)
        }
    }

    val GearSix: ImageVector = phosphorVector("GearSix") {
        addPath(
            pathData = PathParser().parsePathString(
                "M128,80a48,48,0,1,0,48,48A48.05,48.05,0,0,0,128,80Zm0,80a32,32,0,1,1,32-32A32,32,0,0,1,128,160Zm109.94-52.79a8,8,0,0,0-3.89-5.4l-29.83-17-.12-33.62a8,8,0,0,0-2.83-6.08,111.91,111.91,0,0,0-36.72-20.67,8,8,0,0,0-6.46.59L128,41.85,97.88,25a8,8,0,0,0-6.47-.6A112.1,112.1,0,0,0,54.73,45.15a8,8,0,0,0-2.83,6.07l-.15,33.65-29.83,17a8,8,0,0,0-3.89,5.4,106.47,106.47,0,0,0,0,41.56,8,8,0,0,0,3.89,5.4l29.83,17,.12,33.62a8,8,0,0,0,2.83,6.08,111.91,111.91,0,0,0,36.72,20.67,8,8,0,0,0,6.46-.59L128,214.15,158.12,231a7.91,7.91,0,0,0,3.9,1,8.09,8.09,0,0,0,2.57-.42,112.1,112.1,0,0,0,36.68-20.73,8,8,0,0,0,2.83-6.07l.15-33.65,29.83-17a8,8,0,0,0,3.89-5.4A106.47,106.47,0,0,0,237.94,107.21Zm-15,34.91-28.57,16.25a8,8,0,0,0-3,3c-.58,1-1.19,2.06-1.81,3.06a7.94,7.94,0,0,0-1.22,4.21l-.15,32.25a95.89,95.89,0,0,1-25.37,14.3L134,199.13a8,8,0,0,0-3.91-1h-.19c-1.21,0-2.43,0-3.64,0a8.08,8.08,0,0,0-4.1,1l-28.84,16.1A96,96,0,0,1,67.88,201l-.11-32.2a8,8,0,0,0-1.22-4.22c-.62-1-1.23-2-1.8-3.06a8.09,8.09,0,0,0-3-3.06l-28.6-16.29a90.49,90.49,0,0,1,0-28.26L61.67,97.63a8,8,0,0,0,3-3c.58-1,1.19-2.06,1.81-3.06a7.94,7.94,0,0,0,1.22-4.21l.15-32.25a95.89,95.89,0,0,1,25.37-14.3L122,56.87a8,8,0,0,0,4.1,1c1.21,0,2.43,0,3.64,0a8.08,8.08,0,0,0,4.1-1l28.84-16.1A96,96,0,0,1,188.12,55l.11,32.2a8,8,0,0,0,1.22,4.22c.62,1,1.23,2,1.8,3.06a8.09,8.09,0,0,0,3,3.06l28.6,16.29A90.49,90.49,0,0,1,222.9,142.12Z"
            ).toNodes(),
            fill = SolidColor(Color.White)
        )
    }

    val Gear: ImageVector get() = GearSix

    val Heart: ImageVector = phosphorVector("Heart") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 216f)
            curveTo(80f, 176f, 32f, 136f, 32f, 88f)
            arcTo(48f, 48f, 0f, false, true, 114f, 52f)
            lineTo(128f, 68f)
            lineTo(142f, 52f)
            arcTo(48f, 48f, 0f, false, true, 224f, 88f)
            curveTo(224f, 136f, 176f, 176f, 128f, 216f)
            close()
        }
    }

    val HeartFill: ImageVector = phosphorVector("HeartFill") {
        filledPath {
            moveTo(128f, 216f)
            curveTo(80f, 176f, 32f, 136f, 32f, 88f)
            arcTo(48f, 48f, 0f, false, true, 114f, 52f)
            lineTo(128f, 68f)
            lineTo(142f, 52f)
            arcTo(48f, 48f, 0f, false, true, 224f, 88f)
            curveTo(224f, 136f, 176f, 176f, 128f, 216f)
            close()
        }
    }

    val MapPin: ImageVector = phosphorVector("MapPin") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 216f)
            curveTo(128f, 216f, 56f, 144f, 56f, 96f)
            arcTo(72f, 72f, 0f, true, true, 200f, 96f)
            curveTo(200f, 144f, 128f, 216f, 128f, 216f)
            close()
            moveTo(128f, 72f)
            arcTo(24f, 24f, 0f, true, true, 104f, 96f)
            arcTo(24f, 24f, 0f, false, true, 128f, 72f)
            close()
        }
    }

    val Eye: ImageVector = phosphorVector("Eye") {
        strokePath(strokeWidth = 18f) {
            moveTo(24f, 128f)
            curveTo(48f, 64f, 96f, 48f, 128f, 48f)
            curveTo(160f, 48f, 208f, 64f, 232f, 128f)
            curveTo(208f, 192f, 160f, 208f, 128f, 208f)
            curveTo(96f, 208f, 48f, 192f, 24f, 128f)
            close()
            moveTo(128f, 96f)
            arcTo(32f, 32f, 0f, true, true, 96f, 128f)
            arcTo(32f, 32f, 0f, false, true, 128f, 96f)
            close()
        }
    }

    val CopySimple: ImageVector = phosphorVector("CopySimple") {
        strokePath(strokeWidth = 18f) {
            moveTo(72f, 64f)
            lineTo(184f, 64f)
            arcTo(16f, 16f, 0f, false, true, 200f, 80f)
            lineTo(200f, 208f)
            arcTo(16f, 16f, 0f, false, true, 184f, 224f)
            lineTo(72f, 224f)
            arcTo(16f, 16f, 0f, false, true, 56f, 208f)
            lineTo(56f, 80f)
            arcTo(16f, 16f, 0f, false, true, 72f, 64f)
            close()
            moveTo(72f, 40f)
            lineTo(176f, 40f)
            arcTo(16f, 16f, 0f, false, true, 192f, 56f)
        }
    }

    val CaretUp: ImageVector = phosphorVector("CaretUp") {
        strokePath(strokeWidth = 20f) {
            moveTo(64f, 160f)
            lineTo(128f, 96f)
            lineTo(192f, 160f)
        }
    }

    val CaretDown: ImageVector = phosphorVector("CaretDown") {
        strokePath(strokeWidth = 20f) {
            moveTo(64f, 96f)
            lineTo(128f, 160f)
            lineTo(192f, 96f)
        }
    }

    val CaretRight: ImageVector = phosphorVector("CaretRight") {
        strokePath(strokeWidth = 20f) {
            moveTo(96f, 64f)
            lineTo(160f, 128f)
            lineTo(96f, 192f)
        }
    }

    val ArrowRight: ImageVector = phosphorVector("ArrowRight") {
        strokePath(strokeWidth = 18f) {
            moveTo(48f, 128f)
            lineTo(208f, 128f)
            moveTo(144f, 64f)
            lineTo(208f, 128f)
            lineTo(144f, 192f)
        }
    }

    val ArrowSquareOut: ImageVector = phosphorVector("ArrowSquareOut") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 48f)
            lineTo(64f, 48f)
            arcTo(16f, 16f, 0f, false, false, 48f, 64f)
            lineTo(48f, 192f)
            arcTo(16f, 16f, 0f, false, false, 64f, 208f)
            lineTo(192f, 208f)
            arcTo(16f, 16f, 0f, false, false, 208f, 192f)
            lineTo(208f, 128f)
            moveTo(136f, 120f)
            lineTo(208f, 48f)
            moveTo(152f, 48f)
            lineTo(208f, 48f)
            lineTo(208f, 104f)
        }
    }

    val ArrowsDownUp: ImageVector = phosphorVector("ArrowsDownUp") {
        strokePath(strokeWidth = 18f) {
            // Arrow pointing down
            moveTo(88f, 56f)
            lineTo(88f, 200f)
            moveTo(56f, 168f)
            lineTo(88f, 200f)
            lineTo(120f, 168f)
            // Arrow pointing up
            moveTo(168f, 200f)
            lineTo(168f, 56f)
            moveTo(136f, 88f)
            lineTo(168f, 56f)
            lineTo(200f, 88f)
        }
    }

    val ArrowsLeftRight: ImageVector = phosphorVector("ArrowsLeftRight") {
        strokePath(strokeWidth = 18f) {
            moveTo(56f, 88f)
            lineTo(200f, 88f)
            moveTo(168f, 56f)
            lineTo(200f, 88f)
            lineTo(168f, 120f)

            moveTo(200f, 168f)
            lineTo(56f, 168f)
            moveTo(88f, 136f)
            lineTo(56f, 168f)
            lineTo(88f, 200f)
        }
    }

    val ArrowsClockwise: ImageVector = phosphorVector("ArrowsClockwise") {
        strokePath(strokeWidth = 18f) {
            moveTo(196f, 84f)
            arcTo(80f, 80f, 0f, true, true, 84f, 60f)
            moveTo(196f, 40f)
            lineTo(196f, 84f)
            lineTo(152f, 84f)
        }
    }

    val DotsThreeOutline: ImageVector = phosphorVector("DotsThreeOutline") {
        strokePath(strokeWidth = 18f) {
            moveTo(60f, 128f)
            arcTo(16f, 16f, 0f, true, true, 28f, 128f)
            arcTo(16f, 16f, 0f, false, true, 60f, 128f)
            close()
            moveTo(144f, 128f)
            arcTo(16f, 16f, 0f, true, true, 112f, 128f)
            arcTo(16f, 16f, 0f, false, true, 144f, 128f)
            close()
            moveTo(228f, 128f)
            arcTo(16f, 16f, 0f, true, true, 196f, 128f)
            arcTo(16f, 16f, 0f, false, true, 228f, 128f)
            close()
        }
    }

    val DotsSixVertical: ImageVector = phosphorVector("DotsSixVertical") {
        strokePath(strokeWidth = 18f) {
            moveTo(96f, 72f)
            lineTo(96f, 76f)
            moveTo(96f, 126f)
            lineTo(96f, 130f)
            moveTo(96f, 180f)
            lineTo(96f, 184f)
            moveTo(160f, 72f)
            lineTo(160f, 76f)
            moveTo(160f, 126f)
            lineTo(160f, 130f)
            moveTo(160f, 180f)
            lineTo(160f, 184f)
        }
    }

    val ListBullets: ImageVector = phosphorVector("ListBullets") {
        strokePath(strokeWidth = 18f) {
            moveTo(56f, 80f)
            lineTo(56f, 84f)
            moveTo(56f, 128f)
            lineTo(56f, 132f)
            moveTo(56f, 176f)
            lineTo(56f, 180f)
            moveTo(96f, 80f)
            lineTo(216f, 80f)
            moveTo(96f, 128f)
            lineTo(216f, 128f)
            moveTo(96f, 176f)
            lineTo(216f, 176f)
        }
    }

    // ── Travel & Event Categories ──

    val AirplaneTilt: ImageVector = phosphorVector("AirplaneTilt") {
        strokePath(strokeWidth = 18f) {
            moveTo(136f, 136f)
            lineTo(208f, 72f)
            arcTo(24f, 24f, 0f, false, false, 184f, 48f)
            lineTo(120f, 120f)
            lineTo(64f, 96f)
            lineTo(48f, 112f)
            lineTo(96f, 144f)
            lineTo(80f, 176f)
            lineTo(56f, 176f)
            lineTo(48f, 184f)
            lineTo(72f, 208f)
            lineTo(80f, 200f)
            lineTo(80f, 176f)
            lineTo(112f, 160f)
            lineTo(144f, 208f)
            lineTo(160f, 192f)
            close()
        }
    }

    val Bed: ImageVector = phosphorVector("Bed") {
        strokePath(strokeWidth = 18f) {
            moveTo(24f, 176f)
            lineTo(24f, 88f)
            moveTo(24f, 152f)
            lineTo(232f, 152f)
            lineTo(232f, 184f)
            moveTo(72f, 112f)
            arcTo(20f, 20f, 0f, true, true, 32f, 112f)
            arcTo(20f, 20f, 0f, false, true, 72f, 112f)
            close()
            moveTo(96f, 152f)
            lineTo(96f, 120f)
            arcTo(16f, 16f, 0f, false, true, 112f, 104f)
            lineTo(232f, 104f)
        }
    }

    val Target: ImageVector = phosphorVector("Target") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 32f)
            arcTo(96f, 96f, 0f, true, true, 32f, 128f)
            arcTo(96f, 96f, 0f, false, true, 128f, 32f)
            close()
            moveTo(128f, 72f)
            arcTo(56f, 56f, 0f, true, true, 72f, 128f)
            arcTo(56f, 56f, 0f, false, true, 128f, 72f)
            close()
            moveTo(128f, 112f)
            arcTo(16f, 16f, 0f, true, true, 112f, 128f)
            arcTo(16f, 16f, 0f, false, true, 128f, 112f)
            close()
        }
    }

    val Bus: ImageVector = phosphorVector("Bus") {
        strokePath(strokeWidth = 18f) {
            moveTo(56f, 56f)
            arcTo(24f, 24f, 0f, false, true, 80f, 32f)
            lineTo(176f, 32f)
            arcTo(24f, 24f, 0f, false, true, 200f, 56f)
            lineTo(200f, 184f)
            arcTo(16f, 16f, 0f, false, true, 184f, 200f)
            lineTo(72f, 200f)
            arcTo(16f, 16f, 0f, false, true, 56f, 184f)
            close()
            moveTo(56f, 96f)
            lineTo(200f, 96f)
            moveTo(84f, 156f)
            lineTo(84f, 160f)
            moveTo(172f, 156f)
            lineTo(172f, 160f)
            moveTo(72f, 200f)
            lineTo(72f, 224f)
            moveTo(184f, 200f)
            lineTo(184f, 224f)
        }
    }

    val CarSimple: ImageVector = phosphorVector("CarSimple") {
        strokePath(strokeWidth = 18f) {
            moveTo(56f, 120f)
            lineTo(72f, 64f)
            arcTo(16f, 16f, 0f, false, true, 88f, 52f)
            lineTo(168f, 52f)
            arcTo(16f, 16f, 0f, false, true, 184f, 64f)
            lineTo(200f, 120f)
            arcTo(16f, 16f, 0f, false, true, 216f, 136f)
            lineTo(216f, 192f)
            lineTo(40f, 192f)
            lineTo(40f, 136f)
            close()
            moveTo(72f, 192f)
            lineTo(72f, 216f)
            moveTo(184f, 192f)
            lineTo(184f, 216f)
            moveTo(76f, 156f)
            lineTo(76f, 160f)
            moveTo(180f, 156f)
            lineTo(180f, 160f)
        }
    }

    val ForkKnife: ImageVector = phosphorVector("ForkKnife") {
        strokePath(strokeWidth = 18f) {
            // Fork
            moveTo(60f, 40f)
            lineTo(60f, 84f)
            arcTo(20f, 20f, 0f, false, false, 80f, 104f)
            lineTo(80f, 216f)
            moveTo(80f, 40f)
            lineTo(80f, 84f)
            moveTo(100f, 40f)
            lineTo(100f, 84f)
            arcTo(20f, 20f, 0f, false, true, 80f, 104f)
            // Knife
            moveTo(180f, 40f)
            lineTo(180f, 216f)
            moveTo(180f, 40f)
            arcTo(36f, 36f, 0f, false, false, 144f, 76f)
            lineTo(144f, 124f)
            lineTo(180f, 124f)
        }
    }

    val Briefcase: ImageVector = phosphorVector("Briefcase") {
        strokePath(strokeWidth = 18f) {
            moveTo(40f, 88f)
            arcTo(16f, 16f, 0f, false, true, 56f, 72f)
            lineTo(200f, 72f)
            arcTo(16f, 16f, 0f, false, true, 216f, 88f)
            lineTo(216f, 200f)
            arcTo(16f, 16f, 0f, false, true, 200f, 216f)
            lineTo(56f, 216f)
            arcTo(16f, 16f, 0f, false, true, 40f, 200f)
            close()
            moveTo(96f, 72f)
            lineTo(96f, 48f)
            arcTo(8f, 8f, 0f, false, true, 104f, 40f)
            lineTo(152f, 40f)
            arcTo(8f, 8f, 0f, false, true, 160f, 48f)
            lineTo(160f, 72f)
            moveTo(40f, 136f)
            lineTo(216f, 136f)
        }
    }

    val ShoppingBagOpen: ImageVector = phosphorVector("ShoppingBagOpen") {
        strokePath(strokeWidth = 18f) {
            moveTo(40f, 80f)
            lineTo(56f, 216f)
            lineTo(200f, 216f)
            lineTo(216f, 80f)
            close()
            moveTo(88f, 104f)
            arcTo(40f, 40f, 0f, false, true, 168f, 104f)
        }
    }

    val Tote: ImageVector = phosphorVector("Tote") {
        strokePath(strokeWidth = 18f) {
            moveTo(48f, 88f)
            lineTo(64f, 216f)
            lineTo(192f, 216f)
            lineTo(208f, 88f)
            close()
            moveTo(96f, 88f)
            lineTo(96f, 60f)
            arcTo(32f, 32f, 0f, false, true, 160f, 60f)
            lineTo(160f, 88f)
        }
    }

    // ── Finance & Documents ──

    val CurrencyCircleDollar: ImageVector = phosphorVector("CurrencyCircleDollar") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 24f)
            arcTo(104f, 104f, 0f, true, true, 24f, 128f)
            arcTo(104f, 104f, 0f, false, true, 128f, 24f)
            close()
            moveTo(128f, 72f)
            lineTo(128f, 184f)
            moveTo(152f, 96f)
            curveTo(144f, 84f, 116f, 84f, 112f, 104f)
            curveTo(108f, 120f, 148f, 124f, 144f, 148f)
            curveTo(140f, 168f, 112f, 168f, 104f, 156f)
        }
    }

    val Coins: ImageVector = phosphorVector("Coins") {
        strokePath(strokeWidth = 18f) {
            moveTo(96f, 56f)
            arcTo(48f, 24f, 0f, true, true, 96f, 104f)
            arcTo(48f, 24f, 0f, false, true, 96f, 56f)
            close()
            moveTo(48f, 80f)
            lineTo(48f, 144f)
            arcTo(48f, 24f, 0f, false, false, 144f, 144f)
            lineTo(144f, 80f)
            moveTo(144f, 112f)
            arcTo(48f, 24f, 0f, false, false, 208f, 144f)
            lineTo(208f, 192f)
            arcTo(48f, 24f, 0f, false, true, 112f, 192f)
        }
    }

    val CreditCard: ImageVector = phosphorVector("CreditCard") {
        strokePath(strokeWidth = 18f) {
            moveTo(40f, 68f)
            arcTo(16f, 16f, 0f, false, true, 56f, 52f)
            lineTo(200f, 52f)
            arcTo(16f, 16f, 0f, false, true, 216f, 68f)
            lineTo(216f, 188f)
            arcTo(16f, 16f, 0f, false, true, 200f, 204f)
            lineTo(56f, 204f)
            arcTo(16f, 16f, 0f, false, true, 40f, 188f)
            close()
            moveTo(40f, 96f)
            lineTo(216f, 96f)
            moveTo(72f, 160f)
            lineTo(112f, 160f)
        }
    }

    val Receipt: ImageVector = phosphorVector("Receipt") {
        strokePath(strokeWidth = 18f) {
            moveTo(48f, 40f)
            lineTo(208f, 40f)
            lineTo(188f, 216f)
            lineTo(160f, 204f)
            lineTo(128f, 216f)
            lineTo(96f, 204f)
            lineTo(68f, 216f)
            close()
            moveTo(88f, 88f)
            lineTo(168f, 88f)
            moveTo(88f, 128f)
            lineTo(168f, 128f)
            moveTo(88f, 168f)
            lineTo(136f, 168f)
        }
    }

    val FileText: ImageVector = phosphorVector("FileText") {
        strokePath(strokeWidth = 18f) {
            moveTo(56f, 40f)
            lineTo(152f, 40f)
            lineTo(200f, 88f)
            lineTo(200f, 216f)
            lineTo(56f, 216f)
            close()
            moveTo(152f, 40f)
            lineTo(152f, 88f)
            lineTo(200f, 88f)
            moveTo(88f, 136f)
            lineTo(168f, 136f)
            moveTo(88f, 176f)
            lineTo(144f, 176f)
        }
    }

    val FilePdf: ImageVector = phosphorVector("FilePdf") {
        strokePath(strokeWidth = 18f) {
            moveTo(56f, 40f)
            lineTo(152f, 40f)
            lineTo(200f, 88f)
            lineTo(200f, 216f)
            lineTo(56f, 216f)
            close()
            moveTo(152f, 40f)
            lineTo(152f, 88f)
            lineTo(200f, 88f)
            // PDF glyphs
            moveTo(80f, 140f)
            lineTo(80f, 180f)
            moveTo(80f, 140f)
            arcTo(16f, 16f, 0f, false, true, 104f, 156f)
            lineTo(80f, 156f)
            moveTo(124f, 140f)
            lineTo(124f, 180f)
            moveTo(148f, 140f)
            lineTo(148f, 180f)
            moveTo(148f, 140f)
            lineTo(172f, 140f)
            moveTo(148f, 160f)
            lineTo(164f, 160f)
        }
    }

    val FileImage: ImageVector = phosphorVector("FileImage") {
        strokePath(strokeWidth = 18f) {
            moveTo(56f, 40f)
            lineTo(152f, 40f)
            lineTo(200f, 88f)
            lineTo(200f, 216f)
            lineTo(56f, 216f)
            close()
            moveTo(152f, 40f)
            lineTo(152f, 88f)
            lineTo(200f, 88f)
            moveTo(88f, 184f)
            lineTo(120f, 152f)
            lineTo(168f, 184f)
            moveTo(104f, 128f)
            arcTo(10f, 10f, 0f, true, true, 94f, 138f)
            close()
        }
    }

    val FolderSimpleDashed: ImageVector = phosphorVector("FolderSimpleDashed") {
        strokePath(strokeWidth = 18f) {
            moveTo(40f, 80f)
            lineTo(40f, 64f)
            arcTo(8f, 8f, 0f, false, true, 48f, 56f)
            lineTo(96f, 56f)
            lineTo(120f, 80f)
            lineTo(208f, 80f)
            arcTo(8f, 8f, 0f, false, true, 216f, 88f)
            lineTo(216f, 104f)

            moveTo(216f, 144f)
            lineTo(216f, 192f)
            arcTo(8f, 8f, 0f, false, true, 208f, 200f)
            lineTo(48f, 200f)
            arcTo(8f, 8f, 0f, false, true, 40f, 192f)
            lineTo(40f, 120f)
        }
    }

    val Paperclip: ImageVector = phosphorVector("Paperclip") {
        strokePath(strokeWidth = 18f) {
            moveTo(104f, 120f)
            lineTo(160f, 64f)
            arcTo(28f, 28f, 0f, false, true, 200f, 104f)
            lineTo(128f, 176f)
            arcTo(20f, 20f, 0f, false, true, 100f, 148f)
            lineTo(156f, 92f)
        }
    }

    val Sparkle: ImageVector = phosphorVector("Sparkle") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 24f)
            curveTo(128f, 88f, 168f, 128f, 232f, 128f)
            curveTo(168f, 128f, 128f, 168f, 128f, 232f)
            curveTo(128f, 168f, 88f, 128f, 24f, 128f)
            curveTo(88f, 128f, 128f, 88f, 128f, 24f)
            close()
        }
    }

    // ── Status, Security & Utilities ──

    val WifiSlash: ImageVector = phosphorVector("WifiSlash") {
        strokePath(strokeWidth = 18f) {
            moveTo(48f, 48f)
            lineTo(208f, 208f)
            moveTo(72f, 112f)
            arcTo(96f, 96f, 0f, false, true, 184f, 112f)
            moveTo(104f, 148f)
            arcTo(48f, 48f, 0f, false, true, 152f, 148f)
            moveTo(128f, 192f)
            lineTo(128f, 196f)
        }
    }

    val ShieldCheck: ImageVector = phosphorVector("ShieldCheck") {
        strokePath(strokeWidth = 18f) {
            moveTo(48f, 56f)
            lineTo(128f, 32f)
            lineTo(208f, 56f)
            lineTo(208f, 128f)
            curveTo(208f, 184f, 160f, 216f, 128f, 224f)
            curveTo(96f, 216f, 48f, 184f, 48f, 128f)
            close()
            moveTo(96f, 128f)
            lineTo(120f, 152f)
            lineTo(164f, 104f)
        }
    }

    val SealCheck: ImageVector = phosphorVector("SealCheck") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 24f)
            lineTo(156f, 48f)
            lineTo(192f, 48f)
            lineTo(204f, 84f)
            lineTo(232f, 108f)
            lineTo(224f, 144f)
            lineTo(236f, 180f)
            lineTo(204f, 196f)
            lineTo(192f, 232f)
            lineTo(156f, 232f)
            lineTo(128f, 252f)
            lineTo(100f, 232f)
            lineTo(64f, 232f)
            lineTo(52f, 196f)
            lineTo(20f, 180f)
            lineTo(32f, 144f)
            lineTo(24f, 108f)
            lineTo(52f, 84f)
            lineTo(64f, 48f)
            lineTo(100f, 48f)
            close()
            moveTo(96f, 128f)
            lineTo(120f, 152f)
            lineTo(164f, 104f)
        }
    }

    val SunDim: ImageVector = phosphorVector("SunDim") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 80f)
            arcTo(48f, 48f, 0f, true, true, 80f, 128f)
            arcTo(48f, 48f, 0f, false, true, 128f, 80f)
            close()
            moveTo(128f, 32f)
            lineTo(128f, 48f)
            moveTo(128f, 208f)
            lineTo(128f, 224f)
            moveTo(32f, 128f)
            lineTo(48f, 128f)
            moveTo(208f, 128f)
            lineTo(224f, 128f)
            moveTo(60f, 60f)
            lineTo(72f, 72f)
            moveTo(184f, 184f)
            lineTo(196f, 196f)
            moveTo(60f, 196f)
            lineTo(72f, 184f)
            moveTo(184f, 72f)
            lineTo(196f, 60f)
        }
    }

    val Clock: ImageVector = phosphorVector("Clock") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 24f)
            arcTo(104f, 104f, 0f, true, true, 24f, 128f)
            arcTo(104f, 104f, 0f, false, true, 128f, 24f)
            close()
            moveTo(128f, 72f)
            lineTo(128f, 128f)
            lineTo(168f, 152f)
        }
    }

    val WarningCircle: ImageVector = phosphorVector("WarningCircle") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 24f)
            arcTo(104f, 104f, 0f, true, true, 24f, 128f)
            arcTo(104f, 104f, 0f, false, true, 128f, 24f)
            close()
            moveTo(128f, 80f)
            lineTo(128f, 136f)
            moveTo(128f, 172f)
            lineTo(128f, 176f)
        }
    }

    val Info: ImageVector = phosphorVector("Info") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 24f)
            arcTo(104f, 104f, 0f, true, true, 24f, 128f)
            arcTo(104f, 104f, 0f, false, true, 128f, 24f)
            close()
            moveTo(128f, 80f)
            lineTo(128f, 84f)
            moveTo(128f, 116f)
            lineTo(128f, 176f)
        }
    }

    val Fingerprint: ImageVector = phosphorVector("Fingerprint") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 32f)
            arcTo(64f, 64f, 0f, false, true, 192f, 96f)
            lineTo(192f, 160f)
            arcTo(64f, 64f, 0f, false, true, 128f, 224f)
            moveTo(64f, 160f)
            lineTo(64f, 96f)
            arcTo(64f, 64f, 0f, false, true, 96f, 40f)
            moveTo(96f, 160f)
            lineTo(96f, 104f)
            arcTo(32f, 32f, 0f, false, true, 160f, 104f)
            lineTo(160f, 160f)
            moveTo(128f, 112f)
            lineTo(128f, 156f)
        }
    }

    val LockKey: ImageVector = phosphorVector("LockKey") {
        strokePath(strokeWidth = 18f) {
            moveTo(80f, 104f)
            lineTo(80f, 64f)
            arcTo(48f, 48f, 0f, false, true, 176f, 64f)
            lineTo(176f, 104f)
            moveTo(48f, 104f)
            lineTo(208f, 104f)
            lineTo(208f, 216f)
            lineTo(48f, 216f)
            close()
            moveTo(128f, 148f)
            lineTo(128f, 172f)
        }
    }

    val Backspace: ImageVector = phosphorVector("Backspace") {
        strokePath(strokeWidth = 18f) {
            moveTo(72f, 48f)
            lineTo(24f, 128f)
            lineTo(72f, 208f)
            lineTo(224f, 208f)
            lineTo(224f, 48f)
            close()
            moveTo(120f, 104f)
            lineTo(168f, 152f)
            moveTo(168f, 104f)
            lineTo(120f, 152f)
        }
    }

    val Camera: ImageVector = phosphorVector("Camera") {
        strokePath(strokeWidth = 18f) {
            moveTo(40f, 88f)
            arcTo(16f, 16f, 0f, false, true, 56f, 72f)
            lineTo(80f, 72f)
            lineTo(96f, 48f)
            lineTo(160f, 48f)
            lineTo(176f, 72f)
            lineTo(200f, 72f)
            arcTo(16f, 16f, 0f, false, true, 216f, 88f)
            lineTo(216f, 192f)
            arcTo(16f, 16f, 0f, false, true, 200f, 208f)
            lineTo(56f, 208f)
            arcTo(16f, 16f, 0f, false, true, 40f, 192f)
            close()
            moveTo(128f, 108f)
            arcTo(36f, 36f, 0f, true, true, 92f, 144f)
            arcTo(36f, 36f, 0f, false, true, 128f, 108f)
            close()
        }
    }

    val UploadSimple: ImageVector = phosphorVector("UploadSimple") {
        strokePath(strokeWidth = 18f) {
            moveTo(48f, 168f)
            lineTo(48f, 208f)
            lineTo(208f, 208f)
            lineTo(208f, 168f)
            moveTo(128f, 40f)
            lineTo(128f, 160f)
            moveTo(72f, 96f)
            lineTo(128f, 40f)
            lineTo(184f, 96f)
        }
    }

    val Question: ImageVector = phosphorVector("Question") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 24f)
            arcTo(104f, 104f, 0f, true, true, 24f, 128f)
            arcTo(104f, 104f, 0f, false, true, 128f, 24f)
            close()
            moveTo(128f, 176f)
            lineTo(128f, 180f)
            moveTo(104f, 96f)
            arcTo(24f, 24f, 0f, false, true, 144f, 80f)
            arcTo(24f, 24f, 0f, false, true, 144f, 114f)
            curveTo(132f, 122f, 128f, 130f, 128f, 148f)
        }
    }

    val Lightbulb: ImageVector = phosphorVector("Lightbulb") {
        strokePath(strokeWidth = 18f) {
            moveTo(88f, 168f)
            lineTo(88f, 192f)
            arcTo(8f, 8f, 0f, false, false, 96f, 200f)
            lineTo(160f, 200f)
            arcTo(8f, 8f, 0f, false, false, 168f, 192f)
            lineTo(168f, 168f)
            moveTo(108f, 224f)
            lineTo(148f, 224f)
            moveTo(128f, 32f)
            arcTo(72f, 72f, 0f, false, true, 168f, 168f)
            lineTo(88f, 168f)
            arcTo(72f, 72f, 0f, false, true, 128f, 32f)
            close()
        }
    }

    val EnvelopeSimple: ImageVector = phosphorVector("EnvelopeSimple") {
        strokePath(strokeWidth = 18f) {
            moveTo(40f, 68f)
            arcTo(16f, 16f, 0f, false, true, 56f, 52f)
            lineTo(200f, 52f)
            arcTo(16f, 16f, 0f, false, true, 216f, 68f)
            lineTo(216f, 188f)
            arcTo(16f, 16f, 0f, false, true, 200f, 204f)
            lineTo(56f, 204f)
            arcTo(16f, 16f, 0f, false, true, 40f, 188f)
            close()
            moveTo(44f, 76f)
            lineTo(128f, 136f)
            lineTo(212f, 76f)
        }
    }

    val Bug: ImageVector = phosphorVector("Bug") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 64f)
            arcTo(48f, 48f, 0f, false, true, 176f, 112f)
            lineTo(176f, 160f)
            arcTo(48f, 48f, 0f, false, true, 80f, 160f)
            lineTo(80f, 112f)
            arcTo(48f, 48f, 0f, false, true, 128f, 64f)
            close()
            moveTo(80f, 128f)
            lineTo(176f, 128f)
            moveTo(128f, 64f)
            lineTo(128f, 208f)
            moveTo(40f, 112f)
            lineTo(80f, 112f)
            moveTo(176f, 112f)
            lineTo(216f, 112f)
            moveTo(48f, 160f)
            lineTo(80f, 160f)
            moveTo(176f, 160f)
            lineTo(208f, 160f)
        }
    }

    val ChatTeardropText: ImageVector = phosphorVector("ChatTeardropText") {
        strokePath(strokeWidth = 18f) {
            moveTo(128f, 40f)
            arcTo(88f, 88f, 0f, false, true, 216f, 128f)
            curveTo(216f, 176f, 176f, 216f, 128f, 216f)
            lineTo(48f, 216f)
            lineTo(48f, 136f)
            arcTo(88f, 88f, 0f, false, true, 128f, 40f)
            close()
            moveTo(88f, 112f)
            lineTo(168f, 112f)
            moveTo(88f, 144f)
            lineTo(144f, 144f)
        }
    }

    val SquaresFour: ImageVector = phosphorVector("SquaresFour") {
        strokePath(strokeWidth = 18f) {
            moveTo(56f, 56f)
            lineTo(104f, 56f)
            lineTo(104f, 104f)
            lineTo(56f, 104f)
            close()
            moveTo(152f, 56f)
            lineTo(200f, 56f)
            lineTo(200f, 104f)
            lineTo(152f, 104f)
            close()
            moveTo(56f, 152f)
            lineTo(104f, 152f)
            lineTo(104f, 200f)
            lineTo(56f, 200f)
            close()
            moveTo(152f, 152f)
            lineTo(200f, 152f)
            lineTo(200f, 200f)
            lineTo(152f, 200f)
            close()
        }
    }
}

/**
 * Premium Glassmorphism Icon Badge with 1px Inset Rim.
 * Follows edge refraction rules:
 * - 1px border gradient (rgba(255, 255, 255, 0.20) top fading to rgba(255, 255, 255, 0.05) bottom).
 * - Avoids flat black/dark gray; renders pure white with varied opacities (100% active, 60% inactive) or primary accent.
 * - Minimum stroke thickness for visual pop over busy blurred gradients.
 */
@Composable
fun GlassIconBadge(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    iconSize: Dp = 20.dp,
    shape: Shape = CircleShape,
    containerColor: Color = Color.White.copy(alpha = 0.08f),
    tint: Color = Color.White,
    rimTopAlpha: Float = 0.22f,
    rimBottomAlpha: Float = 0.05f
) {
    val rimBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = rimTopAlpha),
            Color.White.copy(alpha = rimBottomAlpha)
        )
    )

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(containerColor, shape)
            .border(width = 1.dp, brush = rimBrush, shape = shape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}
