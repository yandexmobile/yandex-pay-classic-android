// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM file-system/file-system-path.ts >>>

package com.yandex.xplat.common


public interface FileSystemPath {
    val separator: String
    fun isAbsolute(p: String): Boolean
    fun normalize(p: String): String
    fun join(paths: YSArray<String>): String
    fun dirname(p: String): String
    fun basename(p: String): String
    fun extname(p: String): String
    fun name(p: String): String
    fun parse(p: String): ParsedPath
}

public open class ParsedPath(val dir: String, val base: String, val ext: String?, val name: String) {
}
