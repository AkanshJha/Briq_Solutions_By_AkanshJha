package com.briq.solutions.utilities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;

public class ExcelUtilities {

    private XSSFWorkbook wb = null;

    /**
     *
     * @return returns Sheet Object of the Workbook.
     */
    public XSSFSheet getSheet() {
        return sheet;
    }

    private XSSFSheet sheet = null;
    private FileOutputStream fos = null;

    /**
     * Default constructor, which initializes the Workbook and creates a new Sheet Object.
     */
    public ExcelUtilities() {
        this.wb = new XSSFWorkbook();
        this.sheet = wb.createSheet();
    }

    /**
     * Constructor with Excel file Path. It creates a workbook object and get the sheet at index 0.
     * @param readExcelPath: Excel File path which you want to read.
     */
    public ExcelUtilities(String readExcelPath)  {
        try {
            this.wb = new XSSFWorkbook(new FileInputStream(new File(readExcelPath)));
            this.sheet = wb.getSheetAt(0);
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
            System.out.println("File not found at the specified place. Please refer stack trace for more details.");
        }
        catch(IOException e){
            e.printStackTrace();

        }
        catch (Exception e){
            System.out.println("Some unwanted exception occurred. Please refer stack trace for more details.");
            e.printStackTrace();

        }
    }

    /**
     * Method to write given rowm with given ArrayList of String data.
     * @param rowNumber - Row number where you want to write data to.
     * @param data - Array of String data to be written in the row.
     */
    public void writeExcelData(int rowNumber, ArrayList<String> data) {
        int cellNumber = 0;
        XSSFRow row = sheet.createRow(rowNumber);
        XSSFCell cell = null;
        for (String cellData : data) {
            //System.out.println("==========Writing '" + cellData + "' in " + rowNumber + " rowNumber and " + cellNumber + " column.");
            cell = row.createCell(cellNumber++);
            cell.setCellValue(cellData);
            //sheet.createRow(rowNumber).createCell(cellNumber++).setCellValue(cellData);
        }
    }

    /**
     * Overloaded method of previous writing method.
     * It does the same on the given row and cell number.
     * @param rowNumber: row number to be written.
     * @param columnNumber: cell to be written.
     * @param data: Object data to be written.
     */
    public void writeExcelData(int rowNumber, int columnNumber, Object data) {
        XSSFRow row = sheet.createRow(rowNumber);
        XSSFCell cell = row.createCell(columnNumber);
        cell.setCellValue(String.valueOf(data));
  /*      if (data instanceof String)
            cell.setCellValue(data.toString());
        else if (data instanceof Boolean)
            cell.setCellValue(Boolean.parseBoolean(data.toString()));
        else if (data instanceof Integer)
            cell.setCellValue(Integer.parseInt(data.toString()));*/

        //System.out.println("Object Type is ====>> "+data.getClass());
        System.out.println("########### Writing '"+data+"' in "+rowNumber+" row and "+columnNumber+" cell/column.");
    }

    /**
     *
     * @param filePath: Path of the Excel File which you want to retrieve last row from.
     * @return: Last written row number
     * @throws IOException: If not file found, error occurs.
     */
    public static int getLastRowNumber(String filePath) throws IOException {
        XSSFWorkbook wb = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            wb = new XSSFWorkbook(fis);
        }
        catch(FileNotFoundException e){
            System.out.println("File does not exist. Returning last row number as 0.");
            return 0;
        }
        return wb.getSheetAt(0).getLastRowNum();
    }

    /**
     *
     * @param filePath: Path of the Excel File which you want to retrieve last cell from.
     * @return: Last written cell number in zero-th row
     * @throws IOException: If not file found, error occurs.
     */
    public static int getLastCellNumber(String filePath) throws IOException{
        XSSFWorkbook wb = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            wb = new XSSFWorkbook(fis);
        }
        catch(FileNotFoundException e){
            System.out.println("File does not exist. Returning last cell number as 0.");
            return 0;
        }
        return wb.getSheetAt(0).getRow(0).getLastCellNum();
    }

    /**
     * This message writes down the whole Workbook object in the given Excel file.
     * @param outExcel: Path to the Excel file.
     */
    public void flushExcel(String outExcel) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(outExcel));
            wb.write(fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getRowData(int rowNumber){
        XSSFRow row = this.sheet.getRow(rowNumber);
        int lastCellNumber = row.getLastCellNum();
        DataFormatter formatter = new DataFormatter();
        Cell cell = null;
        ArrayList<String> rowData = new ArrayList<String>();

        for(int i = 0; i < lastCellNumber; i++){
            cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            //rowData.add(cell.getStringCellValue());
            rowData.add(formatter.formatCellValue(cell));
        }

        return rowData;
    }
}
