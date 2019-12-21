package com.stupica.mainRunner;


import com.stupica.ConstGlobal;
import com.stupica.GlobalVar;
import com.stupica.core.UtilDate;
import com.stupica.core.UtilString;

import jargs.gnu.CmdLineParser;

import java.io.*;
import java.lang.management.ManagementFactory;
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
    public static String MANIFEST_KEY_CAPSULE_VER = "Embedded-Artifacts";

    /**
     * Flag: should read configuration from file?
     */
    public boolean bShouldReadConfig = true;
    /**
     * Flag: should read configuration from program arguments?
     */
    public boolean bShouldReadArguments = true;
    /**
     * Flag: should enable program Shutdown_Hook (for CTRL+C for example)?
     */
    protected boolean bShouldEnableShutdownHook = false;
    public boolean bIsShutdownInitiated = false;
    protected boolean bIsShutdownReady2Stop = false;
    /**
     * Flag: is program/process running in loops?
     */
    public boolean bIsRunInLoop = false;
    protected long iMaxNumOfLoops = 1;
    protected int  iPauseBetweenLoop = 1000 * 2;    // 2 sec .. is default;
    /**
     * Flag: should write loop information to StdOut?
     * Sample:
     *    programName: Sleep .. -> #Loop: 02456        Time: 2019-11-21_23:49:20       Elapse(ms): 02648
     */
    protected boolean bShouldWriteLoopInfo2stdOut = true;
    /**
     * Flag: should write loop information to log?
     */
    protected boolean bShouldWriteLoopInfo2log = false;

    public String sJavaVersion = "/";

    protected Properties objPropSett = new Properties();

    protected CmdLineParser         obj_parser = null;
    protected CmdLineParser.Option  obj_op_help;
    protected CmdLineParser.Option  obj_op_quiet;
    protected CmdLineParser.Option  obj_op_verbose;

    protected final String sDirPid01 = "/var/run";
    protected final String sDirPid02 = "/var/tmp";
    protected final String sDirPid03 = "/tmp";
    protected String sDirPid = sDirPid01;
    protected File objDirPid = null;
    protected File objFilePid = null;

    protected static Logger logger = Logger.getLogger(MainRunBase.class.getName());


    // inner class
    public class RefDataInteger {
        public long iCountLoop = 0L;
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
            String sTemp = "mainStart(): Program (PID: " + getProcessPID() + ") is starting ..";
            if (logger != null) {
                logger.info(sTemp);
            } else {
                msgInfo(sTemp);
            }
        }

        // Create PID (temp)file
        {
            String          sProgName;
            StringBuilder   sMsg = new StringBuilder();

            if (UtilString.isEmptyTrim(GlobalVar.getInstance().sProgName)) sProgName = getProcessPID();
            else                                                           sProgName = GlobalVar.getInstance().sProgName;
            objFilePid = createPidFile(sProgName, sDirPid, sMsg);
            if (objFilePid == null) {
                objFilePid = createPidFile(sProgName, sDirPid02, sMsg);
            }
            if (objFilePid == null) {
                objFilePid = createPidFile(sProgName, System.getProperty("user.dir"), sMsg);
            }
            if (objFilePid == null) {
                objFilePid = createPidFile(sProgName, null, sMsg);
            }
            if (objFilePid == null) {
                msgErr(sMsg.toString());
            } else {
                objFilePid.deleteOnExit();
                sMsg.delete(0, sMsg.length() - 1);
                sMsg.append("(temp) Pid_file created. Program: ").append(sProgName);
                sMsg.append(" = ").append(" PID: ").append(getProcessPID());
                sMsg.append("\n\tPid_file: ").append(objFilePid.getAbsolutePath());
                msgInfo(sMsg.toString());
                logger.info(sMsg.toString());
            }
        }
    }


    /**
     * Method: createPidFile
     *
     * ..
     */
    protected File createPidFile(String asProgName, String asDirPid, StringBuilder asMsg) {
        File    objPidFile = null;

        if (UtilString.isEmpty(asDirPid)) {
            try {
                objPidFile = File.createTempFile(asProgName + "-", ".pid");
            } catch (Exception ex) {
                if (asMsg.length() > 0) asMsg.append("\n\t");
                asMsg.append("Failed to create (temp) Pid_file!! Program: ").append(asProgName);
                asMsg.append(" => ").append("PID: ").append(getProcessPID());
                asMsg.append("\n\tMsg.: ").append(ex.getMessage());
                logger.severe(asMsg.toString());
            }
        } else {
            objDirPid = new File(asDirPid);
            try {
                if (objDirPid.exists())
                    objPidFile = File.createTempFile(asProgName + "-", ".pid", objDirPid);
            } catch (Exception ex) {
                if (asMsg.length() > 0) asMsg.append("\n\t");
                asMsg.append("Failed to create (temp) Pid_file!! Program: ").append(asProgName);
                asMsg.append(" => ").append("PID: ").append(getProcessPID());
                asMsg.append("; Dir.: ").append(asDirPid);
                asMsg.append("\n\tMsg.: ").append(ex.getMessage());
                logger.severe(asMsg.toString());
            }
        }
        if (objPidFile != null) {
            try { // write PID
                BufferedWriter bw = new BufferedWriter(new FileWriter(objPidFile));
                bw.write(getProcessPID());
                bw.close();
            } catch(IOException e) {
                msgErr("Failed to write to (temp) Pid_file!! Program: " + asProgName + " = "
                        + " PID: " + getProcessPID()
                        + "\n\tMsg.: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return objPidFile;
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
     * Reference:
     * -> https://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
     *
     * @return String
     */
    protected String getProcessPID() {
        String  sPID = "-1";

        // Note: may fail in some JVM implementations
        // therefore fallback has to be provided
        //
        // something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');

        //if (index < 1) {
            // part before '@' empty (index = 0) / '@' not found (index = -1)
        //}
        if (index > 0) {
            try {
                sPID = Long.toString(Long.parseLong(jvmName.substring(0, index)));
            } catch (NumberFormatException e) {
                // ignore
                msgErr("PID could not be retrieved! Msg.: " + e.getMessage());
            }
        }
        return sPID;
    }

    protected String getProcessHostMX() {
        String  sHostMX = "/";

        // something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');

        if (index > 0) sHostMX = jvmName.substring(index);
        else sHostMX = jvmName;
        return sHostMX;
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
                if (UtilString.isEmpty(sTemp)) sTemp = atts.getValue(MANIFEST_KEY_CAPSULE_VER);
            }
        }
        if (UtilString.isEmpty(sTemp)) sTemp = "/";
        //System.out.println("\tVersion text extracted: " + sTemp);
        if (sTemp.contains(":")) {
            String[] arrVersion = sTemp.split(":");
            sTemp = arrVersion[arrVersion.length - 1];
        }
        if (sTemp.contains(".")) {
            String[] arrVersion = sTemp.split("\\.");
            //System.out.println("\tVersion num extracted: " + arrVersion.length);
            GlobalVar.getInstance().sVersionMax = arrVersion[0];
            if (arrVersion.length > 0) GlobalVar.getInstance().sVersionMin = arrVersion[1].trim();
            if (arrVersion.length > 1) GlobalVar.getInstance().sVersionPatch = arrVersion[2].trim();
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
            msgInfo("Java (runtime) version: " + sJavaVersion
                    + "; PID: " + getProcessPID()
                    + "; HostMX: " + getProcessHostMX());
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

        // Enable ShutdownHook
        if (bShouldEnableShutdownHook) {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    int     iResultTemp;
                    String  sTemp;

                    iResultTemp = runShutdownHook();
                    // Error
                    if (iResultTemp != ConstGlobal.RETURN_OK) {
                        sTemp = "invokeApp(): Error at runShutdownHook() operation!";
                        logger.severe(sTemp);
                        msgWarn(sTemp);
                        //iResult = ConstGlobal.PROCESS_EXIT_FAILURE;
                    }
                    msgInfo("invokeApp(Thread): Shouting down (final) ..");
                }
            });
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
     * Method: runShutdownHook
     *
     * Run/Initiate Shutdown procedure.
     *
     * @return int	1 = AllOK;
     */
    protected int runShutdownHook() {
        bIsShutdownInitiated = true;
        msgInfo("runShutdownHook(): Shouting down initiated ..");
        //bIsShutdownReady2Stop = true;
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
        //long        iCountLoop = 0L;
        long        iCountDataAll = 0L;
        Date        dtStart;
        Date        dtStartLoop;
        Date        dtStop;
        RefDataInteger objRefCountData;

        // Initialization
        iResult = ConstGlobal.RETURN_SUCCESS;
        dtStart = new Date();
        //dtStartLoop = new Date();
        objRefCountData = new RefDataInteger();
        if (GlobalVar.bIsModeVerbose) {
            logger.info("runInLoop(): =-> Start running in Loop - iMaxNumOfLoops: " + iMaxNumOfLoops + " --==");
        }

        // Process data ..
        //
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            do {
                int     iResultTemp;
                String  sTemp;

                dtStartLoop = new Date();

                if (GlobalVar.bIsModeVerbose) {
                    logger.info("runInLoop(): =-> Loop count: " + objRefCountData.iCountLoop + " ---===");
                }
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
                iCountDataAll += objRefCountData.iCountData;

                objRefCountData.iCountLoop++;
                if (iMaxNumOfLoops > 0) {
                    if (iMaxNumOfLoops <= (objRefCountData.iCountLoop - 0)) {
                        logger.info("runInLoop(): Maximum number of loops reached: " + iMaxNumOfLoops);
                        break;
                    }
                }

                // Check previous step
                if (iResult == ConstGlobal.RETURN_OK) {
                    if (iMaxNumOfLoops != 1L) {
                        StringBuilder   sSleep = new StringBuilder();
                        Date            dtStopLoop = new Date();

                        if (UtilString.isEmptyTrim(GlobalVar.getInstance().sProgName)) {
                            sSleep.append("runInLoop()");
                        } else {
                            sSleep.append(String.format("%22.22s", GlobalVar.getInstance().sProgName));
                        }
                        sSleep.append(": Sleep ..");
                        sSleep.append(" -> #Loop: ").append(String.format("%05d", objRefCountData.iCountLoop));
                        sSleep.append("\tTime: ").append(UtilDate.toUniversalString(dtStopLoop));
                        sSleep.append("\tElapse(ms): ").append(String.format("%05d", dtStopLoop.getTime() - dtStartLoop.getTime()));
                        if (bShouldWriteLoopInfo2stdOut)
                            System.out.println(sSleep.toString());
                        if (bShouldWriteLoopInfo2log)
                            logger.info("runInLoop(): " + sSleep.toString());
                        if (iPauseBetweenLoop != 0) {
                            try { // Pause for ? second(s)
                                Thread.sleep(iPauseBetweenLoop);
                            } catch (Exception ex) {
                                iResult = ConstGlobal.RETURN_ENDOFDATA;
                                logger.severe("runInLoop: Interrupt exception!!"
                                        + " Msg.: " + ex.getMessage());
                            }
                        }
                    }
                }
            } while (iResult == ConstGlobal.RETURN_OK);
        }

        dtStop = new Date();
        logger.info("runInLoop(): Processing done."
                + "\n\tData num.: " + objRefCountData.iCountData + "/" + iCountDataAll
                + "\tLoop#: " + objRefCountData.iCountLoop
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
