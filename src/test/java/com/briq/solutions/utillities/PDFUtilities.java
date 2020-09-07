package com.briq.solutions.utillities;

import technology.tabula.Table;
import technology.tabula.TextChunk;

import java.util.ArrayList;
import java.util.List;

public class PDFUtilities {

    public ArrayList<String> getPDFTableHeaders(Table table) {
        ArrayList<String> outputArray = new ArrayList<String>();
        for (int cellNumber = 0; cellNumber < table.getColCount(); cellNumber++) {
            outputArray.add(listToString(table.getCell(0, cellNumber).getTextElements()));
        }
        //System.out.println(outputArray);

        return outputArray;

    }

    public String listToString(List list) {
        String output = "";
        for (Object listElement : list) {
            output = output + " " + ((TextChunk) listElement).getText();
        }
        return output;
    }
}
