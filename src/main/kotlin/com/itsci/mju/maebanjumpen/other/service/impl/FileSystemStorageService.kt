package com.itsci.mju.maebanjumpen.other.service.impl

import com.itsci.mju.maebanjumpen.other.service.StorageService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@Service
class FileSystemStorageService : StorageService {

    @Value("\${app.upload.dir:uploads}")
    private lateinit var uploadDir: String

    override fun uploadFile(file: MultipartFile, folderName: String, subfolderName: String): String {
        try {
            val targetLocation = getTargetLocation(folderName, subfolderName)
            Files.createDirectories(targetLocation)

            val originalFilename = file.originalFilename ?: "file"
            val extension = originalFilename.substringAfterLast('.', "")
            val storedFilename = "${UUID.randomUUID()}.$extension"

            val targetPath = targetLocation.resolve(storedFilename)
            Files.copy(file.inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING)

            return storedFilename
        } catch (e: IOException) {
            throw RuntimeException("Could not store file. Please try again!", e)
        }
    }

    override fun loadFile(filename: String, folderName: String, subfolderName: String): Path {
        val targetLocation = getTargetLocation(folderName, subfolderName)
        return targetLocation.resolve(filename).normalize()
    }

    override fun deleteFile(filename: String, folderName: String, subfolderName: String): Boolean {
        return try {
            val filePath = loadFile(filename, folderName, subfolderName)
            Files.deleteIfExists(filePath)
        } catch (e: IOException) {
            false
        }
    }

    private fun getTargetLocation(folderName: String, subfolderName: String): Path {
        return if (subfolderName == "no-subfolder") {
            Paths.get(uploadDir, folderName)
        } else {
            Paths.get(uploadDir, folderName, subfolderName)
        }
    }
}

