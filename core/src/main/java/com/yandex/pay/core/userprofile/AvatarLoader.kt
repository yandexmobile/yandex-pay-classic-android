package com.yandex.pay.core.userprofile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.concurrent.ExecutorService

internal class AvatarLoader(private val context: Context, private val executor: ExecutorService) {
    private val fileToManage: File?
        get() {
            val libDir = File(context.cacheDir, "com.yandex.pay.images")
            return if (!libDir.exists() && !libDir.mkdir()) null else File(libDir, "avatar.png")
        }

    fun loadFromFileSystem(): Drawable? {
        val imageName = fileToManage
        if (imageName?.exists() != true) {
            return null
        }
        return Drawable.createFromPath(imageName.absolutePath)
    }

    fun loadFromNetwork(uri: Uri, callback: (Drawable?) -> Unit) {
        executor.execute {
            val result: Bitmap? = try {
                val url = URL(uri.toString())
                BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: Exception) {
                null
            }

            if (result != null) {
                saveImage(result)
                callback(BitmapDrawable(context.resources, result))
            } else {
                callback(null)
            }
        }
    }

    fun drop() {
        try {
            fileToManage?.delete()
        } catch (e: Throwable) {
            // Do nothing
        }
    }

    private fun saveImage(image: Bitmap) {
        try {
            FileOutputStream(fileToManage).use {
                image.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        } catch (e: Exception) {
            // Do nothing.
        }
    }
}
