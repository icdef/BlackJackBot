package main.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class ConfigReader {


    private static ConfigReader INSTANCE;
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private ConfigReader(){

    }

    public String getToken(){
            try {
                Properties properties = new Properties();
                properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
                return properties.getProperty("token");
            }
            catch (IOException e){
                logger.error(Arrays.toString(e.getStackTrace()));
            }
            return null;

    }

    public static ConfigReader getInstance(){
        if (INSTANCE == null)
            INSTANCE = new ConfigReader();

        return INSTANCE;
    }
}
