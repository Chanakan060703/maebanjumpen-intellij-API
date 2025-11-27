package com.itsci.mju.maebanjumpen.other.service

import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

interface StorageService {
    fun uploadFile(file: MultipartFile, folderName: String, subfolderName: String = "no-subfolder"): String
    fun loadFile(filename: String, folderName: String, subfolderName: String = "no-subfolder"): Path
    fun deleteFile(filename: String, folderName: String, subfolderName: String = "no-subfolder"): Boolean
}

