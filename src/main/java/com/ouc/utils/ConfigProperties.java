package com.ouc.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigProperties {    
    private Properties props = null;
    private FileInputStream configInputStream = null;
    //private Logger logger = LogManager.getLogger();
    
    
    public ConfigProperties() {
        loadProperties();
    }
    
    private void loadProperties() {
        props = new Properties();
        try {
            String configFilePath = "resources/config.properties";
            configInputStream = new FileInputStream(new File(configFilePath));
            props.load(configInputStream);
        } catch (IOException e) {
            //logger.error(e.getMessage());
            System.exit(1);
        } finally {
            if (configInputStream != null) {
                try {
                	configInputStream.close();
                } catch (IOException ex) {
                    //logger.error(ex.getMessage());
                    System.exit(1);
                }
            }
        }
    }
    
    public String readProperty(String key) {
        return props.getProperty(key);
    }
}