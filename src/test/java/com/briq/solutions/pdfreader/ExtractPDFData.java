package com.briq.solutions.pdfreader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;

public class ExtractPDFData {
    public static void main(String[] args) throws IOException {
        String dir = System.getProperty("user.dir");
        String filePath = dir+"\\test_document\\bids for transport.pdf";
        ExtractProjectName epn = new ExtractProjectName(filePath);
        String projectName = epn.getProjectName();
        PDDocument pdDocument = ExtractProjectName.pdDocument;
        String data = new PDFTextStripper().getText(pdDocument);
        System.out.println(projectName);

        System.out.println(data);


    }
}
