package ru.seims.database.entitiy;

import ru.seims.database.proccessing.SQLExecutor;
import ru.seims.utils.logging.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class StoredImage {
    public static final int MAX_IMG_LENGTH = 3145728;
    public static final String defaultImageId = "0";
    private final String name;
    private final String extension;
    private final String base64Data;

    public static StoredImage loadDefaultImage() {
        try {
            return new StoredImage(defaultImageId);
        } catch (Exception e) {
            return null;
        }
    }

    public StoredImage() {
        this("", "", "");
    }

    public StoredImage(String name, String extension, String base64Data) {
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
            if(imgId.equals(defaultImageId))
                throw new SQLException("Image placeholder cannot be read");
            StoredImage image = loadDefaultImage();
            name = image.getName();
            extension = image.getExtension();
            base64Data = image.getBase64Data();
        }
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
