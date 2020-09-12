package com.briq.solutions.pdfreader;

import com.briq.solutions.utillities.ExcelUtilities;
import com.briq.solutions.utillities.PDFUtilities;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.testng.annotations.Test;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BidsOfTransportPDFTableExtractor {
    String currentDir = System.getProperty("user.dir");
    String filePath = currentDir + "\\test_document\\bids for transport.pdf";
    String excelOutputFilePath = currentDir + "\\results\\BidsForTransportTable.xlsx";
    PDFUtilities pu = new PDFUtilities();
    ExcelUtilities eu = new ExcelUtilities();

    @Test
    void extractPDFTableData() throws IOException {
        PDDocument pd = PDDocument.load(new File(filePath));

        ObjectExtractor oe = new ObjectExtractor(pd);
        SpreadsheetExtractionAlgorithm algo = new SpreadsheetExtractionAlgorithm();
        String projectName = new ExtractProjectName(filePath).getProjectName();
        int pageNumber = pd.getNumberOfPages();
        int rowToWrite = 1;
        Page pg = null;
        List<Table> tables = null;
        //System.out.println("Tables are extracted");
        RectangularTextContainer tableCell = null;
        List textElements = null;
        ArrayList<String> headers = null;
        ArrayList<String> dataToWriteInExcel = null;

        for (int itr = 1; itr <= pageNumber; itr++) {

            pg = oe.extract(itr);
            tables = algo.extract(pg);

            for (Table tb : tables) {

                if (itr == 1) {//Just to write headers in excel file
                    headers = pu.getPDFTableHeaders(tb);
                    headers.add("Project Name");
                    eu.writeExcelData(0, headers);
                }

                for (int row = 1; row < tb.getRowCount(); row++) {
                    dataToWriteInExcel = new ArrayList<String>();
                    for (int cell = 0; cell < tb.getColCount(); cell++) {
                        tableCell = tb.getCell(row, cell);
                        textElements = tableCell.getTextElements();
                        dataToWriteInExcel.add(pu.listToString(textElements));

                    }
                    dataToWriteInExcel.add(projectName);
                    eu.writeExcelData(rowToWrite++, dataToWriteInExcel);
                    dataToWriteInExcel = null;
                    textElements = null;
                }
                //break;
            }
            eu.flushExcel(excelOutputFilePath);
        }
    }

}
