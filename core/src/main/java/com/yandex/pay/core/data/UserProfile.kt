package com.yandex.pay.core.data

import android.graphics.drawable.Drawable
import android.net.Uri
import com.yandex.xplat.yandex.pay.UserProfileResponse

internal sealed class UserProfile(val name: String, val uid: Uid) {
    class Unresolved(name: String, uid: Uid, val loDpiUrl: Uri?, val hiDpiUrl: Uri?) :
        UserProfile(name, uid) {
        fun toResolved(image: Drawable, placeholder: Boolean) =
            Resolved(name, uid, image, placeholder)
    }

    class Resolved(name: String, uid: Uid, val image: Drawable, private val placeholder: Boolean) :
        UserProfile(name, uid) {
        val imageOrNull: Drawable?
            get() = image.takeUnless { placeholder }
    }

    internal companion object {
        fun from(xplat: UserProfileResponse): Unresolved {
            val uid = Uid(xplat.uid)
            val loDpiUrl = xplat.loDpiUrl?.let(Uri::parse)
            val hiDpiUrl = xplat.hiDpiUrl?.let(Uri::parse)
            return Unresolved(xplat.name, uid, loDpiUrl, hiDpiUrl)
        }
    }
}
