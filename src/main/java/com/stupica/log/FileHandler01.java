package com.stupica.log;


import java.io.IOException;
import java.util.logging.FileHandler;


/**
 * Ref.: https://stackoverflow.com/questions/3639694/java-util-logging-properties-how-to-log-to-two-different-files
 */
public class FileHandler01 extends FileHandler {

    public FileHandler01() throws IOException, SecurityException {
        super();
    }
}
