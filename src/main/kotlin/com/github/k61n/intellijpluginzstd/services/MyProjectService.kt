package com.github.k61n.intellijpluginzstd.services

import com.github.luben.zstd.ZstdInputStream
import com.github.luben.zstd.ZstdOutputStream
import com.intellij.openapi.components.Service
import kotlin.io.path.Path
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream
import kotlin.io.path.name
import kotlin.io.path.isRegularFile
import kotlin.io.path.absolutePathString
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists

@Service(Service.Level.PROJECT)
class ZstdService() {

    /**
     * Compress [inputAbsolutePath] into "input.zst".
     * @param level zstd compression level (1–9 typical; default 3)
     * @return absolute path to created .zst file
     */
    fun compressFile(inputAbsolutePath: String, level: Int = 3): String {
        val input = Path(inputAbsolutePath)
        require(input.isRegularFile()) { "Input must be a regular file: $inputAbsolutePath" }

        val output = input.parent.resolve("${input.name}.zst")
        output.deleteIfExists()
        output.createFile()

        input.inputStream().use { inStream ->
            output.outputStream().use { rawOut ->
                ZstdOutputStream(rawOut).setLevel(level).use { zOut ->
                    inStream.copyTo(zOut)
                }
            }
        }
        return output.absolutePathString()
    }

    /**
     * Decompress .zst at [inputAbsolutePath] into file without .zst suffix
     * (or ".out" appended if no .zst suffix).
     * @return absolute path to decompressed file
     */
    fun decompressFile(inputAbsolutePath: String): String {
        val input = Path(inputAbsolutePath)
        require(input.isRegularFile()) { "Input must be a regular file: $inputAbsolutePath" }

        val baseName = input.name.removeSuffix(".zst")
        val output = input.parent.resolve(
            if (baseName != input.name) baseName else "${baseName}.out"
        )
        output.deleteIfExists()
        output.createFile()

        input.inputStream().use { rawIn ->
            ZstdInputStream(rawIn).use { zIn ->
                output.outputStream().use { outStream ->
                    zIn.copyTo(outStream)
                }
            }
        }
        return output.absolutePathString()
    }
}