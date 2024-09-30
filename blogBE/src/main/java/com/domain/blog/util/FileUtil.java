package com.domain.blog.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.domain.blog.constant.FileConstant.baseURI;

public class FileUtil {
    public static void createDirectory(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectory(tmpDir.toPath());
                System.out.println(">>> CREATE NEW DIRECTORY SUCCESSFUL, PATH = " + tmpDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(">>> SKIP MAKING DIRECTORY, ALREADY EXISTS");
        }

    }

    public static String storeFile(MultipartFile file, String folder) throws URISyntaxException, IOException {
        //unique filename
        String fileName = System.currentTimeMillis() + "_" + Objects.requireNonNull(file.getOriginalFilename()).replaceAll("\\s","_");
        URI uri = new URI(baseURI + "/" + folder + "/" + fileName);
        try(InputStream inputStream = file.getInputStream()){
            Files.copy(inputStream, Paths.get(uri));
        }
        return fileName;
    }

    public static void deleteOldFile(String fileName, String folder) {
        try {
            Path filePath = Paths.get(new URI(baseURI + "/" + folder + "/" + fileName));
            Files.deleteIfExists(filePath);
        } catch (URISyntaxException | IOException e) {
            // Log the error
            e.printStackTrace();
        }
    }
}
