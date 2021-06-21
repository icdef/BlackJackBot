package main.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigReader {


    private static ConfigReader INSTANCE;

    private ConfigReader(){

    }

    private final String configFilePath =
            Paths.get(System.getProperty("user.dir"), "src","main","resources","config.properties").toString();


    public String getToken(){
        try (FileInputStream inputStream = new FileInputStream(configFilePath)){
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty("token");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static ConfigReader getInstance(){
        return INSTANCE == null ? new ConfigReader() : INSTANCE;
    }
}
