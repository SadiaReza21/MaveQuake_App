package com.mavequake.util

import android.content.Context
import android.net.Uri
import java.io.File

/** Retrofit/OkHttp need a real File to build a multipart part - content:// Uris aren't enough. */
fun copyUriToCacheFile(context: Context, uri: Uri, fileName: String): File {
    val outFile = File(context.cacheDir, fileName)
    context.contentResolver.openInputStream(uri)?.use { input ->
        outFile.outputStream().use { output -> input.copyTo(output) }
    }
    return outFile
}
