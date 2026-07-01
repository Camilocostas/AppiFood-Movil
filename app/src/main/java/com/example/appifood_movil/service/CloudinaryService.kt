package com.example.appifood_movil.service

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.Cloudinary
import com.example.appifood_movil.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object CloudinaryService {

    private const val TAG = "CloudinaryService"

    private val cloudinary: Cloudinary by lazy {
        Cloudinary("cloudinary://${BuildConfig.CLOUDINARY_API_KEY}:${BuildConfig.CLOUDINARY_API_SECRET}@${BuildConfig.CLOUDINARY_CLOUD_NAME}")
    }

    /**
     * Sube una imagen a Cloudinary
     * @param context Context de la aplicación (para acceder al ContentResolver)
     * @param imageUri Uri de la imagen
     * @param folder Carpeta en Cloudinary
     * @param publicId Nombre del archivo
     */
    suspend fun uploadImage(
        context: Context,
        imageUri: Uri,
        folder: String,
        publicId: String
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "📤 Subiendo imagen a Cloudinary...")
                Log.d(TAG, "📁 Carpeta: $folder")
                Log.d(TAG, "📄 Public ID: $publicId")

                val file = uriToFile(context, imageUri) ?: return@withContext null

                val result = cloudinary.uploader().upload(
                    file,
                    mapOf(
                        "folder" to folder,
                        "public_id" to publicId,
                        "use_filename" to true,
                        "unique_filename" to false
                    )
                )

                val url = result["secure_url"] as? String
                Log.d(TAG, "✅ Imagen subida: $url")
                url
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error subiendo imagen: ${e.message}", e)
                null
            }
        }
    }

    /**
     * Elimina una imagen de Cloudinary
     */
    suspend fun deleteImage(publicId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🗑️ Eliminando imagen: $publicId")
                val result = cloudinary.uploader().destroy(publicId, mapOf<String, Any>())
                val success = result["result"] == "ok"
                Log.d(TAG, if (success) "✅ Imagen eliminada" else "❌ No se pudo eliminar")
                success
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error eliminando imagen: ${e.message}", e)
                false
            }
        }
    }

    /**
     * Convierte un Uri a File temporal (recibe Context)
     */
    private suspend fun uriToFile(context: Context, uri: Uri): File? {
        return withContext(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri) ?: return@withContext null

                val tempFile = File.createTempFile("cloudinary_", ".jpg")
                tempFile.deleteOnExit()

                FileOutputStream(tempFile).use { output ->
                    inputStream.copyTo(output)
                }

                tempFile
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error convirtiendo Uri a File: ${e.message}", e)
                null
            }
        }
    }
}