package com.briq.solutions.exceltojson;

import com.briq.solutions.utillities.ExcelUtilities;
import com.briq.solutions.utillities.ReadPropertiesFile;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class ExcelToJSON {

    private final String currentDir = System.getProperty("user.dir");
    private final String propFilePath = currentDir + "\\configurations\\Briq_Test.properties";
    private final Properties prop = ReadPropertiesFile.loadPropertiesFile(propFilePath);
    private final String readExcelPath = prop.getProperty("excel_to_json");
    private final String jsonOutput = prop.getProperty("json_output_path");

    /**
     * This test is being developed in order to extract the given data from the Excel and convert it to the JSON File with proper format.
     * Here, the Input Excel file and Output JSON file path is bring provided from the Properties file available in configuration folder.
     *
     */
    @Test
    void excelToJSON() throws IOException {
        ExcelUtilities readExcel = new ExcelUtilities(readExcelPath);
        int lastRowIndex = ExcelUtilities.getLastRowNumber(readExcelPath);
        //int lastCellNumber = ExcelUtilities.getLastCellNumber(readExcelPath);

        final ArrayList<String> jsonObjectKeys = readExcel.getRowData(0);
        ArrayList<String> individualRowData = null;
        System.out.println("Total Headers in Excel File - " + jsonObjectKeys.size());
        JSONObject jsonObject = null;
        JSONArray jsonArray = new JSONArray();

        for (int i = 1; i <= lastRowIndex; i++) {
            individualRowData = readExcel.getRowData(i);
            jsonObject = new JSONObject();
            int itr = 0;
            for (String value : individualRowData) {
                //System.out.println(jsonObjectKeys.get(i-1)+" === "+value);
                jsonObject.put(jsonObjectKeys.get(itr++),value);
            }
            //System.out.println("Fetched Project Name from the JSON Object - "+jsonObject.get("projectName"));
            jsonArray.add(jsonObject);
            jsonObject = null;
            individualRowData = null;
        }

        FileWriter fw = new FileWriter(jsonOutput);
        fw.write(jsonArray.toJSONString());
        fw.flush();
        fw.close();

    }
}

