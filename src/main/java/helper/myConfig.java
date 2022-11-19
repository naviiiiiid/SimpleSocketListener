package helper;

import main.socketListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by n-soorani on 2017/03/14.
 */
public class myConfig {

    private final Properties properties;

    public myConfig(String pathParam) throws IOException {

        try (InputStream inputStream = socketListener.class.getClassLoader().getResourceAsStream(pathParam)) {
            this.properties = new Properties();
            this.properties.load(inputStream);
        } catch (Exception ex) {
            throw ex;
        }
    }

    public String getProperties(String key)throws Exception{

        String prop;
        if (this.properties.containsKey(key)) {
            prop = this.properties.getProperty(key);
        }
        else {
            throw new Exception("key: " + key + "not Exist!!!");
        }
        return prop;
    }

}
