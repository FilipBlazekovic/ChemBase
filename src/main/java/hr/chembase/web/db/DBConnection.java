package hr.chembase.web.db;

import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hr.chembase.web.conf.Configuration;
import hr.chembase.web.utils.Utils;

public class DBConnection {

    private static Logger logger = LoggerFactory.getLogger("chembase-logger");

    /* _______________________________________________________________________________________________ */
    
    public static Connection openDBConnection()
    {
        Connection connection = null;
        try
        {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://" +
                                                     Configuration.getDatabaseHost() + ":" +
                                                     Configuration.getDatabasePort() + "/" +
                                                     Configuration.getDatabaseName(),
                                                     Configuration.getDatabaseUser(),
                                                     Configuration.getDatabasePassword()); 
        }
        catch (Exception ex)
        {
            logger.info(Utils.generateStackTrace(ex));
        }
        return connection;
    }
    
    /* _______________________________________________________________________________________________ */

    public static void closeDBConnection(Connection connection)
    {
        try
        {
            if (connection != null)
                connection.close();
        }
        catch (Exception ex)
        {
            logger.info(Utils.generateStackTrace(ex));
        }
    }
    
    /* _______________________________________________________________________________________________ */

}
