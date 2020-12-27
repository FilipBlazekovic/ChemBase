package hr.chembase.web.conf;

import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {

    private static Document xmlDoc;
    private static String configFilePath  = "/etc/development/chembase/ChemBase.xml";
    private static String logbackFilePath = "/etc/development/chembase/ChemBase-logback.xml";

    /* _______________________________________________________________________________________________ */

    public static void initializeConfiguration()
    {
        Logger logger = LoggerFactory.getLogger("chembase-logger");
        try
        {
            final String tempConfigFilePath = System.getProperty("ChemBaseConfigFile");
            if (tempConfigFilePath != null && tempConfigFilePath.trim().length() > 0)
                configFilePath = tempConfigFilePath;
            
            final String tempLogbackFilePath = System.getProperty("ChemBaseLogbackFile");
            if (tempLogbackFilePath != null && tempLogbackFilePath.trim().length() > 0)
                logbackFilePath = tempLogbackFilePath;        
        }
        catch (Exception ex) { logger.error("Configuration error!", ex); }

        try
        {
            xmlDoc = new SAXBuilder().build(new File(configFilePath));
        }
        catch (JDOMException ex)
        {
            logger.error("Configuration error!", ex);
        }
        catch (IOException ex)
        {
            logger.error("Configuration error!", ex);
        }
    }
    
    /* _______________________________________________________________________________________________ */

    public static String getConfigFilePath()
    {
        return configFilePath;
    }

    public static String getLogbackFilePath()
    {
        return logbackFilePath;
    }

    /* _______________________________________________________________________________________________ */
    
    public static String getDatabaseHost() 
    {
        return xmlDoc.getRootElement().getChild("host").getText();
    }

    public static String getDatabasePort() 
    {
        return xmlDoc.getRootElement().getChild("port").getText();
    }

    public static String getDatabaseName() 
    {
        return xmlDoc.getRootElement().getChild("db").getText();
    }
    
    public static String getDatabaseUser()
    {
        return xmlDoc.getRootElement().getChild("user").getText();
    }

    public static String getDatabasePassword()
    {
        return xmlDoc.getRootElement().getChild("pass").getText();
    }
    
    /* _______________________________________________________________________________________________ */

}
