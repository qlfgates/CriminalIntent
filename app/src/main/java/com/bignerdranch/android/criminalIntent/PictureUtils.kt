package com.bignerdranch.android.criminalIntent

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlin.math.roundToInt

fun getScaledBitmap(path:String, desWidth: Int, desHeight: Int): Bitmap{
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        val srcWidth = options.outWidth.toFloat()
        val srcHeight = options.outHeight.toFloat()

        val sampleSize = if(srcHeight <= desHeight && srcWidth <= desWidth){
            1
        } else{
            val heightScale = srcHeight / desHeight
            val widthScale = srcWidth / desWidth

            minOf(heightScale, widthScale).roundToInt()
        }

        return BitmapFactory.decodeFile(path, BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        })
    }
