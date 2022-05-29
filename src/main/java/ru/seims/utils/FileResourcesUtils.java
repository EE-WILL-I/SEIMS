package ru.seims.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import ru.seims.utils.logging.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileResourcesUtils {
    public static String RESOURCE_PATH = "";
    private static ClassLoader classLoader;
    private static StringBuilder stringBuilder;

    public static void setResourcePath(String path) throws IOException {
        if(path.isEmpty()) throw new IOException("Resource path is empty");
        File resPath = new File(path);
        if(resPath.exists()) {
            RESOURCE_PATH = path;
            File temp = new File(resPath + "/temp");
            if(!temp.exists())
                if(!temp.mkdir())
                    throw new IOException("Cannot create \"temp\" folder");
        } else {
            throw new IOException("Resource path not exists");
        }
    }

    public static String getFileDataAsString(String filePath) throws IOException, IllegalArgumentException {
        Logger.log(FileResourcesUtils.class, "Loading resource at: " + filePath, 4);
        //if (filePath.isEmpty())
            //throw new IllegalArgumentException("Path is empty");

        try {
            PathMatchingResourcePatternResolver scanner = new PathMatchingResourcePatternResolver();
            Resource resource = scanner.getResource(filePath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            Stream<String> lines = bufferedReader.lines();
            lines.forEach(line -> stringBuilder.append(line));
            Logger.log(FileResourcesUtils.class, "Resource loaded", 4);
            bufferedReader.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            throw new IOException("Failed to read the resources folder: " + e.getMessage());
        }
    }

    public static InputStream getFileAsStream(String filePath) throws IOException {
        return FileResourcesUtils.getClassLoader().getResourceAsStream(filePath);
    }

    public static FileInputStream getFileAsStream(File file) throws IOException {
        return new FileInputStream(file);
    }

    public static FileOutputStream getFileOutputStream(File file) throws IOException {
        return new FileOutputStream(file);
    }

    public static ClassLoader getClassLoader() {
        if (classLoader == null)
            classLoader = FileResourcesUtils.class.getClassLoader();
        return classLoader;
    }

    public static File transferMultipartFile(MultipartFile multipartFile, String outPath) throws IOException {
        File file = new File(outPath);
        if (file.exists() && !file.setWritable(true))
            throw new IOException("File is locked");
        if (!file.createNewFile())
            //throw new IOException("Cannot transfer file to backend.");
        Logger.log(FileResourcesUtils.class, "Transferred file: " + file.getAbsolutePath(), 1);
        multipartFile.transferTo(file);
        return file;
    }
}