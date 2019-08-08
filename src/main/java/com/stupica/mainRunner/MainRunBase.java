package com.stupica.mainRunner;


import com.stupica.ConstGlobal;
import com.stupica.GlobalVar;
import com.stupica.core.UtilDate;
import com.stupica.core.UtilString;

import jargs.gnu.CmdLineParser;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by bostjans on 24/07/17.
 */
public class MainRunBase {
    // Variables
    //
    /**
     * terminal escape sequence (starts white text)
     */
    public String TERM_WHITE = "\033[1;37m";
    /** terminal escape sequence (starts yellow text) */
    public String TERM_YELLOW = "\033[1;33m";
    /** terminal escape sequence (starts purple text) */
    public String TERM_LPURPLE = "\033[1;35m";
    /**
     * terminal escape sequence (starts light red text)
     */
    public String TERM_LRED = "\033[1;31m";
    /**
     * terminal escape sequence (starts light cyan text)
     */
    public String TERM_LCYAN = "\033[1;36m";
    /**
     * terminal escape sequence (starts light green text)
     */
    public String TERM_LGREEN = "\033[1;32m";
    /**
     * terminal escape sequence (starts light blue text)
     */
    public String TERM_LBLUE = "\033[1;34m";
    /**
     * terminal escape sequence (starts dark gray text)
     */
    public String TERM_DGRAY = "\033[1;30m";
    /**
     * terminal escape sequence (starts gray text)
     */
    public String TERM_GRAY = "\033[0;37m";
    /**
     * terminal escape sequence (starts brown text)
     */
    public String TERM_BROWN = "\033[0;33m";
    /**
     * terminal escape sequence (starts purple text)
     */
    public String TERM_PURPLE = "\033[0;35m";
    /**
     * terminal escape sequence (starts red text)
     */
    public String TERM_RED = "\033[0;31m";
    /**
     * terminal escape sequence (starts cyan text)
     */
    public String TERM_CYAN = "\033[0;36m";
    /**
     * terminal escape sequence (starts green text)
     */
    public String TERM_GREEN = "\033[0;32m";
    /**
     * terminal escape sequence (starts blue text)
     */
    public String TERM_BLUE = "\033[0;34m";
    /**
     * terminal escape sequence (starts black text)
     */
    public String TERM_BLACK = "\033[0;30m";
    /** terminal escape sequence (starts bolded text) */
    public String TERM_BOLD = "\033[40m\033[1;37m";
    /** terminal escape sequence (ends coloured text) */
    public String TERM_RESET = "\033[0m";

    public static String MANIFEST_KEY_IMPL_VERSION = "Implementation-Version";
    public static String MANIFEST_KEY_SPEC_VERSION = "Specification-Version";

    /**
     * Flag: should read configuration from file?
     */
    public boolean bShouldReadConfig = true;
    /**
     * Flag: should read configuration from program arguments?
     */
    public boolean bShouldReadArguments = true;
    /**
     * Flag: is program/process running in loops?
     */
    public boolean bIsRunInLoop = false;
    protected long iMaxNumOfLoops = 1;
    protected int  iPauseBetweenLoop = 1000 * 2;

    public String sJavaVersion = "/";

    protected Properties objPropSett = new Properties();

    protected CmdLineParser obj_parser = null;
    protected CmdLineParser.Option obj_op_help;
    protected CmdLineParser.Option obj_op_quiet;
    protected CmdLineParser.Option obj_op_verbose;

    protected static Logger logger = Logger.getLogger(MainRunBase.class.getName());


    // inner class
    public class RefDataInteger {
        public int  iCountData = 0;
    }


    /**
     * Object constructor
     */
    public MainRunBase() {
        double javaVersion = 0;

        sJavaVersion = System.getProperty("java.runtime.version");

        // are standard streams connected to terminal?
        // we need jvm 1.6.x to figure this out...
        try {
            javaVersion = Double.parseDouble(sJavaVersion.substring(0, 3));
        } catch (Exception e) {
            //System.err.println("ERROR: Java (runtime) version could not be identified! Val.: " + sJavaVersion);
            msgErr("Java (runtime) version could not be identified! Val.: " + sJavaVersion);
        }
        if (javaVersion > 1.5 && System.console() == null) {
            TERM_WHITE = "";
            TERM_YELLOW = "";
            TERM_LPURPLE = "";
            TERM_LRED = "";
            TERM_LCYAN = "";
            TERM_LGREEN = "";
            TERM_LBLUE = "";
            TERM_DGRAY = "";
            TERM_GRAY = "";
            TERM_BROWN = "";
            TERM_PURPLE = "";
            TERM_RED = "";
            TERM_CYAN = "";
            TERM_GREEN = "";
            TERM_BLUE = "";
            TERM_BLACK = "";
            TERM_BOLD = "";
            TERM_RESET = "";
        }
    }


    /**
     * Method: initialize
     *
     * ..
     */
    protected void initialize() {
    }


    /**
     * Method: mainStart
     *
     * ..
     */
    protected void mainStart() {
        if (GlobalVar.bIsModeTest) {
            if (logger != null) {
                logger.setLevel(Level.FINE);

                //ConsoleHandler handler = new ConsoleHandler();
                // PUBLISH this level
                //handler.setLevel(Level.FINE);
                //logger.addHandler(handler);
                //
                //logger.setUseParentHandlers(false);
            }
        }

        // Check logger
        {
            String sTemp = "mainStart(): Program is starting ..";
            if (logger != null) {
                logger.info(sTemp);
            } else {
                msgInfo(sTemp);
            }
        }
    }


    public void printSystemInfo() {
        printSystemInfo(null);
    }

    public void printSystemInfo(OutputStream s) {
        if (s == null)
            s = System.out;
        PrintStream out = new PrintStream(s);
        out.println("\t--> DateTime: " + UtilDate.toUniversalString(new Date()));
        System.getProperties().list(out);
    }


    public void printProgramInfo() {
        // Display program info
        System.out.println();
        //System.out.println("Program: " + GlobalVar.getInstance().sProgName);
        msgInfo("Program: " + GlobalVar.getInstance().sProgName);
        //System.out.println("Version: " + GlobalVar.getInstance().get_version());
        msgWarn("Version: " + GlobalVar.getInstance().get_version());
        //System.out.println("Made by: " + GlobalVar.getInstance().sAuthor);
        msgInfo("Made by: " + GlobalVar.getInstance().sAuthor);
        System.out.println("===");
    }


    /**
     * Method: readConfig
     *
     * Read ..
     *
     * @return int iResult	1 = AllOK;
     */
    public int readConfig() {
        // Local variables
        int             iResult;
        String          sFileConf = "properties/config.properties";
        FileInputStream fileIn = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            try {
                fileIn = new FileInputStream(sFileConf);
            } catch (FileNotFoundException e) {
                iResult = ConstGlobal.RETURN_ERROR;
                logger.severe("readConfig(): Error at reading conf. file!"
                        + " Msg.: " + e.getMessage());
            }
        }
        if (fileIn == null) {
            iResult = ConstGlobal.RETURN_ERROR;
            logger.severe("readConfig(): Error at reading conf. file! No file present!"
                    + " File: " + sFileConf);
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            objPropSett = new Properties();

            if (objPropSett != null) {
                try {
                    objPropSett.load(fileIn);
                    //objPropSett.loadFromXML(fileIn);
                } catch (InvalidPropertiesFormatException e) {
                    iResult = ConstGlobal.RETURN_ERROR;
                    logger.severe("readConfig(): Error at parsing IN Document!"
                            + " Msg.: " + e.getMessage());
                } catch (IOException e) {
                    iResult = ConstGlobal.RETURN_ERROR;
                    logger.severe("readConfig(): Error at reading IN Document!"
                            + " Msg.: " + e.getMessage());
                }
            }
        }
        return iResult;
    }

    /**
     * Method: setConfig
     *
     * ..
     *
     * @return int 	1 = AllOK;
     */
    public int setConfig() {
        return ConstGlobal.RETURN_OK;
    }


    /**
     * Method: readVersionManifest
     *
     * ..
     *
     * @return int iResult	1 = AllOK;
     */
    public int readVersionManifest() {
        // Local variables
        int         iResult;
        boolean     bIsDevEnv = false;
        String      sTemp = null;
        Manifest    mf = null;
        Attributes  atts = null;
        Enumeration<URL>    resources = null;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

//        sTemp = MainRunBase.class.getPackage().getImplementationVersion();

        try {
            //mf.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"));
            resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
        } catch (Exception ex) {
            iResult = ConstGlobal.RETURN_ERROR;
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            //System.out.println("Resource output for MANIFEST .. :");
            while (resources.hasMoreElements()) {
                URL objResource = resources.nextElement();
                try {
                    //System.out.println(objResource);
                    // check that this is your manifest and do what you need or get the next one
                    if (       (objResource.toString().toLowerCase().contains("intellij"))
                            && (objResource.toString().toLowerCase().contains("idea")) ) {
                        bIsDevEnv = true;
                        break;
                    }
//                    if (       (objResource.toString().toLowerCase().contains("lenkotr"))
//                            || (objResource.toString().toLowerCase().contains("stupica")) ) {
//                        mf = new Manifest(objResource.openStream());
//                        //break;
//                    }
                    if (       (objResource.toString().toLowerCase().contains(GlobalVar.getInstance().sProgName.toLowerCase())) ) {
                        mf = new Manifest(objResource.openStream());
                        break;
                    }
                } catch (IOException ex) {
                    iResult = ConstGlobal.RETURN_ERROR;
                    System.err.println("Error reading Manifest: " + objResource
                            + "; Msg.: " + ex.getMessage());
                    break;
                }
            }
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            if (mf != null) {
                atts = mf.getMainAttributes();
            }
        }
        if (bIsDevEnv) {
            sTemp = "DEV";
        } else {
            if (atts != null) {
                sTemp = atts.getValue(MANIFEST_KEY_IMPL_VERSION);
                if (UtilString.isEmpty(sTemp)) sTemp = atts.getValue(MANIFEST_KEY_SPEC_VERSION);
            }
        }
        if (UtilString.isEmpty(sTemp)) sTemp = "/";
        //sTemp = "2.0.0";
        //System.out.println("\tVersion text extracted: " + sTemp);
        if (sTemp.contains(".")) {
            String[] arrVersion = sTemp.split("\\.");
            //System.out.println("\tVersion num extracted: " + arrVersion.length);
            GlobalVar.getInstance().sVersionMax = arrVersion[0];
            if (arrVersion.length > 0) GlobalVar.getInstance().sVersionMin = arrVersion[1];
            if (arrVersion.length > 1) GlobalVar.getInstance().sVersionPatch = arrVersion[2];
        } else {
            GlobalVar.getInstance().sVersionMax = sTemp;
            GlobalVar.getInstance().sVersionMin = "0";
            GlobalVar.getInstance().sVersionPatch = "Build";
        }
        return iResult;
    }


    /**
     * Method: defineArguments
     *
     * ..
     *
     * @return int iResult	1 = AllOK;
     */
    protected int defineArguments() {
        // Local variables
        int         iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        obj_op_help = obj_parser.addBooleanOption('h', "help");
        obj_op_quiet = obj_parser.addBooleanOption('q', "quiet");
        obj_op_verbose = obj_parser.addBooleanOption('v', "verbose");

        return iResult;
    }

    /**
     * Method: readArguments
     *
     * ..
     *
     * @return int iResult	1 = AllOK;
     */
    protected int readArguments() {
        // Local variables
        int         iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_OK;

        if (Boolean.TRUE.equals(obj_parser.getOptionValue(obj_op_help))) {
            printUsage();
            System.exit(ConstGlobal.PROCESS_EXIT_SUCCESS);
        }

        if (Boolean.TRUE.equals(obj_parser.getOptionValue(obj_op_verbose))) {
            GlobalVar.bIsModeVerbose = true;
            //System.out.println("Java (runtime) version: " + sJavaVersion);
            msgInfo("Java (runtime) version: " + sJavaVersion);
            printSystemInfo();
        }
        if (Boolean.TRUE.equals(obj_parser.getOptionValue(obj_op_quiet))) {
            GlobalVar.bIsModeVerbose = false;
        }

        if (!Boolean.TRUE.equals(obj_parser.getOptionValue(obj_op_quiet))) {
            // Display program info
            printProgramInfo();
            //GlobalVar.bIsModeVerbose = true;
        //} else {
            //GlobalVar.bIsModeVerbose = false;
        }
        return iResult;
    }


    /**
     * Method: invokeApp
     *
     * ..
     *
     * @return int iResult	1 = AllOK;
     */
    public int invokeApp(String arrArgs[]) {
        // Local variables
        int         iResult;

        // Initialization
        iResult = ConstGlobal.PROCESS_EXIT_SUCCESS;

        // Initialize
        initialize();

        // MainStart - for addOn tasks when main() method is started
        mainStart();

        // Read Version info. -  from Manifest
        {
            int iResultTemp;

            iResultTemp = readVersionManifest();
            // Error
            if (iResultTemp != ConstGlobal.RETURN_OK) {
                msgErr("main(): Error at readVersionManifest() operation!");
                iResult = ConstGlobal.PROCESS_EXIT_FAILURE;
            }
        }

        // Read config
        if (bShouldReadConfig) {
            int     iResultTemp;
            String  sTemp;

            // Check previous step
            if (iResult == ConstGlobal.PROCESS_EXIT_SUCCESS) {
                // Read ..
                iResultTemp = readConfig();
                // Error
                if (iResultTemp != ConstGlobal.RETURN_OK) {
                    //sTemp = "invokeApp(): Error at readConfig() operation!"
                    //        + "\n\tPrepare config file from sample .. as this will be required in future!";
                    sTemp = "invokeApp(): Error at readConfig() operation!";
                    logger.severe(sTemp);
                    msgWarn(sTemp);
                    iResult = ConstGlobal.PROCESS_EXIT_FAILURE;
                }
            }
        }

        // Set config/settings (from config file)
        if (bShouldReadConfig) {
            int     iResultTemp;
            String  sTemp;

            // Check previous step
            if (iResult == ConstGlobal.PROCESS_EXIT_SUCCESS) {
                // Read ..
                iResultTemp = setConfig();
                // Error
                if (iResultTemp != ConstGlobal.RETURN_OK) {
                    //sTemp = "invokeApp(): Error at readConfig() operation!"
                    //        + "\n\tPrepare config file from sample .. as this will be required in future!";
                    sTemp = "invokeApp(): Error at setConfig() operation!";
                    logger.severe(sTemp);
                    msgWarn(sTemp);
                    iResult = ConstGlobal.PROCESS_EXIT_FAILURE;
                }
            }
        }

        // Define args
        if (bShouldReadArguments) {
            int     iResultTemp;
            String  sTemp;

            // Check previous step
            if (iResult == ConstGlobal.PROCESS_EXIT_SUCCESS) {
                // Create a CmdLineParser, and add to it the appropriate Options.
                obj_parser = new CmdLineParser();

                iResultTemp = defineArguments();
                // Error
                if (iResultTemp != ConstGlobal.RETURN_OK) {
                    sTemp = "invokeApp(): Error at defineArguments() operation!";
                    logger.severe(sTemp);
                    msgWarn(sTemp);
                    iResult = ConstGlobal.PROCESS_EXIT_FAILURE;
                }
            }
        }

        // Parse args
        if (bShouldReadArguments) {
            int     iResultTemp;
            String  sTemp;

            // Check previous step
            if (iResult == ConstGlobal.PROCESS_EXIT_SUCCESS) {
                try {
                    obj_parser.parse(arrArgs);
                } catch (CmdLineParser.OptionException e) {
                    msgErr(e.getMessage());
                    printUsage();
                    System.exit(ConstGlobal.PROCESS_EXIT_FAIL_PARAM);
                }

                iResultTemp = readArguments();
                // Error
                if (iResultTemp != ConstGlobal.RETURN_OK) {
                    sTemp = "invokeApp(): Error at readArguments() operation!";
                    logger.severe(sTemp);
                    msgWarn(sTemp);
                    iResult = ConstGlobal.PROCESS_EXIT_FAILURE;
                }
            }
        }

        // Invoke Run()
        {
            int     iResultTemp;
            String  sTemp;

            // Check previous step
            if (iResult == ConstGlobal.PROCESS_EXIT_SUCCESS) {
                iResultTemp = runBefore();
                // Error
                if (iResultTemp != ConstGlobal.RETURN_OK) {
                    sTemp = "invokeApp(): Error at runBefore() operation!";
                    logger.severe(sTemp);
                    msgErr(sTemp);
                    iResult = ConstGlobal.PROCESS_EXIT_FAILURE;
                }
            }
            // Check previous step
            if (iResult == ConstGlobal.PROCESS_EXIT_SUCCESS) {
                // Run ..
                iResultTemp = run();
                // Error
                if (iResultTemp != ConstGlobal.RETURN_OK) {
                    sTemp = "invokeApp(): Error at run() operation!";
                    logger.severe(sTemp);
                    msgErr(sTemp);
                    iResult = ConstGlobal.PROCESS_EXIT_FAILURE;
                }
            }
            // Check previous step
            //if (iResult == ConstGlobal.PROCESS_EXIT_SUCCESS)
            {
                iResultTemp = runAfter();
                // Error
                if (iResultTemp != ConstGlobal.RETURN_OK) {
                    sTemp = "invokeApp(): Error at runAfter() operation!";
                    logger.severe(sTemp);
                    msgErr(sTemp);
                    iResult = ConstGlobal.PROCESS_EXIT_FAILURE;
                }
            }
        }
        return iResult;
    }


    /**
     * Prints info message to stdout
     * @param s message
     */
    protected void msgInfo(String s) {
        if (s != null) {
            System.out.println(TERM_BOLD + "INFO:    " + TERM_RESET + s);
        }
    }

    /**
     * Prints warning message to stderr
     * @param s message
     */
    protected void msgWarn(String s) {
        if (s != null) {
            System.out.println(TERM_YELLOW + "WARNING: " + TERM_RESET + s);
        }
    }

    /**
     * Prints error message to stderr
     * @param s message
     */
    protected void msgErr(String s) {
        if (s != null) {
            System.err.println(TERM_LRED + "ERROR:   " + TERM_RESET + s);
            //if (logger != null)
            //	logger.error(s);
        }
    }


    /**
     * Method: printUsage
     *
     * ..
     */
    protected void printUsage() {
        System.err.println("Usage: prog [-h,--help]");
        System.err.println("            [-q,--quiet]");
        System.err.println("            [-v,--verbose]");
        System.err.println("            [..]");
    }


    /**
     * Method: run
     *
     * Run ..
     *
     * @return int	1 = AllOK;
     */
    public int run() {
        int         iResult;
        String      sTemp;

        // Initialization
        iResult = ConstGlobal.RETURN_SUCCESS;

        if (bIsRunInLoop) {
            iResult = runLoopBefore();
            // Error
            if (iResult != ConstGlobal.RETURN_OK) {
                sTemp = "run(): Error at runLoopBefore() operation!";
                logger.severe(sTemp);
                msgErr(sTemp);
            }
            // Check previous step
            if (iResult == ConstGlobal.RETURN_OK) {
                // Run ..
                iResult = runInLoop();
                // Error
                if (iResult != ConstGlobal.RETURN_OK) {
                    sTemp = "run(): Error at runInLoop() operation!";
                    logger.severe(sTemp);
                    msgErr(sTemp);
                }
            }
            // Check previous step
            if (iResult == ConstGlobal.RETURN_OK) {
                iResult = runLoopAfter();
                // Error
                if (iResult != ConstGlobal.RETURN_OK) {
                    sTemp = "run(): Error at runLoopAfter() operation!";
                    logger.severe(sTemp);
                    msgErr(sTemp);
                }
            }
        }
        return iResult;
    }

    /**
     * Method: runBefore
     *
     * Run ..
     *
     * @return int	1 = AllOK;
     */
    protected int runBefore() {
        return ConstGlobal.RETURN_OK;
    }

    /**
     * Method: runAfter
     *
     * Run ..
     *
     * @return int	1 = AllOK;
     */
    protected int runAfter() {
        return ConstGlobal.RETURN_OK;
    }


    /**
     * Method: runInLoop
     *
     * Run_in_Loop ..
     *
     * @return int	1 = AllOK;
     */
    protected int runInLoop() {
        // Local variables
        int         iResult;
        //
        long        iCountLoop = 0L;
        long        iCountData = 0L;
        Date        dtStart;
        Date        dtStartLoop;
        Date        dtStop;
        RefDataInteger objRefCountData;

        // Initialization
        iResult = ConstGlobal.RETURN_SUCCESS;
        dtStart = new Date();
        //dtStartLoop = new Date();
        objRefCountData = new RefDataInteger();

        // Process data ..
        //
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            do {
                int     iResultTemp;
                String  sTemp;

                dtStartLoop = new Date();

                // Check previous step
                if (iResult == ConstGlobal.RETURN_OK) {
                    // Run ..
                    iResultTemp = runLoopCycle(objRefCountData);
                    // Error
                    if (iResultTemp != ConstGlobal.RETURN_OK) {
                        sTemp = "runInLoop(): Error at runLoopCycle() operation!";
                        logger.severe(sTemp);
                        msgErr(sTemp);
                        iResult = iResultTemp;
                    }
                }
                iCountData = objRefCountData.iCountData;

                iCountLoop++;
                if (iMaxNumOfLoops > 0) {
                    if (iMaxNumOfLoops <= (iCountLoop - 0)) {
                        logger.info("runInLoop(): Maximum number of loops reached: " + iMaxNumOfLoops);
                        break;
                    }
                }

                // Check previous step
                if (iResult == ConstGlobal.RETURN_OK) {
                    if (iMaxNumOfLoops != 1L) {
                        try { // Pause for ? second(s)
                            StringBuilder   sSleep = new StringBuilder();
                            Date            dtStopLoop = new Date();

                            if (UtilString.isEmptyTrim(GlobalVar.getInstance().sProgName)) {
                                sSleep.append("runInLoop()");
                            } else {
                                sSleep.append(String.format("%15.15s", GlobalVar.getInstance().sProgName));
                            }
                            sSleep.append(": Sleep ..");
                            sSleep.append(" -> #Loop: ").append(String.format("%05d", iCountLoop));
                            sSleep.append("\tTime: ").append(UtilDate.toUniversalString(dtStopLoop));
                            sSleep.append("\tElapse(ms): ").append(String.format("%05d", dtStopLoop.getTime() - dtStartLoop.getTime()));
                            System.out.println(sSleep.toString());
                            Thread.sleep(iPauseBetweenLoop);
                        } catch (Exception ex) {
                            iResult = ConstGlobal.RETURN_ENDOFDATA;
                            logger.severe("runInLoop: Interrupt exception!!"
                                    + " Msg.: " + ex.getMessage());
                        }
                    }
                }
            } while (iResult == ConstGlobal.RETURN_OK);
        }

        dtStop = new Date();
        logger.info("runInLoop(): Processing done."
                + "\n\tData num.: " + iCountData
                + "\tLoop#: " + iCountLoop
                + "\t\tDuration(ms): " + (dtStop.getTime() - dtStart.getTime()));
        return iResult;
    }

    /**
     * Method: runLoopBefore
     *
     * Run_Loop_cycle_before ..
     *
     * @return int	1 = AllOK;
     */
    protected int runLoopBefore() {
        return ConstGlobal.RETURN_OK;
    }

    /**
     * Method: runLoopAfter
     *
     * Run_Loop_cycle_before ..
     *
     * @return int	1 = AllOK;
     */
    protected int runLoopAfter() {
        return ConstGlobal.RETURN_OK;
    }


    /**
     * Method: runLoopCycle
     *
     * Run_Loop_cycle ..
     *
     * @return int	1 = AllOK;
     */
    protected int runLoopCycle(RefDataInteger aobjRefCountData) {
        aobjRefCountData.iCountData = 0;
        return ConstGlobal.RETURN_OK;
    }
}
