package utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class ConfigReader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);


    /**
     *
     * @return Bot token as String
     */
    public static String getToken(){
            try {
                Properties properties = new Properties();
                properties.load(ConfigReader.class.getClassLoader().getResourceAsStream("config.properties"));
                return properties.getProperty("token");
            }
            catch (IOException e){
                logger.error(Arrays.toString(e.getStackTrace()));
            }
            return null;

    }


}
