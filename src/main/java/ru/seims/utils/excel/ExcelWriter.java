package ru.seims.utils.excel;

import org.apache.poi.ss.usermodel.*;
import ru.seims.database.entitiy.DataTable;
import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.logging.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ExcelWriter {
    public int writeSheet = 1;
    public int startRowIndex = 20;
    public int startCellIndex = 15;
    public Workbook workbook;
    public boolean allowNullElementCreation = false;

    public ExcelWriter(Workbook workbook) {
        this.workbook = workbook;
    }

    public ExcelWriter() {}

    public void write(DataTable table) throws NullPointerException {
        Sheet sheet = workbook.getSheetAt(writeSheet);
        Logger.log(ExcelWriter.class, "Writing to sheet " + writeSheet, 1);
        for(int i = 0; i < table.getDataRows().size(); i++) {
            ArrayList<String> columns = table.getColumnLabels();
            for (int j = 2; j < columns.size(); j++) {
                String value = table.getRow(i).get(columns.get(j));
                Row row = sheet.getRow(startRowIndex + i);
                if(row == null && allowNullElementCreation)
                    row = sheet.createRow(i);
                //Logger.log(ExcelWriter.class, "Saving value: " + value + " to " + (startRowIndex + i) + ":" + (startCellIndex + j - 2), 4);
                try {
                    Cell cell = row.getCell(startCellIndex + j - 2);
                    if (cell == null && allowNullElementCreation)
                        cell = row.createCell(startCellIndex + j - 2);
                    if((cell.getCellType().equals(CellType.NUMERIC) && !Double.isNaN(cell.getNumericCellValue()))
                            || (cell.getCellType().equals(CellType.STRING) && !cell.getStringCellValue().isEmpty()))
                        cell = row.getCell(startCellIndex + j - 1);
                    cell.setCellValue(value);
                } catch (NullPointerException e) {
                    Logger.log(this, "Null cell at "+(startRowIndex + i) + ":" + (startCellIndex + j - 2)+", sheet: "+sheet.getSheetName()+" in excel template. skipping..", 3);
                }
            }
        }
        Logger.log(ExcelWriter.class, "Table " + table.getName() + " written to work book", 1);
    }

    public ByteArrayOutputStream saveBytes(String name) {
        String path = FileResourcesUtils.UPLOAD_PATH + "/" + name;
        Logger.log(ExcelWriter.class, "Writing to workbook " + name + " to " + path, 1);
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            Logger.log(ExcelWriter.class, "Workbook saved", 1);
            return outputStream;
        } catch (IOException e) {
            Logger.log(ExcelWriter.class, "Cannot save workbook " + name + ". " + e.getMessage(), 2);
        }
        return null;
    }

    public String save(String name) {
        String path = FileResourcesUtils.UPLOAD_PATH + "/" + name;
        Logger.log(ExcelWriter.class, "Writing to workbook " + name + " to " + path, 1);
        try {
            FileOutputStream outputStream = new FileOutputStream(path);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            Logger.log(ExcelWriter.class, "Workbook saved", 1);
            return path;
        } catch (IOException e) {
            Logger.log(ExcelWriter.class, "Cannot save workbook " + name + ". " + e.getMessage(), 2);
        }
        return null;
    }
}
