package com.stupica.mainRunner;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertNotNull;


//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainRunBaseTestLog {

    //static {
    //    System.setProperty("java.util.logging.config.file", ClassLoader.getSystemResource("logging.properties").getPath());
    //}

    //protected static Logger logger;
    protected static Logger logger = Logger.getLogger(MainRunBaseTestLog.class.getName());


    @org.junit.jupiter.api.BeforeAll
    static void setUpOnce() {
        String  sTemp = ClassLoader.getSystemResource("logging.properties").getPath();
        File    objFileLogProp = new File(sTemp);

        if (objFileLogProp.exists()) {
            System.out.println("INFO: log prop. file: " + objFileLogProp.getAbsolutePath());
            //System.setProperty("java.util.logging.config.file", sTemp);
        } else {
            System.out.println("WARN: file: " + objFileLogProp.getAbsolutePath() + " > does NOT exists!");
        }
        //logger = Logger.getLogger(MainRunBaseTestLog.class.getName());
    }

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void test_logInit() {
        assertNotNull(logger);
        {
            logger.setLevel(Level.FINE);

            //ConsoleHandler handler = new ConsoleHandler();
            // PUBLISH this level
            //handler.setLevel(Level.FINE);
            //logger.addHandler(handler);
            //
            //logger.setUseParentHandlers(false);
        }
        for (Handler objLoop : Logger.getGlobal().getHandlers()) {
            System.out.println("stdOut: Handler: " + objLoop.toString());
        }
        for (Handler objLoop : Logger.getAnonymousLogger().getHandlers()) {
            System.out.println("stdOut: AnonymouseHandler: " + objLoop.toString());
        }
        {   // Check logger
            String sTemp = "testStart(): Program (PID: " + "/" + ") is starting ..";
            logger.info(sTemp);
            System.out.println("stdOut: " + sTemp);
        }
    }
}