package com.app.batch.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

public class FileUploadUtil {

    public static String saveUploadedFile(MultipartFile file, String uploadDir) throws IOException {
        // Create the upload directory if it doesn't exist
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Save the file to the upload directory
        String filePath = uploadDir + File.separator + file.getOriginalFilename();
        File dest = new File(filePath);
        file.transferTo(dest);

        return filePath;
    }
}