/**
 * $Id: ExampleApp.java 17360 2008-06-12 13:02:09Z bfg $
 * $URL: https://svn.interseek.com/repositories/modules/javaapp/trunk/src/main/java/com/interseek/javaapp/ExampleApp.java $
 * $Date: 2008-06-12 15:02:09 +0200 (Thu, 12 Jun 2008) $
 * $Author: bfg $
 * $Revision: 17360 $
 * $LastChangedRevision: 17360 $
 * $LastChangedBy: bfg $
 * $LastChangedDate: 2008-06-12 15:02:09 +0200 (Thu, 12 Jun 2008) $
 */
package com.interseek.javaapp;

/**
 * @author bfg
 * @version $Id: ExampleApp.java 17360 2008-06-12 13:02:09Z bfg $
 * @see StandaloneApp
 */

import org.apache.commons.cli.*;

public class ExampleApp extends StandaloneApp {
	
	/**
	 * Object constructor.
	 */
	ExampleApp () {
		/**
		 * Let's tell something about ourselves...
		 */
		programVersion = "0.49";
		programShortUsage = "[OPTIONS] <loop_count> <loop_delay_in_ms>";
		programUsageExample = "ExampleApp 100000 50\n" + "    Program performs 100000 loops with delay of 50 miliseconds.";
		programDescription = "This is very cool program\n" +
				"just looops and loops.";
		
	}

	/**
	 * Main method. Body of this method ***must*** be always the same:
	 * <pre>
	 * public static void main(String[] args) {
	 *		ExampleApp self = new ExampleApp();
	 *		self.invokeApp(args);
	 * }
	 * </pre>
	 * @param args command line arguments (unfiltered)
	 */
	public static void main(String[] args) {
		ExampleApp self = new ExampleApp();
		self.invokeApp(args);
	}
	
	/**
	 * Real "main" method.
	 * @param args filtered, command line switches and their arguments free args array
	 * @param cmdl command line object...
	 */
	public void startup (String[] args, CommandLine cmdl) {
		System.out.println(TERM_LGREEN + "Tole je v zeleni" + TERM_RESET);
		System.out.println(TERM_LRED + "Tole pa v rdeci barvi..." + TERM_RESET);
		System.out.println("Ampak, itak se ve, da pisat na " + TERM_YELLOW + "System.out " + TERM_RESET + TERM_BOLD + "NI KUL SPLOH!" + TERM_RESET);
		
		logger.info("info.");
		logger.warn("warning.");
		logger.error("error.");
		logger.fatal("fatal error.");
		logger.debug("debug message.");
		logger.trace("trace message.");
		
		if (logger.isDebugEnabled()) {
			logger.debug("This is how i write lots of data only when debugging is enabled.");
		}
		if (logger.isTraceEnabled()) {
			logger.trace("This is how i write even more data only when trace is enabled.");
		}
		
		msgInfo("I'm just a nice notice.");
		msgWarn("I'm a warning message.");
		msgErr("I'm an error message.");

		// msgFatal("I'm a fatal show stopper, therefore i'm going to kill JVM with error code 1.");
		/**
		 * The rest of your program goes here...
		 */
		System.out.println("My args[] goes here:");
		for (int i = 0; i < args.length; i++) {
			System.out.println("args[" + i + "]: '" + args[i] + "'");			
		}
		
		msgInfo("Fetching command line arguments.");
		if (cmdl.hasOption("option-y"))
			System.out.println("Command line option --option-y = " + cmdl.getOptionValue("option-y"));
		else
			System.out.println("Command line doesn't contain --option-y");
		
		// this.programConfiguration contains program configuration...
		
		msgInfo("Now i'm really gonna do something.");
		
		if (args.length < 2)
			msgFatal("Invalid command line arguments: missing loop count or delay.");
		
		int count = 0;
		long delay = 0;
		try {
			count = Integer.parseInt(args[0]);
			delay = Long.parseLong(args[1]);
		}
		catch (Exception e) {
			msgFatal("Invalid loop or delay arguments - not a number: " + e.getMessage());
		}
		
		// fire it up...
		reallyDoSomething(count, delay);
	}
	
	public void reallyDoSomething (int count, long delay) {
		for (int i = 0; i < count; i++) {
			logger.debug("Entering loop no: " + i);
			if (i % 10 == 0)
				logger.info("Doing loop no: " + i);

			try {
				Thread.sleep(delay);
			}
			catch (Exception e) {}
		}
	}
	
	/**
	 * You can and you're encouraged to implement this method.
	 * In this method you specify your own command line switches.
	 * @param Options object
	 */
	protected void getAppCmdlOptions (Options o) {
		// example options
		o.addOption("x", "option-x", false, "This option doesn't require argument");
		o.addOption("y", "option-y", true, "This option requires argument.");
		o.addOption(null, "long-opt-only", false, "This is long option only.");
		o.addOption("z", false, "This is short option only.");
	}
}
