package main.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {



    public String getToken(){
        try {
            Properties properties = new Properties();
            FileInputStream inputStream = new FileInputStream("resources/config.properties");
            properties.load(inputStream);
            return properties.getProperty("token");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
