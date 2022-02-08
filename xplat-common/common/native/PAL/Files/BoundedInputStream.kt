package com.yandex.xplat.common

import java.io.InputStream

private const val EOF = -1

/**
 * This is a stream that will only supply bytes up to a certain length - if its
 * position goes above that, it will stop.
 */
class BoundedInputStream(
    /** The wrapped input stream */
    private val inner: InputStream,
    /** The maximum number of bytes to return. Infinite by default. */
    private val max: Long = Long.MAX_VALUE
) : InputStream() {
    /** The number of bytes already returned */
    private var pos: Long = 0
    /** The marked position */
    private var mark: Long = EOF.toLong()
    /** If the stream was configured with an upper bound */
    private val isBound: Boolean
        get() = max != Long.MAX_VALUE
    /** If the stream reached its upper bound */
    private val reachedUpperBound: Boolean
        get() = max in 0..pos

    override fun read(): Int {
        if (reachedUpperBound) {
            return EOF
        }
        val result = inner.read()
        pos++
        return result
    }

    override fun read(b: ByteArray): Int = this.read(b, 0, b.size)

    override fun read(b: ByteArray, offset: Int, length: Int): Int {
        if (reachedUpperBound) {
            return EOF
        }
        val maxRead: Long = if (isBound) Math.min(length.toLong(), max - pos) else length.toLong()
        val bytesRead = inner.read(b, offset, maxRead.toInt())

        if (bytesRead == EOF) {
            return EOF
        }

        pos += bytesRead.toLong()
        return bytesRead
    }

    override fun skip(n: Long): Long {
        val toSkip = if (isBound) Math.min(n, max - pos) else n
        val skippedBytes = inner.skip(toSkip)
        pos += skippedBytes
        return skippedBytes
    }

    override fun available(): Int = if (reachedUpperBound) 0 else inner.available()

    override fun toString() = inner.toString()

    override fun close() = inner.close()

    @Synchronized
    override fun reset() {
        inner.reset()
        pos = mark
    }

    @Synchronized
    override fun mark(readlimit: Int) {
        inner.mark(readlimit)
        mark = pos
    }

    override fun markSupported() = inner.markSupported()
}
