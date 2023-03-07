package ru.seims.database.entitiy;

import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.properties.PropertyReader;
import ru.seims.utils.properties.PropertyType;

import java.io.*;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class StoredImage {
    public static final int MAX_IMG_LENGTH = 3145728;
    public static final String DEFAULT_IMAGE_NAME = PropertyReader.getPropertyValue(PropertyType.SERVER, "app.emptyImageFileName");
    private String name = null;
    private String extension = null;
    private String base64Data = null;

    public static StoredImage loadDefaultImage() {
        try {
            return new StoredImage(new File("/img/" + DEFAULT_IMAGE_NAME));
        } catch (Exception e) {
            return null;
        }
    }

    public StoredImage() throws IOException {
        this("", "", "");
    }

    public StoredImage(String name, String extension, String base64Data) throws IOException {
        this.name = name;
        this.extension = extension;
        this.base64Data = base64Data;
    }

    public StoredImage(int id) throws SQLException, IOException{
        this(String.valueOf(id));
    }

    public StoredImage(String imgId) throws SQLException, IOException {
        ResultSet resultSet = SQLExecutor.getInstance().executeSelect("select * from images where id like '" + imgId + "'");
        if(resultSet.next()) {
            name = resultSet.getString("name");
            extension = resultSet.getString("type");
            Blob img_data = resultSet.getBlob("img_data");
            resultSet.close();
            InputStream inputStream = img_data.getBinaryStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[MAX_IMG_LENGTH];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] imgBytes = outputStream.toByteArray();
            inputStream.close();
            outputStream.close();
            base64Data = Base64.getEncoder().encodeToString(imgBytes);
        } else {
            if(imgId.equals(DEFAULT_IMAGE_NAME))
                throw new SQLException("Image placeholder cannot be read");
            StoredImage image = loadDefaultImage();
            name = image.getName();
            extension = image.getExtension();
            base64Data = image.getBase64Data();
        }
    }

    public StoredImage(File file) throws IOException {
            InputStream inputStream = FileResourcesUtils.getFileAsStream(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[MAX_IMG_LENGTH];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] imgBytes = outputStream.toByteArray();
            inputStream.close();
            outputStream.close();
            base64Data = Base64.getEncoder().encodeToString(imgBytes);
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public String getBase64Data() {
        return base64Data;
    }
}
