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

public class ExcelReader {
    public Workbook workbook;
    public int currentSheet = 1;
    public int sheetCount = 1;
    public int startRowIndex = 19;
    public int startCellIndex = 15;
    public String name = "";
    public String extension = "";
    public Vector<List<CellBase>> read(Workbook workbook) {
        if(currentSheet > sheetCount)
            throw new IllegalArgumentException("Sheet index out of bounds");
        Vector<List<CellBase>> cellVectorHolder = new Vector<>();
        Sheet sheet = workbook.getSheetAt(currentSheet);
        int rowLength = sheet.getRow(startRowIndex + 1).getLastCellNum();
        Iterator<Row> rowIter = sheet.rowIterator();
        for (int i = 0; i < startRowIndex - 1; i++)
            rowIter.next();
        //Row row = rowIter.next();
        boolean nullChecked = false;
        while (rowIter.hasNext()) {
            Row row = rowIter.next();
            //In case of non-square table format
            if(row.getLastCellNum() < rowLength) {
                continue;
            }
            if(!nullChecked && row.getCell(rowLength - 1).toString().isEmpty()) {
                rowLength--;
                nullChecked = true;
            }
            List<CellBase> list = new ArrayList<>();
            for (int cellCounter = startCellIndex; cellCounter < rowLength; cellCounter++) {
                Cell cell;
                if (row.getCell(cellCounter) == null) {
                    cell = row.createCell(cellCounter);
                } else {
                    cell = row.getCell(cellCounter);
                }
                list.add((CellBase) cell);
            }
            cellVectorHolder.addElement(list);
            //row = rowIter.next();
        }
        Logger.log(ExcelReader.class, "Read data from workbook " + name + " values: " + cellVectorHolder.toString(), 4);
        return cellVectorHolder;
    }

    public Vector<List<CellBase>> read() {
        return read(workbook);
    }

    public DataTable readNext() {
            DataTable table = getTable(read(workbook), workbook.getSheetName(currentSheet));
            currentSheet++;
            return table;
    }

    public Workbook load(File file, boolean deleteOnLoad) throws IOException {
        Logger.log(ExcelWriter.class, "Loading Excel workbook", 1);
        FileInputStream myInput = null;
        try {
            myInput = FileResourcesUtils.getFileAsStream(file);
            extension = file.getName().split("\\.")[1];
            name = file.getName();
            if (extension.equals("xlsx"))
                workbook = new XSSFWorkbook(myInput);
            else if (extension.equals("xls"))
                workbook = new HSSFWorkbook(myInput);
            else throw new IOException("Given file is not valid excel file");
            sheetCount = workbook.getNumberOfSheets();
            Logger.log(ExcelWriter.class, "Workbook " + name + " loaded", 1);
            return workbook;
        } catch (Exception e) {
            Logger.log(ExcelReader.class, e.getMessage(), 2);
            throw new FileNotFoundException("Can't load excel file: " + e.getLocalizedMessage());
        } finally {
            if (myInput != null) {
                myInput.close();
            }
            if(deleteOnLoad) {
                if (!file.delete()) {
                    Logger.log(ExcelReader.class, "Cannot delete excel temp file", 2);
                }
            }
        }
        //file.delete();
    }

    public List<List<String>> parse(Vector<List<CellBase>> dataHolder) {
        List<List<String>> tableData = new ArrayList<>(dataHolder.size());
        for (List<CellBase> rowData : dataHolder) {
            List<String> data = new ArrayList<>(rowData.size());
            for(Cell cell : rowData) {
                cell.setCellType(CellType.STRING);
                data.add(cell.getStringCellValue());
            }
            tableData.add(data);
        }
        return tableData;
    }

    public DataTable getTable(Vector<List<CellBase>> dataHolder, String tableName) {
        DataTable table = new DataTable(tableName);
        List<List<String>> parsedData = parse(dataHolder);
        table.populateColumns(parsedData.get(0));
        parsedData.remove(1);
        parsedData.remove(0);
        table.populateRows(parsedData);
        return table;
    }

    public DataTable getTable(String tableName) {
        return getTable(read(), tableName);
    }

    public PreparedStatement prepareStatement(String tableName, DataTable table, String orgId) {
        InsertQueryBuilder queryBuilder = new InsertQueryBuilder(tableName, orgId);
        ArrayList<Map<String, String>> tableData = table.getDataRows();
        for (int i = 0; i < tableData.size(); i++) {
            Map<String, String> rowData = tableData.get(i);
            for(String column : table.getColumnLabels()) {
                queryBuilder.addColumn(rowData.get(column));
            }
            if(i < tableData.size() - 1)
                queryBuilder.addRow();
        }
        queryBuilder.closeRow();
        return queryBuilder.getStatement();
    }

    @Override
    public void finalize() {
        /*File tempFile = new File(FileResourcesUtils.UPLOAD_PATH + "/excelData.tmp");
        if(tempFile.exists())
            tempFile.delete();*/
    }
}
