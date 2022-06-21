package com.myhumandesignhd.ui.affirmation.ext

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.myhumandesignhd.BuildConfig
import com.myhumandesignhd.ui.affirmation.AffirmationFragment
import com.myhumandesignhd.util.convertDpToPx
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun AffirmationFragment.getAffirmationBitmap(affirmationView: View): Bitmap {
    affirmationView.isDrawingCacheEnabled = true
    affirmationView.buildDrawingCache()
    val bm: Bitmap = Bitmap.createBitmap(affirmationView.drawingCache)
    val bytes = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.PNG, 100, bytes)

    return bm
}

fun AffirmationFragment.getRoundedRectBitmap(bitmap: Bitmap, pixels: Int): Bitmap {
    val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    val color: Int = -0xbdbdbe
    val paint = Paint()
    val rect = Rect(0, 0, bitmap.width, bitmap.height)
    val rectF = RectF(rect)
    val roundPx: Float = pixels.toFloat()
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, rect, rect, paint)
    return result
}

fun AffirmationFragment.saveAffirmationImg(bitmap: Bitmap): Uri? {
    val imagesFolder = File(requireActivity().cacheDir, "images")
    var uri: Uri? = null
    try {
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "affirmation_${System.currentTimeMillis()}.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 30, stream)
        stream.flush()
        stream.close()
        uri = FileProvider.getUriForFile(requireContext(), BuildConfig.FILE_PROVIDER_PATH, file)
    } catch (e: IOException) {
        Log.d("IOException", "IOException while trying to write file for sharing: " + e.message)
    }
    return uri
}

fun AffirmationFragment.shareAffirmation(affirmationView: View) {


    lifecycleScope.launch(Dispatchers.IO) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, "@hdumandesign #humandesign")
        intent.putExtra(
            Intent.EXTRA_STREAM,
            saveAffirmationImg(
                getRoundedRectBitmap(
                    getAffirmationBitmap(affirmationView), requireContext().convertDpToPx(64f).toInt()
                )
            )
        )
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        intent.type = "image/png"
        requireActivity().startActivityForResult(intent, -999)

    }.invokeOnCompletion {

    }

}