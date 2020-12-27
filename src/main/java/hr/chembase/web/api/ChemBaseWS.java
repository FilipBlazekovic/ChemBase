package hr.chembase.web.api;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import hr.chembase.web.conf.Configuration;

public class ChemBaseWS extends ResourceConfig {

    public ChemBaseWS()
    {   
//      SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        Configuration.initializeConfiguration();
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try
        {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(Configuration.getLogbackFilePath());
        }
        catch (JoranException ex) { ex.printStackTrace(); }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);

        Logger julLogger = Logger.getLogger("hr.chembase.ChemBase.logger");
        julLogger.setLevel(Level.ALL);
        register(new LoggingFeature(julLogger, Level.ALL, LoggingFeature.Verbosity.PAYLOAD_ANY, 1024*50));
    }
}
