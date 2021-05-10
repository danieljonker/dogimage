package com.blockchain.codechallenge.imageLoader

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import io.reactivex.Scheduler
import io.reactivex.Single
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class ImageLoader(private val ioScheduler: Scheduler, private val uiScheduler: Scheduler) {

    @SuppressLint("CheckResult")
    fun loadImage(imageView: ImageView, url: String) {
        Single.create<Bitmap> { emitter ->
            try {
                val bitmap = getBitMapFromUrl(url)
                bitmap?.let {
                    emitter.onSuccess(it)
                } ?: emitter.onError(IllegalArgumentException())

            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
            .subscribe({
                postExecute(it, imageView)
            }, {

            })
    }

    private fun getBitMapFromUrl(imageuri: String): Bitmap? {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(imageuri)
            //  Log.d("bucky","bitmap" + imageuri);
            connection = url.openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val inputStream: InputStream = connection.getInputStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            connection?.disconnect()
        }
    }

    private fun postExecute(result: Bitmap, imageview: ImageView) {
        imageview.setImageBitmap(result)
    }
}

