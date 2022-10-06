package server;

import constants.GameConstants;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import tools.EncodingDetect;

public class ServerProperties {

    private static final Properties props = new Properties();

    private ServerProperties() {
    }

    static {
        String toLoad;
        //loadProperties(toLoad);
        if (getProperty("GMS") != null) {
            GameConstants.GMS = Boolean.parseBoolean(getProperty("GMS"));
        }
        toLoad = GameConstants.GMS ? "server.properties" : "server.properties";
        loadProperties(toLoad);

    }

    public static void loadProperties(String s) {

        try {
            InputStream in = new FileInputStream(s);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in, EncodingDetect.codeString(s)));
            props.load(bf);
            bf.close();
        } catch (IOException ex) {
            System.err.println("讀取\"" + s + "\"檔案失敗 " + ex);
        }

    }

    public static String getProperty(String s) {
        return props.getProperty(s);
    }

    public static void setProperty(String prop, String newInf) {
        props.setProperty(prop, newInf);
    }

    public static String getProperty(String s, String def) {
        return props.getProperty(s, def);
    }

    public static boolean getProperty(String s, boolean def) {
        return getProperty(s, def ? "true" : "false").equalsIgnoreCase("true");
    }

    public static byte getProperty(String s, byte def) {
        String property = props.getProperty(s);
        if (property != null) {
            return Byte.parseByte(property);
        }
        return def;
    }

    public static short getProperty(String s, short def) {
        String property = props.getProperty(s);
        if (property != null) {
            return Short.parseShort(property);
        }
        return def;
    }

    public static int getProperty(String s, int def) {
        String property = props.getProperty(s);
        if (property != null) {
            return Integer.parseInt(property);
        }
        return def;
    }

    public static long getProperty(String s, long def) {
        String property = props.getProperty(s);
        if (property != null) {
            return Long.parseLong(property);
        }
        return def;
    }
}
