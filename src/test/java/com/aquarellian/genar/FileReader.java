package com.aquarellian.genar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 10.07.2015 13:08
 * <p/>
 * $Id$
 */
public final class FileReader {
    private FileReader() {
    }

    public static String readFile(URL file) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(file.getFile()), Charset.defaultCharset());
            StringBuilder sb = new StringBuilder();
            for (String s : lines) {
                sb.append(s);
            }
            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public static Properties loadProperties(URL file) throws Exception {
        Properties prop = new Properties();
        FileInputStream fis =
                new FileInputStream(file.getFile());
        prop.loadFromXML(fis);
        return prop;
    }
}
