package com.turitsynanton.android.carchoosing

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import kotlin.math.roundToInt

//      Функция для именьшения размера изображения для отображения в приложении
fun getScaledBitmap(path: String, destWidth: Int, destHeight: Int): Bitmap {
//    Чтение размера изображения
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, options)
    val srcWidth = options.outWidth.toFloat()
    val srcHeight = options.outHeight.toFloat()
//    Уменьшение масштаба
    val sampleSize = if (srcHeight <= destHeight && srcWidth <= destWidth) {
        1
    } else {
        val heightScale = srcHeight / destHeight
        val widthScale = srcWidth / destWidth
        minOf(heightScale, widthScale).roundToInt()
    }

    val scaledOptions = BitmapFactory.Options()
    scaledOptions.inSampleSize = sampleSize

//      Чтение изображения с учетом масштаба, но без переворота
    val bitmap = BitmapFactory.decodeFile(path, scaledOptions)
    val exif = ExifInterface(path)
    val orientation =
        exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
    }
//    Создание подходящего изображения
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}