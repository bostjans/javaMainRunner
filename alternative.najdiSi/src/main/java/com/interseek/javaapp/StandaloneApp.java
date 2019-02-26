/**
 * $Id: StandaloneApp.java 17360 2008-06-12 13:02:09Z bfg $
 * $URL: https://svn.interseek.com/repositories/modules/javaapp/trunk/src/main/java/com/interseek/javaapp/StandaloneApp.java $
 * $Date: 2008-06-12 15:02:09 +0200 (Thu, 12 Jun 2008) $
 * $Author: bfg $
 * $Revision: 17360 $
 * $LastChangedRevision: 17360 $
 * $LastChangedBy: bfg $
 * $LastChangedDate: 2008-06-12 15:02:09 +0200 (Thu, 12 Jun 2008) $
 */
package com.interseek.javaapp;

// standard java library
import java.io.*;
import java.util.*;

// command line parsing...
import org.apache.commons.cli.*;

// log quattro j
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * This class implements nice and easy way to create
 * user-friendly standalone java application for you.
 * It initializes log4j for you and parses command line arguments.
 * 
 * Usage:
 * <pre>
 * import com.interseek.javaapp.*;
 * import org.apache.commons.cli.*;
 * 
 * public class MyApp extends StandaloneApp {
 *
 *	MyApp () {
 *		programVersion = "0.6";
 *		programShortUsage = "[OPTIONS] file file ...";
 *		programUsageExample = "MyApp /path/to/file1 /path/to/file2";
 *		programDescription = "This is very cool program\n" +
 *				"that can do almost everything\n" + 
 *				"you can imagine.";
 *		
 *	}
 * 	public static void main (String args[]) {
 * 		MyApp self = new MyApp();
 * 		self.invokeApp(args);
 * 	}
 * 	public static void startup (String args[]) {
 * 		System.out.println("This is what was long time ago called public static void main (String[] args) {}");
 * 
 * 		// fetch some command line arguments
 * 		if (cmdl.hasOption("option-y"))
 *			System.out.println("Command line option --option-y = " + cmdl.getOptionValue("option-y"));
 *		else
 *			System.out.println("Command line doesn't contain --option-y");
 * 	}
 *  
 * 	// specify your own command line options...
 * 	protected void getAppCmdlOptions (Options o) {
 *		// example options
 *		o.addOption("x", "option-x", false, "This option doesn't require argument");
 *		o.addOption("y", "option-y", true, "This option requires argument.");
 *		o.addOption(null, "long-opt-only", false, "This is long option only.");
 *		o.addOption("z", false, "This is short option only.");
 *	}
 * 
 * </pre>
 * @author $Author: bfg $
 * @version $Revision: 17360 $
 * @see ExampleApp
 */
public class StandaloneApp {
	/**
	 * Log4j logging object.
	 * It is very important to know that this object is initialized
	 * <b>AFTER</b> command line has been parsed, therefore you cannot
	 * use it in your own app object contructor.
	 * @see ExampleApp for example
	 */
	protected Logger logger = null;

	/**
	 * terminal escape sequence (starts white text)
	 */
	public String TERM_WHITE = "\033[1;37m";
	/**
	 * terminal escape sequence (starts yellow text)
	 */
	public String TERM_YELLOW = "\033[1;33m";
	/**
	 * terminal escape sequence (starts purple text)
	 */
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
	/**
	 * terminal escape sequence (starts bolded text)
	 */
	public String TERM_BOLD = "\033[40m\033[1;37m";
	/**
	 * terminal escape sequence (ends coloured text)
	 */
	public String TERM_RESET = "\033[0m";

	/**
	 * Describes program usage in short term.
	 * @see printHelp() method.
	 * @see ExampleApp see usage example.
	 */
	protected String programShortUsage = "[OPTIONS] :: !!!change this property!!!";
	protected String programDescription = "This is program description.\n!!! CHANGE THIS PROPERTY IN YOUR OBJECT CONSTRUCTOR !!!";
	protected String programUsageExample = "This is usage example.\n!!! CHANGE THIS PROPERTY IN YOUR OBJECT CONSTRUCTOR !!!";
	protected String programVersion = "$Revision: 17360 $";
	protected Properties programConfiguration = new Properties();
	
	// private stuff
	private int log4jReloadTime = 3;

	/**
	 * Object constructor
	 */
	public StandaloneApp ()  {

		// are standard streams connected to terminal?
		// we need jvm 1.6.x to figure this out...
		double java_version = 0;
		try {
			java_version = Double.parseDouble(System.getProperty("java.runtime.version").substring(0, 3));
		}
		catch (Exception e) {}

		if (java_version > 1.5 && System.console() == null) {
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
	 * Returns built-in default log4j logging configuration
	 * @return log4j built-in standard log4j configuration 
	 */
	protected Properties getLoggingPropertiesDefault () {
		Properties p = new Properties();
		p.setProperty("log4j.rootLogger", "INFO,out");
		p.setProperty("log4j.appender.out", "org.apache.log4j.ConsoleAppender");
		p.setProperty("log4j.appender.out.layout", "org.apache.log4j.PatternLayout");
		p.setProperty("log4j.appender.out.layout.conversionPattern", "[%d{yyyy/MM/dd HH:mm:ss.SSS}] %-5p: %m%n");
		
		return p;
	}

	/**
	 * Returns built-in default debug log4j logging configuration
	 * @return log4j built-in debug configuration 
	 */
	protected Properties getLoggingPropertiesDefaultDebug () {
		Properties p = new Properties();
		p.setProperty("log4j.rootLogger", "ALL,out");
		p.setProperty("log4j.appender.out", "org.apache.log4j.ConsoleAppender");
		p.setProperty("log4j.appender.out.target", "System.err");
		p.setProperty("log4j.appender.out.layout", "org.apache.log4j.PatternLayout");
		p.setProperty("log4j.appender.out.layout.conversionPattern", "[%d{yyyy/MM/dd HH:mm:ss.SSS}] %-5p: %l: %m%n");

		return p;
	}

	/**
	 * main :: main invocation method
	 * @param args command line arguments
	 */
	public static void main (String args[]) {
		StandaloneApp self = new StandaloneApp();
		self.invokeApp(args);		
	}
	
	public void invokeApp (String args[]){
		
		String log_config_file = null;
		int log_reload_time = log4jReloadTime;
		boolean log_debug = false;
		
		/** parse command line */
		CommandLineParser parser = new GnuParser();
		Options cmdlOpts = getCmdlOptions();
		CommandLine line = null;
		try {
			line = parser.parse(cmdlOpts, args);
			
			if (line.hasOption("system-info")) {
				printSystemInfo();
				System.exit(0);
			}
			// should we print version?
			if (line.hasOption("version")) {
				System.out.printf("%s %s\n", this.getClass().getName(), programVersion);
				System.exit(0);
			}
			// help message?
			if (line.hasOption("help")) {
				printHelp(cmdlOpts);
				System.exit(0);
			}
			if (line.hasOption("log-standard-conf")) {
				getLoggingPropertiesDefault().list(System.out);
				System.exit(0);
			}
			if (line.hasOption("log-debug-conf")) {
				getLoggingPropertiesDefaultDebug().list(System.out);
				System.exit(0);
			}

		    // load configuration?
			if (line.hasOption("config")) {
				String file = line.getOptionValue("config");
				File f = new File(file);
				if (! f.exists() || ! f.isFile() || ! f.canRead())
					msgFatal("Invalid, non-existing or unreadable configuration properties file: " + file);
				
				// load it
				try {
					FileInputStream fis = new FileInputStream(f);
					programConfiguration.load(fis);
				}
				catch (Exception e) {
					msgFatal("Error loading configuration properties file " + file + ": " + e.getMessage());
				}
			}
			// override single property?
			if (line.hasOption("override-property")) {
				String str = line.getOptionValue("override-property");
				String chunks[] = str.split("=");
				if (chunks.length < 2) {
					System.err.println("Invalid property specification. Syntax: prop.name=value");
					System.err.println("Run " + this.getClass().getName() + " --help for instructions");
					System.exit(1);
				}
				programConfiguration.setProperty(chunks[0], chunks[1]);
			}
			
			// logging
			if (line.hasOption("debug")) {
				log_debug = true;
			}
			if (line.hasOption("debug-log4j")) {
				System.setProperty("log4j.debug", "");
			}
			if (line.hasOption("log-config")) {
				log_config_file = line.getOptionValue("log-config");
			}
			if (line.hasOption("log-watch")) {
				log_reload_time = Integer.parseInt(line.getOptionValue("log-watch")) * 1000;
			}
		}
		catch (ParseException exp) {
		    msgFatal("Error parsing command line: " + exp.getMessage());
		}
		
		/** configure logging subsystem */
		configureLogger(log_config_file, log_reload_time, log_debug);
		logger = Logger.getLogger(this.getClass().getName());
		logger.debug("Startup, logging subsystem configured.");

		// normalize command line
		args = this.normalizeCommandLine(args, cmdlOpts);

		// fire up the real startup method!
		startup(args, line);
	}
	
	/**
	 * This method is invoked inside invokeApp() method.
	 * You are encouraged to overload this method in your own
	 * application class to specify your own command line options
	 * @param o Empty Options object
	 */
	protected void getAppCmdlOptions (Options o) {	
	}
	
	/**
	 * This method can be overloaded in your own application class
	 * in case you want to override standard command line switches.
	 * @param o Empty Options object
	 */
	protected void overrideStdCmdlOptions (Options o) {
	}
	
	/**
	 * Returns command line options suitable for parsing commandline using Parser interface
	 * @return Options command line options
	 */
	private Options getCmdlOptions () {
		// all options
		Options stdopt = new Options();
		//
		stdopt.addOption("c", "config", true, "Load program configuration (properties file)");
		stdopt.addOption(null, "override-property", true, "Overrides properties file property (Syntax: property.name=value)");
		stdopt.addOption(null, "system-info", false, "Prints out system info.");
		stdopt.addOption("h", "help", false, "This help message.");
		stdopt.addOption("V", "version", false, "Prints program version.");

		
		// logging options
		Options logopt = new Options();
		logopt.addOption(
				"L", "log-config", true,
				"Use specified log4j configuration file (Default: none).\n" +
				"File can be in properties or xml format\n" +
				TERM_BOLD + "NOTE: " + TERM_RESET +
				"Default built-in log4j configuration is used if this option is omitted.\n" +
				TERM_YELLOW + "WARNING: " + TERM_RESET +
				"This option overrides --debug option."
			);
		logopt.addOption(null, "debug", false, "Use built-in log4j debugging configuration.");
		logopt.addOption(null, "debug-log4j", false, "Turn on log4j logging system debugging.");
		logopt.addOption(
				null, "log-watch", true,
				"Specifies time interval in seconds for reloading logging configuration (Default: " + log4jReloadTime + ")\n" +
				TERM_BOLD + "NOTE: " + TERM_RESET +
				"This option is only effective in conjuction with --log-config option.\n" +
				TERM_BOLD + "NOTE: " + TERM_RESET +
				"Set this to 0 if you don't want program to check for logging configuration changes."
			);
		logopt.addOption(null, "log-standard-conf", false, "Prints starndard logging configuration");
		logopt.addOption(null, "log-debug-conf", false, "Prints debug logging configuration");
		
		// application specific options
		Options appopt = new Options();
		getAppCmdlOptions(appopt);
		
		// override options...
		Options oopt = new Options();
		overrideStdCmdlOptions(oopt);

		// join all options together...
		Options o = new Options();
		appendOptions(o, appopt);
		appendOptions(o, logopt);
		appendOptions(o, stdopt);
		appendOptions(o, oopt);

		return o;
	}

	private void configureLogger (String log_config, int log_reload_time, boolean debug) {
		// check for log configuration file...
		if (log_config != null && log_config.length() > 0) {
			File f = new File(log_config);
			if (! f.exists() || ! f.isFile() || ! f.canRead())
				msgFatal("Invalid, non-existing or unreadable logging configuration file: " + log_config);

			// if we were fed with xml file,
			// we can use advanced features of dom log4j configurator...
			if (log_config.toLowerCase().endsWith(".xml")) {
				if (log_reload_time > 0)
					DOMConfigurator.configureAndWatch(log_config, (log_reload_time * 1000));
				else
					DOMConfigurator.configure(log_config);
			} else {
				if (log_reload_time > 0)
					PropertyConfigurator.configureAndWatch(log_config, (log_reload_time * 1000));
				else
					PropertyConfigurator.configure(log_config);
			}

			if (! isLoggingConfigured()) {
				msgWarn("Log4j configuration failed, configuring with built-in standard configuration.");
				msgWarn("Run program with --debug-log4j switch to find out what went wrong with your logging configuration.");
				PropertyConfigurator.configure(getLoggingPropertiesDefault());
			}
		} else {
			// configure logging using built-in configuration
			Properties p = (debug) ? getLoggingPropertiesDefaultDebug() : getLoggingPropertiesDefault();
			// msgInfo("using built-in logging props...");
			PropertyConfigurator.configure(p);
		}
	}
	
	/**
	 * Appends command line options do existing command line options.
	 * @param a destination command line options
	 * @param b source command line options
	 */
	@SuppressWarnings("unchecked")
	private void appendOptions (Options a, Options b) {
		if (a != null && b != null) {
			Collection c = b.getOptions();
			Option[] oa = (Option[]) c.toArray(new Option [c.size()]);
			for (int i = 0; i < oa.length; i++)
				a.addOption(oa[i]);
		}
	}

	/**
	 * Real startup method (replacement for main() method).
	 *
	 * This method is called *AFTER* command line has already
	 * been parsed and *AFTER* logging subsystem has been initialized.
	 *
	 * @param args filtered, command line switches free command line array
	 * @param args CommandLine object holding parsed command line
	 */
	public void startup (String args[], CommandLine cmdl) {
		logger.fatal("Subclasses must overload startup() method. THIS IS A FATAL BUG!!!");
		System.err.println("Invalid class usage. Run " + this.getClass().getName() + " --help for instructions.");
		System.exit(1);
	}
	
	public void printSystemInfo () {
		printSystemInfo(null);
	}
	
	public void printSystemInfo (OutputStream s) {
		if (s == null)
			s = System.out;
		PrintStream out = new PrintStream(s);
		out.println("date: " + new Date());
		System.getProperties().list(out);
	}
	
	public void printHelp (Options o) {
		printHelp(o, null);
	}
	
	public void printHelp (Options o, OutputStream s) {
		if (s == null)
			s = System.out;
		PrintStream out = new PrintStream(s);

		out.println(TERM_BOLD + "Usage: " + TERM_RESET + TERM_LGREEN + this.getClass().getName() + TERM_RESET + " " + programShortUsage);
		out.println();
		out.println(programDescription);
		out.println();
		
		/**
		out.println();
		out.println(TERM_BOLD + "OPTIONS:" + TERM_RESET);
		
		out.println();
		out.println(TERM_BOLD + "LOGGING OPTIONS:" + TERM_RESET);
		*/

		out.println();
		out.println(TERM_BOLD + "OPTIONS:" + TERM_RESET);
		printOptions(o, out);
		
		if (programUsageExample != null && programUsageExample.length() > 0) {
			out.println();
			out.println(TERM_BOLD + "USAGE EXAMPLE:" + TERM_RESET);
			out.println(programUsageExample);
		}
	}

	@SuppressWarnings("unchecked")
	public void printOptions (Options o, OutputStream s) {
		if (s == null)
			s = System.out;
		PrintStream out = new PrintStream(s);
		
		if (o == null) {
			logger.fatal("Options o == null. THIS IS BUG!!!");
			System.exit(1);
		}

		Collection c = o.getOptions();
		Option[] oa = (Option[]) c.toArray(new Option [c.size()]);
		for (int i = 0; i < oa.length; i++) {
			String shortopt = oa[i].getOpt();
			String longopt = oa[i].getLongOpt();
			String desc = oa[i].getDescription();
			//boolean wantsOpt = oa[i].hasArg();
			if (shortopt == null)
				shortopt = "";
			else
				shortopt = "-" + shortopt;

			if (longopt == null)
				longopt = "";
			else
				longopt = "--" + longopt;

			if (desc == null)
				desc = "";

			// check for multiline option comment
			String descArr[] = desc.split("[\\r\\n]+");
			out.printf("  %-2.2s    %-20.20s %s\n", shortopt, longopt, descArr[0]);
			if (descArr.length > 1) {
				for (int j = 1; j < descArr.length; j++)
					out.println("                             " + descArr[j]);
			}
		}
	}

	/** 
	 * Returns true if it appears that log4j have been previously configured. This code 
	 * checks to see if there are any appenders defined for log4j which is the 
	 * definitive way to tell if log4j is already initialized
	 */
	@SuppressWarnings("unchecked")
	protected static boolean isLoggingConfigured() {
			Enumeration appenders = LogManager.getRootLogger().getAllAppenders();
			if (appenders.hasMoreElements()) {
				return true;
			} else {
				Enumeration loggers = LogManager.getCurrentLoggers();
				while (loggers.hasMoreElements()) {
					Logger c = (Logger) loggers.nextElement();
					if (c.getAllAppenders().hasMoreElements())
						return true;
				} 
			}
			return false; 
	}
	
	/**
	 * Weeds out command line switches from command line argument array...
	 * 
	 * @param args full command line argument array
	 * @return normalized, command line switch free argument array 
	 */
	@SuppressWarnings("unchecked")
	private String[] normalizeCommandLine (String args[], Options o) {
		// String result[] = {};
		List list = new ArrayList();
		logger.debug("Normalizing command line array.");
		
		//int j = 0;
		//boolean prevWasSwitch = false;
		boolean foundEOO = false;
		boolean requires_arg = false;

		// cleanup array...
		for (int i = 0; i < args.length; i++) {
			String chunk = args[i];

			// end of options? put everything into result
			if (foundEOO) {
				list.add(chunk);
				continue;
			}

			boolean is_long_opt = chunk.startsWith("--");
			boolean is_short_opt = chunk.startsWith("-");
			
			// if this is an option, we need to determine
			// if it requires an argument...
			if (is_short_opt || is_long_opt) {
				String opt = "";
				if (is_short_opt)
					opt = chunk.substring(1);
				else if (is_long_opt)
					opt = chunk.substring(2);
				
				if (opt.equals("")) {
					list.add("-");
					continue;
				}
				
				Option x = o.getOption(opt);
				requires_arg = (x != null && x.hasArg());
				continue;
			}
			else {
				// not an option? check if previous option
				// requires an argument
				if (requires_arg) {
					requires_arg = false;
					continue;
				}
				requires_arg = false;
				list.add(chunk);
			}
		}

		// create result array...
		String result[] = (String[]) list.toArray(new String [list.size()]);

		if (logger.isDebugEnabled()) {
			String str = "";
			for (int i = 0; i < args.length; i++) {
				if (i != 0) str += ", ";
				str += "'" + args[i] + "'";
			}
			logger.debug("Original command line array: " + str);
			
			str = "";
			for (int i = 0; i < result.length; i++) {
				if (i != 0) str += ", ";
				str += "'" + result[i] + "'";
			}
			logger.debug("Normalized command line array: " + str);

			logger.debug("Normalization finished. Returning result.");
		}
		
		return result;
	}
	
	/**
	 * Prints info message to stdout
	 * @param s message
	 */
	protected void msgInfo (String s) {
		if (s != null){
			System.out.println(TERM_BOLD + "INFO:    " + TERM_RESET + s);
			//if (logger != null)
			//	logger.info(s);
		}
	}
	
	/**
	 * Prints warning message to stderr
	 * @param s message
	 */
	protected void msgWarn (String s) {
		if (s != null) {
			System.err.println(TERM_YELLOW + "WARNING: " + TERM_RESET + s);
			//if (logger != null)
			//	logger.warn(s);
		}
	}
	
	/**
	 * Prints error message to stderr
	 * @param s message
	 */
	protected void msgErr (String s) {
		if (s != null) {
			System.err.println(TERM_LRED + "ERROR:   " + TERM_RESET + s);
			//if (logger != null)
			//	logger.error(s);
		}
	}
	
	/**
	 * Prints out fatal error message to stderr and exits application with error code 1
	 * @param s message
	 */
	protected void msgFatal (String s) {
		msgFatal(s, true);
	}
	
	/**
	 * Prints out fatal error message to stderr and possibly exits application with error code 1
	 * @param s message
	 * @param exit if true, exit the application
	 */
	protected void msgFatal (String s, boolean exit) {
		if (s != null) {
			System.err.println(TERM_LRED + "FATAL:   " + TERM_RESET + s);
			//if (logger != null)
			//	logger.fatal(s);
		}
		
		if (exit)
			System.exit(1);
	}
}
