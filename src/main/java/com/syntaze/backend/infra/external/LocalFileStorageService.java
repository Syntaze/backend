package com.syntaze.backend.infra.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {

    private final Path uploadsDir;

    public LocalFileStorageService(@Value("${app.uploads.dir:uploads}") String uploadsDir) throws IOException {
        this.uploadsDir = Path.of(uploadsDir);
        Files.createDirectories(this.uploadsDir);
    }

    @Override
    public String store(MultipartFile file) throws IOException {
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID() + ext;
        Path target = uploadsDir.resolve(filename);
        try (var in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        // return relative path (can be served via static resources)
        return target.toString().replace("\\", "/");
    }
}

