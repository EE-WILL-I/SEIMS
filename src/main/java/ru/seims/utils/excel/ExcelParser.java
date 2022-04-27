package ru.seims.utils.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import ru.seims.utils.FileResourcesUtils;
import ru.seims.utils.logging.Logger;
import ru.seims.database.entitiy.DataTable;
import ru.seims.database.proccessing.InsertQueryBuilder;

import java.io.*;
import java.sql.PreparedStatement;
import java.util.*;

public class ExcelParser {
    public Vector<List<CellBase>> read(File file) throws FileNotFoundException {
        Vector<List<CellBase>> cellVectorHolder = new Vector<>();
        try {
            FileInputStream myInput = FileResourcesUtils.getFileAsStream(file);
            Workbook myWorkBook;
            try {
                myWorkBook = new XSSFWorkbook(myInput);
            } catch (Exception e) {
                try {
                    myInput = FileResourcesUtils.getFileAsStream(file);
                    myWorkBook = new HSSFWorkbook(myInput);
                } catch (Exception e1) {
                    Logger.log(ExcelParser.class, e1.getMessage(), 2);
                    throw new FileNotFoundException("Can't load file: " + e1.getLocalizedMessage());
                }
            } finally {
                //file.delete();
                myInput.close();
            }
            Sheet mySheet = myWorkBook.getSheetAt(0);
            write(myWorkBook);//test
            int maxNumOfCells = mySheet.getRow(0).getLastCellNum();
            Iterator<Row> rowIter = mySheet.rowIterator();
            while(rowIter.hasNext()) {
                Row myRow = rowIter.next();
                List<CellBase> list = new ArrayList<>();
                for (int cellCounter = 0; cellCounter < maxNumOfCells; cellCounter++) {
                    Cell cell;
                    if (myRow.getCell(cellCounter) == null) {
                        cell = myRow.createCell(cellCounter);
                    } else {
                        cell = myRow.getCell(cellCounter);
                    }
                    list.add((CellBase) cell);
                }
                cellVectorHolder.addElement(list);
            }
        } catch (Exception e) {
            Logger.log(ExcelParser.class, e.getMessage(), 2);
            throw new FileNotFoundException("Error during file reading: " + e.getLocalizedMessage());
        } finally {
            file.delete();
        }
        System.out.println(cellVectorHolder.toString());
        return cellVectorHolder;
    }

    public void write(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Persons");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Name");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Age");
        headerCell.setCellStyle(headerStyle);

        try {
            FileOutputStream outputStream = new FileOutputStream(FileResourcesUtils.RESOURCE_PATH + "temp/excelData.xlsx");
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            //lol
        }
    }

    public List<List<String>> parseData(Vector<List<CellBase>> dataHolder) {
        List<List<String>> tableData = new ArrayList<>(dataHolder.size());
        for (List<CellBase> rowData : dataHolder) {
            List<String> data = new ArrayList<>(rowData.size());
            for(Cell cell : rowData)
                data.add(cell.toString());
            tableData.add(data);
        }
        return tableData;
    }

    public DataTable getTable(Vector<List<CellBase>> dataHolder, String tableName) {
        DataTable table = new DataTable(tableName);
        List<List<String>> parsedData = parseData(dataHolder);
        table.populateColumns(parsedData.get(0));
        parsedData.remove(0);
        table.populateRows(parsedData);
        return table;
    }

    public PreparedStatement prepareStatement(String table, String insertSample, Vector<List<XSSFCell>> dataHolder, int argsBias) {
        InsertQueryBuilder queryBuilder = new InsertQueryBuilder(table, insertSample);
        queryBuilder.setArgsBias(argsBias);

        for (List<XSSFCell> rowData : dataHolder) {
            List<String> data = new ArrayList<>();
            for(XSSFCell cell : rowData)
                data.add(cell.toString());
            queryBuilder.addRow(data.toArray(new String[0]));
        }

        return queryBuilder.getStatement();
    }

    @Override
    public void finalize() {
        File tempFile = new File(FileResourcesUtils.RESOURCE_PATH + "temp/excelData.tmp");
        if(tempFile.exists())
            tempFile.delete();
    }
}
