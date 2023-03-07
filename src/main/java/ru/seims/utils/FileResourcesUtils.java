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
import java.util.ArrayList;
import java.util.stream.Stream;

public class FileResourcesUtils {
    public static String RESOURCE_PATH = "";
    public static String UPLOAD_PATH = "";
    private static ClassLoader classLoader;

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

    public static ArrayList<String> getResourcesNames(String path) {
        ArrayList<String> filenames = new ArrayList<>();
        try {
            InputStream in = getFileAsStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        } catch (Exception e) {
            Logger.log(FileResourcesUtils.class, e.getMessage(), 2);
            e.printStackTrace();
        }
        return filenames;
    }

    public static String getFileDataAsString(String filePath) throws IOException, IllegalArgumentException {
        Logger.log(FileResourcesUtils.class, "Loading resource at: " + filePath, 1);
        //if (filePath.isEmpty())
            //throw new IllegalArgumentException("Path is empty");
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getFileAsStream(filePath)));
            Stream<String> lines = bufferedReader.lines();
            lines.forEach(stringBuilder::append);
            Logger.log(FileResourcesUtils.class, "Resource " + filePath + " loaded", 1);
            bufferedReader.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to read the resource : " + filePath + '\n' + e.getMessage());
        }
    }

    public static InputStream getFileAsStream(String filePath) {
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

    public static File transferMultipartFile(MultipartFile multipartFile, String outPath, String fileName) throws IOException {
        File file = new File(outPath);
        if(!file.exists())
            file.mkdir();
        file = new File(outPath + "/" + fileName);
        if(file.isFile() && file.exists())
            file.delete();
        if (!file.createNewFile())
            throw new IOException("Cannot transfer file to backend");
        multipartFile.transferTo(file);
        Logger.log(FileResourcesUtils.class, "Transferred file: " + file.getAbsolutePath(), 1);
        return file;
    }
}