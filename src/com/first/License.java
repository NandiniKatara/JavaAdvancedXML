package com.first;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.io.File;
import java.io.FileWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class License {
    public static void parse(String xml_file, FileWriter validLicensesFile,
                             FileWriter inValidLicensesFile, FileWriter mergedFile, Set<String> licenseSet) {

        try {
            File inputFile = new File(xml_file);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            NodeList producerList = doc.getElementsByTagName("CSR_Producer");

            for (int i = 0; i < producerList.getLength(); i++) {
                Element producerElement = (Element) producerList.item(i);
                String NIPR_Number = producerElement.getAttribute("NIPR_Number");
                NodeList licenseList = producerElement.getElementsByTagName("License");
                for (int j = 0; j < licenseList.getLength(); j++) {
                    Element licenseElement = (Element) licenseList.item(j);
                    String expirationDateStr = licenseElement.getAttribute("License_Expiration_Date");
                    Date expirationDate = dateFormat.parse(expirationDateStr);
                    String licenseNumber = licenseElement.getAttribute("License_Number");
                    String State_Code = licenseElement.getAttribute("State_Code");
                    String Date_Status_Effective = licenseElement.getAttribute("Date_Status_Effective");
                    if (!licenseSet.contains(licenseNumber) && expirationDate.compareTo(currentDate) >= 0) {
                        licenseSet.add(licenseNumber);
                        validLicensesFile.write("NIPR Number: " + NIPR_Number + " , " + "State Code: " + State_Code
                                + " , " + "Valid License Number: " + licenseNumber + " , "
                                + "Date Status Effective: " + Date_Status_Effective + "\n");
                        mergedFile.write("NIPR Number: " + NIPR_Number + " , " + "State Code: " + State_Code + " , "
                                + "Valid License Number: " + licenseNumber + " , "
                                + "Date Status Effective: " + Date_Status_Effective + "\n");

                    }
                    else if (!licenseSet.contains(licenseNumber) && expirationDate.compareTo(currentDate) < 0) {
                        licenseSet.add(licenseNumber);
                        inValidLicensesFile.write("NIPR Number: " + NIPR_Number + " , " + "State Code: " + State_Code
                                + " , " + "Valid License Number: " + licenseNumber + " , "
                                + "Date Status Effective: " + Date_Status_Effective + "\n");
                        mergedFile.write("NIPR Number: " + NIPR_Number + " , " + "State Code: " + State_Code + " , "
                                + "Valid License Number: " + licenseNumber + " , "
                                + "Date Status Effective: " + Date_Status_Effective + "\n");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Set<String> licenseSet = new HashSet<String>();
        
        String xmlLicense1 = "src/com/first/xmlLicense1.xml";
        String xmlLicense2 = "src/com/first/xmlLicense2.xml";

        try (FileWriter validLicensesFile = new FileWriter("src/com/first/validLicensesFile.txt");
                FileWriter inValidLicensesFile = new FileWriter("src/com/first/inValidLicensesFile.txt");
                FileWriter mergedFile = new FileWriter("src/com/first/mergedFile.txt")) {
            validLicensesFile.write("Valid Licenses List based on License_Expiration_Date : \n\n");
            inValidLicensesFile.write("InValid Licenses List based on License_Expiration_Date : \n\n");
            mergedFile.write("Merged Licenses List \n\n");
            parse(xmlLicense1, validLicensesFile, inValidLicensesFile, mergedFile, licenseSet);
            parse(xmlLicense2, validLicensesFile, inValidLicensesFile, mergedFile, licenseSet);
            validLicensesFile.close();
            inValidLicensesFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

