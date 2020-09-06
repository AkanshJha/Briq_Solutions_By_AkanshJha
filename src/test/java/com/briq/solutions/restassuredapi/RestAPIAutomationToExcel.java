package com.briq.solutions.restassuredapi;

import com.briq.solutions.utillities.ExcelUtilities;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;

public class RestAPIAutomationToExcel {

    private final String currDir = System.getProperty("user.dir");
    private final String jsonResponseFile = currDir + "\\json_output\\API_Response.json"; //Response JSON File

    /**
     * 1. This test runs the given RestAssured EndPoint-
     * https://data.sfgov.org/resource/p4e4-a5a7.json
     * 2. Stores the API response in a JSON file.
     * 3. Reads this JSON file and JSON Objects.
     * 4. Writes the Objects data in the Excel File.
     * If the Excel file already exists, It start writing the data from EOF.
     *
     * NOTE: It is assumed that all the Objects in the JSON has either equal or less attributes/keys than the very first JSONObject in the Array.
     *       If any JSONObject has more attributes than first JSONObject, these attributes will not be fetched and added to the Excel File.
     *       If any JSONObject has less attributes that the first JSONObject, the value for the not found keys is added as 'Key does not exist for this JSONObject.' in the excel file for that JSONObject.
     *
     * If there is any exception, handled or unhandled in catch block, the Excel file will get ready till the execution is successful.
     *
     * @throws IOException: Thrown if error occured related to file operations.
     */
    @Test
    void endPointResultJsonToExcel() throws IOException {
        String excelOutputFile = currDir + "\\results\\RestAssuredResponseOutput.xlsx";
        ExcelUtilities eu = new ExcelUtilities(); // ExcelUtils object to write data in Excel File
        JSONParser parser = new JSONParser(); // Object to parse the JSON File
        Object obj = null; // To parse the JSON file and storing it in Object obj.
        JSONArray jsonArray = null;
        JSONObject jsonObject = null; //holds attributes each object of JSONArray
        JSONObject locationObj = null; //holds attributes of location Object
        Object valueToBeWritten = ""; //It holds the value of attribute of JSONObject object

        Set<JSONObject> jsonKeys = null; // Fetching the Keys of the JSONObject of JSONArray.
        Set<JSONObject> locationKeys = null; // This object stores the Keys/attributes of location object's attributes.
        ArrayList<String> headers = null; //converting Set for jsonKeys to ArrayList of jsonKeys(Strings) be written in Excel File as Header.
        ArrayList<String> dataToWriteInExcel = null; //Object to hold list of data to written in Excel

        int lastRowNumber = 0; //to hold value of last row
        int lastCellNumber = 0; //to hold value of last cell of first Row
        int indexOfLocation = 0; //To store the index of location object in headers ArrayList.
        int iteration = 1; //To keep count of Iteration during execution

        //Calling the method which hits the EndPoint to get the JSON Response and Write it in JSON File.
        runEndPoint();

        try {
            obj = parser.parse(new FileReader(jsonResponseFile)); //Parsing the JSON file and storing it in Object obj.
            jsonArray = (JSONArray) obj;

            jsonKeys = ((JSONObject) jsonArray.get(1)).keySet();
            headers = new ArrayList<String>();
            dataToWriteInExcel = null;

            lastRowNumber = 0;
            lastCellNumber = 0;
            indexOfLocation = 0;
            iteration = 1;

            System.out.println("JSONArray Size: " + jsonArray.size());

            //Converting jsonKeys to ArrayList, so that it can be written in the excel in Row one as Headers.
            for (Object jsonKey : jsonKeys) {
                headers.add(jsonKey.toString());
            }

            indexOfLocation = headers.indexOf("location");
            System.out.println("index of location object in Array List : " + indexOfLocation);

            lastRowNumber = ExcelUtilities.getLastRowNumber(excelOutputFile);
            lastCellNumber = ExcelUtilities.getLastCellNumber(excelOutputFile);
            System.out.println("Latest Row Number: " + lastRowNumber);
            System.out.println("Latest Cell Number in Row 1: " + lastCellNumber);

            for (Object eachArray : jsonArray) { //Iterating JSONArray Object to Object object
                System.out.println("----------------------------------------------Iterating through JSONObject: "+iteration+"----------------------------------------------");
                jsonObject = (JSONObject) eachArray; // Parsing this Object object to JSONObject object
                dataToWriteInExcel = new ArrayList<String>();
                for (Object jsonKey : jsonKeys) { //For this current JSONObject, Iterating through its all attributes.
                    valueToBeWritten = jsonObject.get(jsonKey.toString()); //Getting value of Key.
                    try {
                        dataToWriteInExcel.add(valueToBeWritten.toString()); // Adding it to ArrayListObject
                        System.out.println("Value of " + jsonKey.toString() + " : " + valueToBeWritten.toString());
                    }
                    catch(NullPointerException e){
                        System.out.println("NullPointerException Occurred because the requested key for this object does not exist.");
                        System.out.println("adding this value as 'Key does not exist for this JSONObject' for this key.");
                        dataToWriteInExcel.add("Key does not exist for this JSONObject."); // Adding it to ArrayListObject
                    }
                    //eu.writeExcelData(lastRowNumber + 1, headers.indexOf(jsonKey.toString()), valueToBeWritten);  //This block of code will be writing each cell individually in a row
                    if (jsonKey.equals("location")) { //Looking for the attribute 'location' to iterate through location object's attributes.
                        if (iteration == 1) { //To fetch the details of the objects of the location object, and this block will run, only for once during whole execution.
                            locationObj = (JSONObject) jsonObject.get(jsonKey.toString()); //initializing locationObj to iterate through location object's attributes.
                            locationKeys = locationObj.keySet(); //Gettings Keys/attributes of all location Objects
                            System.out.println("Location Keys : " + locationKeys);
                            //Adding the location attributes to the headers object, so that these all attributes are added in Excel in Row 1 as Headers.
                            for (Object locationKey : locationKeys) {
                                headers.add(++indexOfLocation, locationKey.toString());
                            }
                            eu.writeExcelData(0, headers); // Writing these headers to the Excel file.
                            // NOTE: This block of code will not be executing for another time during current execution.
                        }

                        // Iterating through the locationKey to fetch value of keys.
                        for (Object locationKey : locationKeys) {
                            valueToBeWritten = locationObj.get(locationKey.toString());
                            dataToWriteInExcel.add(valueToBeWritten.toString());
                            System.out.println("Value of " + locationKey.toString() + " : " + valueToBeWritten.toString());
                            //eu.writeExcelData(lastRowNumber + 1, headers.indexOf(locationKey.toString()), valueToBeWritten); //This block of code will be writing each cell individually in a row
                        }
                    }
                }
                //Once all the values added in dataToWriteInExcel object, for a Single JSONObject, we write this Object in the row.
                eu.writeExcelData(lastRowNumber+1, dataToWriteInExcel);
                dataToWriteInExcel = null;
                lastRowNumber++;
                iteration++;
                //break; //Added this to Test it for only 1 JSONObject
            }

            eu.flushExcel(excelOutputFile);

        } catch (IOException e) {
            e.printStackTrace();
            eu.flushExcel(excelOutputFile);
        } catch (ParseException e) {
            e.printStackTrace();
            eu.flushExcel(excelOutputFile);
        }
        catch(Exception e){
            System.out.println("Some unwanted exception occurred. Please check stack trace.");
            e.printStackTrace();
            eu.flushExcel(excelOutputFile);
        }


    }

    void runEndPoint() throws IOException {
        ExcelUtilities eu = new ExcelUtilities();
        RestAssured.baseURI = "https://data.sfgov.org/resource/p4e4-a5a7.json";
        //Request Object
        RequestSpecification httpReq = RestAssured.given();
        //Response Object
        Response response = httpReq.request(Method.GET, "");

        FileWriter file = new FileWriter(jsonResponseFile);
        file.write(response.getBody().asString());
        file.close();
        System.out.println("Response file has been updated");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Status Line: " + response.getStatusLine());

    }
}
