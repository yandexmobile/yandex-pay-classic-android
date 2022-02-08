package com.yandex.pay.core.cardbinding

import android.content.res.Resources
import android.util.Base64
import androidx.annotation.RawRes
import androidx.annotation.WorkerThread
import com.yandex.xplat.common.XPromise
import com.yandex.xplat.common.YSError
import com.yandex.xplat.common.promise
import com.yandex.xplat.yandex.pay.CardDataCipher
import com.yandex.xplat.yandex.pay.CardDataCipherResult
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

internal class DefaultCardDataCipher(
    @RawRes private val keyId: Int,
    private val resources: Resources
) : CardDataCipher {
    internal class CardDataCipherException(message: String, inner: Throwable) :
        YSError(message, inner)

    override fun encrypt(data: String): XPromise<CardDataCipherResult> {
        return promise { resolve, reject ->
            try {
                resolve(encrypt(data.toByteArray()))
            } catch (e: Throwable) {
                reject(CardDataCipherException(e.message ?: e.javaClass.name, e))
            }
        }
    }

    @WorkerThread
    private fun encrypt(input: ByteArray): CardDataCipherResult {
        val keySpec = X509EncodedKeySpec(resources.openRawResource(keyId).readBytes())
        val keyFactory = KeyFactory.getInstance("RSA")
        val key = keyFactory.generatePublic(keySpec) as RSAPublicKey
        val cipherInfo = CipherInfo.from(input.size, key)

        val cipher = Cipher.getInstance(cipherInfo.algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedData = cipher.doFinal(input)
        return CardDataCipherResult(
            Base64.encodeToString(encryptedData, Base64.NO_WRAP),
            cipherInfo.hashAlgorithm,
        )
    }

    private enum class CipherInfo(
        val algorithm: String,
        val hashAlgorithm: String,
        val digestLength: Int,
    ) {
        RSA_SHA512("RSA/NONE/OAEPwithSHA-512andMGF1Padding", "SHA512", 128),
        RSA_SHA256("RSA/NONE/OAEPwithSHA-256andMGF1Padding", "SHA256", 64);

        fun getMaxMessageLength(key: RSAPublicKey): Int =
            key.modulus.bitLength() / 8 - digestLength - 2

        companion object {
            fun from(inputSize: Int, key: RSAPublicKey): CipherInfo =
                if (inputSize > RSA_SHA512.getMaxMessageLength(key)) RSA_SHA256 else RSA_SHA512
        }
    }
}
