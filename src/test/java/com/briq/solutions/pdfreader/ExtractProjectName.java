package com.briq.solutions.pdfreader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;

public class ExtractProjectName {
    private static String currDir = System.getProperty("user.dir");
    private static String projectName = null;
    private static String pdfFilePath = null; //currDir+"\\test_document\\bids for transport.pdf";
    public static PDDocument pdDocument = null;

    public ExtractProjectName(String pdfFilePath){
        this.pdfFilePath = pdfFilePath;

        int index = 0;
        String arr[] = null;
        String data = null;
        try {
            pdDocument = PDDocument.load(new File(pdfFilePath));
            data = new PDFTextStripper().getText(pdDocument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(data); Prints whole data on console.

        arr = data.split("\n");

        for(String val:arr){
            if(val.length()>30)
            {
                //System.out.print("Project Name in given PDF is : ");
                projectName = val;
                //System.out.print(projectName);
            }
            if(index++>3)
                break; //
        }
  }

  public String getProjectName(){
        return projectName;
  }
}
