package database;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class DatabaseProperties {

    private static final Properties props = new Properties();

    static {
        loadProperties("db.properties");
    }

    public static void loadProperties(String s) {
        FileReader fr;
        try {
            fr = new FileReader(s);
            props.load(fr);
            fr.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(String s) {
        return props.getProperty(s);
    }
}
