package com.briq.solutions.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ReadPropertiesFile {
    /**
     *
     * @param propertiesFilePath : Path of the properties file you want to read and
     *                           load
     * @return the Properties object
     */
    public static Properties loadPropertiesFile(String propertiesFilePath) {

        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(propertiesFilePath);
            properties.load(fis);
            System.out.println("Properties of '" + propertiesFilePath + "' has been loaded in the object successfully.");
        } catch (FileNotFoundException e) {
            // log.debug(e.getMessage());
            System.out.println("Properties File is not available in '" + propertiesFilePath
                    + " folder. Please place the file in this location.");
            System.out.println("Terminating the execution abruptly.");
            System.exit(0);

        } catch (IOException e) {
            System.out.println("Could not read the file. Please make sure properties file is ready to be used.");
        } catch (Exception e) {
            System.out.println("Some unexpected error occurred. Please make sure properties file is ready to be used.");
        }

        return properties;
    }





}
