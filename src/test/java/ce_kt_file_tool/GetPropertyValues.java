package ce_kt_file_tool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class GetPropertyValues {
    String result = "";
    InputStream inputStream;
    String path = "";
    String log = "";

    public String getPath() {
        return path;
    }

    public String getLog() {
        return log;
    }

    public String getPropValues() throws IOException {

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            Date time = new Date(System.currentTimeMillis());

            // get the property value and print it out
            String path = prop.getProperty("path");
            String log = prop.getProperty("log");
            this.path = path;
            this.log = log;

            result = "Property List = " + path + ", " + " " + ", Log = " + log;
            System.out.println(result + "\nProgram Ran on " + time);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
        return result;
    }
}
