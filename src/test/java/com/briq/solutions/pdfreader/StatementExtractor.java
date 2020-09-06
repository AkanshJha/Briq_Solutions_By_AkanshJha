package com.briq.solutions.pdfreader;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

/*  1. Bank Address - Done
  2. Customer Name - Done
  3. Customer Address - Done
  4. Account Number - Done
  5. Statement Date - Done
  6. Ending Balance - Done
  7. Total Withdrawals - Done
  8. Total Deposits - Done
  9. Total Checks - Done
  */
public class StatementExtractor {
    private static String currDir = System.getProperty("user.dir");
    private static String statmentFilePath = currDir + "\\test_document\\sample statement.pdf";

    public static void main(String[] args) throws IOException {
        PDDocument pdDocument = PDDocument.load(new File(statmentFilePath));
        String pts = new PDFTextStripper().getText(pdDocument);
        String strippedData[] = pts.split("\n");
        //System.out.println(pts);

        String bankAddress = strippedData[0] + "\n " + strippedData[1];
        String customerName = strippedData[2];
        String customerAddress = strippedData[3] + "\n " + strippedData[4];
        String accountNumber = null;
        String statementDate = null;
        String endingBalance = null;
        String totalWithdrawl = null;
        String totalDeposits = null;
        String totalCheques = null;

        for (String val : strippedData) {
            if (val.contains("Primary Account Number"))
                accountNumber = val.replace("Primary Account Number: ", "");

            if (val.contains("Statement Date: "))
                statementDate = val.substring(val.length() - 17, val.length()).trim();

            if (val.contains("Ending Balance")) {
                int ind = val.indexOf('$');
                endingBalance = val.substring(ind, val.length());
            }

            if (val.contains("Total ATM Withdrawals")) {
                int ind = val.indexOf('$');
                totalWithdrawl = val.substring(ind, val.length());
            }

            if (val.contains("Total Deposits")) {
                int ind = val.indexOf('$');
                totalDeposits = val.substring(ind, val.length());
            }

            if (val.contains("Total Checks")) {
                int ind = val.indexOf('$');
                totalCheques = val.substring(ind, val.length());
            }


        }

        System.out.println("Bank Address: " + bankAddress);
        System.out.println("Customer Name: " + customerName);
        System.out.println("Customer Address: " + customerAddress);
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Statement Date: " + statementDate);
        System.out.println("Ending Balance: " + endingBalance);
        System.out.println("Total Withdrawl: " + totalWithdrawl);
        System.out.println("Total Deposit: " + totalDeposits);
        System.out.println("Total Cheques: " + totalCheques);
    }
}
