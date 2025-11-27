package com.itsci.mju.maebanjumpen.other.controller

import com.itsci.mju.maebanjumpen.hire.repository.HireRepository
import com.itsci.mju.maebanjumpen.hire.service.HireService
import com.itsci.mju.maebanjumpen.other.service.StorageService
import com.itsci.mju.maebanjumpen.partyrole.repository.HousekeeperRepository
import com.itsci.mju.maebanjumpen.person.repository.PersonRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import java.net.MalformedURLException
import java.nio.file.Files

@RestController
@RequestMapping("/maeban/files")
class FileUploadController(
    private val storageService: StorageService,
    private val personRepository: PersonRepository,
    private val housekeeperRepository: HousekeeperRepository,
    private val hireRepository: HireRepository,
    private val hireService: HireService
) {
    @Value("\${app.public-base-url}")
    private lateinit var publicBaseUrl: String

    // Helper method เพื่อสร้าง URL เต็มสำหรับไฟล์ที่มี subfolder
    private fun buildFullFileUrl(folderName: String, subfolderName: String, filename: String): String {
        val baseUrl = if (publicBaseUrl.endsWith("/")) publicBaseUrl else "$publicBaseUrl/"
        return "${baseUrl}maeban/files/download/$folderName/$subfolderName/$filename"
    }

    // Helper Method สำหรับแยกชื่อไฟล์ออกจาก URL เต็ม
    private fun extractFilenameFromUrl(fileUrl: String?): String? {
        if (fileUrl.isNullOrEmpty()) return null
        val lastSlashIndex = fileUrl.lastIndexOf('/')
        return if (lastSlashIndex != -1 && lastSlashIndex < fileUrl.length - 1) {
            fileUrl.substring(lastSlashIndex + 1)
        } else {
            fileUrl
        }
    }

    /**
     * อัปโหลดรูปภาพโปรไฟล์สำหรับ Person
     */
    @PostMapping("/upload/person/profile-picture/{personId}")
    fun uploadPersonProfilePicture(
        @PathVariable personId: Int,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, String>> {
        val response = mutableMapOf<String, String>()
        return try {
            val optionalPerson = personRepository.findById(personId)
            if (optionalPerson.isEmpty) {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Person with ID $personId not found.")
            }
            val person = optionalPerson.get()

            if (!person.pictureUrl.isNullOrEmpty()) {
                val oldFileName = extractFilenameFromUrl(person.pictureUrl)
                if (oldFileName != null) {
                    storageService.deleteFile(oldFileName, "profile_pictures", "no-subfolder")
                }
            }

            val storedFileName = storageService.uploadFile(file, "profile_pictures", "no-subfolder")
            val fullFileUrl = buildFullFileUrl("profile_pictures", "no-subfolder", storedFileName)
            person.pictureUrl = fullFileUrl
            personRepository.save(person)

            response["message"] = "Profile picture uploaded successfully"
            response["pictureUrl"] = fullFileUrl
            ResponseEntity.ok(response)
        } catch (e: ResponseStatusException) {
            throw e
        } catch (e: Exception) {
            response["error"] = "Could not upload file: ${e.message}"
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        }
    }

    /**
     * อัปโหลดรูปภาพยืนยันตัวตนสำหรับ Housekeeper
     */
    @PostMapping("/upload/housekeeper/verify-photo/{housekeeperId}")
    fun uploadHousekeeperVerifyPhoto(
        @PathVariable housekeeperId: Int,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, String>> {
        val response = mutableMapOf<String, String>()
        return try {
            val optionalHousekeeper = housekeeperRepository.findById(housekeeperId)
            if (optionalHousekeeper.isEmpty) {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Housekeeper with ID $housekeeperId not found.")
            }
            val housekeeper = optionalHousekeeper.get()

            if (!housekeeper.photoVerifyUrl.isNullOrEmpty()) {
                val oldFileName = extractFilenameFromUrl(housekeeper.photoVerifyUrl)
                if (oldFileName != null) {
                    storageService.deleteFile(oldFileName, "verify_photos", "no-subfolder")
                }
            }

            val storedFileName = storageService.uploadFile(file, "verify_photos", "no-subfolder")
            val fullFileUrl = buildFullFileUrl("verify_photos", "no-subfolder", storedFileName)
            housekeeper.photoVerifyUrl = fullFileUrl
            housekeeperRepository.save(housekeeper)

            response["message"] = "Verification photo uploaded successfully"
            response["photoVerifyUrl"] = fullFileUrl
            ResponseEntity.ok(response)
        } catch (e: ResponseStatusException) {
            throw e
        } catch (e: Exception) {
            response["error"] = "Could not upload file: ${e.message}"
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        }
    }

    /**
     * อัปโหลดรูปภาพความคืบหน้าของงาน (Hire) หลายรูป
     */
    @PostMapping("/upload/hire/progression-images/{hireId}")
    fun uploadHireProgressionImages(
        @PathVariable hireId: Int,
        @RequestParam("files") files: Array<MultipartFile>
    ): ResponseEntity<Map<String, Any>> {
        val response = mutableMapOf<String, Any>()
        return try {
            val uploadedUrls = mutableListOf<String>()
            val folderName = "progression_images"
            val subfolderName = hireId.toString()

            val optionalHire = hireRepository.findById(hireId)
            if (optionalHire.isEmpty) {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Hire with ID $hireId not found.")
            }

            for (file in files) {
                val storedFileName = storageService.uploadFile(file, folderName, subfolderName)
                val fullFileUrl = buildFullFileUrl(folderName, subfolderName, storedFileName)
                uploadedUrls.add(fullFileUrl)
            }

            hireService.addProgressionImagesToHire(hireId, uploadedUrls)

            response["message"] = "Progression images uploaded successfully"
            response["progressionImageUrls"] = uploadedUrls
            ResponseEntity.ok(response)
        } catch (e: ResponseStatusException) {
            throw e
        } catch (e: Exception) {
            response["error"] = "Could not upload files: ${e.message}"
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        }
    }

    /**
     * Endpoint สำหรับดาวน์โหลดไฟล์ (รวมทั้งแบบมีและไม่มี subfolder)
     */
    @GetMapping(
        "/download/{folderName}/{filename:.+}",
        "/download/{folderName}/{subfolderName}/{filename:.+}"
    )
    fun downloadFile(
        @PathVariable folderName: String,
        @PathVariable(required = false) subfolderName: String?,
        @PathVariable filename: String
    ): ResponseEntity<Resource> {
        return try {
            val filePath = if (subfolderName != null) {
                storageService.loadFile(filename, folderName, subfolderName)
            } else {
                storageService.loadFile(filename, folderName)
            }
            val resource: Resource = UrlResource(filePath.toUri())

            if (resource.exists() || resource.isReadable) {
                var contentType = Files.probeContentType(filePath)
                if (contentType == null) {
                    contentType = "application/octet-stream"
                }
                ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"${resource.filename}\"")
                    .body(resource)
            } else {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: $filename")
            }
        } catch (e: MalformedURLException) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating URL for file: ${e.message}")
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not read file: ${e.message}")
        }
    }

    /**
     * Endpoint สำหรับลบไฟล์
     */
    @DeleteMapping("/delete/{folderName}/{subfolderName}/{filename:.+}")
    fun deleteFile(
        @PathVariable folderName: String,
        @PathVariable subfolderName: String,
        @PathVariable filename: String
    ): ResponseEntity<Map<String, String>> {
        val response = mutableMapOf<String, String>()
        return try {
            val deleted = storageService.deleteFile(filename, folderName, subfolderName)
            if (deleted) {
                response["message"] = "File '$filename' deleted successfully from $subfolderName."
                ResponseEntity.ok(response)
            } else {
                response["error"] = "File '$filename' not found or could not be deleted from $subfolderName."
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
            }
        } catch (e: Exception) {
            response["error"] = "Error deleting file: ${e.message}"
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        }
    }
}

