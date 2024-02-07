package ru.whitebeef.meridianbot.utils;

import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFolderExtractor {

    public static void extractFolderFromJar(JarFile jarFile, String folderPath, String destinationDirectory) throws IOException {
        File destDir = new File(destinationDirectory);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        for (JarEntry entry : Collections.list(jarFile.entries())) {
            if (entry.getName().startsWith(folderPath) && !entry.isDirectory()) {
                String fileName = entry.getName().substring(folderPath.length());
                File outFile = new File(destDir, fileName);

                try (InputStream inputStream = jarFile.getInputStream(entry);
                     FileOutputStream outputStream = new FileOutputStream(outFile)) {
                    FileCopyUtils.copy(inputStream, outputStream);
                }
            }
        }
    }
}