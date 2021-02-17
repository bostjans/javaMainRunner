package com.stupica.processRunner;


import com.stupica.ConstGlobal;
import com.stupica.GlobalVar;
import com.stupica.core.UtilCommon;
import com.stupica.core.UtilDate;
import com.stupica.core.UtilString;

import java.util.Date;
import java.util.logging.Logger;


public class ProcessBase {

    public boolean  bShouldStop = false;
    public boolean  bShouldWait = false;
    public boolean  bIsInThread = true;
    public boolean  bIsThreadDone = false;

    /**
     * Flag: is program/process running in loops?
     */
    public boolean bIsProcessInLoop = false;
    protected long iMaxNumOfLoops = 1;
    protected int  iPauseBetweenLoop = 1000 * 2;    // 2 sec .. is default;
    protected int  iPauseAtStart = 0;               // Pause before (actual) start processing;

    public long    iTimeElapsedStopLimit = 0;
    //long    iTimeElapsedStopLimit = 1000 * 60;          // 1 min
    //long    iTimeElapsedStopLimit = 1000 * 60 * 60;     // 1 hour

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

    public String   sProcessName = "ProcX";

    private static Logger logger = Logger.getLogger(ProcessBase.class.getName());


    // inner class
    public class RefDataInteger {
        public long iCountLoop = 0L;
        public int  iCountData = 0;
    }


    /**
     * Method: initialize
     *
     * ..
     */
    protected void initialize() {
    }


    /**
     * Method: run
     *
     * Run ..
     */
    public void run() {
        int         iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_SUCCESS;

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            initialize();
        }

        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            iResult = processBefore();
            // Error ..
            if (iResult != ConstGlobal.RETURN_OK) {
                logger.severe("run(" + sProcessName + "): Error at processBefore() operation!");
            }
        }
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            iResult = process();
            // Error ..
            if (iResult != ConstGlobal.RETURN_OK) {
                logger.severe("run(" + sProcessName + "): Error at process() operation!");
            }
        }
        // Check previous step
        if (iResult == ConstGlobal.RETURN_OK) {
            iResult = processAfter();
            // Error ..
            if (iResult != ConstGlobal.RETURN_OK) {
                logger.severe("run(" + sProcessName + "): Error at processAfter() operation!");
            }
        }
        //return iResult;
    }


    /**
     * Method: process
     *
     * ..
     *
     * @return int i_result	1 = AllOK;
     */
    public int process() {
        int         iResult;
        String      sTemp;

        // Initialization
        iResult = ConstGlobal.RETURN_SUCCESS;

        if (iPauseAtStart != 0) {
            UtilCommon.sleepFoxMillis(iPauseAtStart);
        }

        if (bIsProcessInLoop) {
            iResult = processLoopBefore();
            // Error
            if (iResult != ConstGlobal.RETURN_OK) {
                sTemp = "process(): Error at processLoopBefore() operation!";
                logger.severe(sTemp);
                System.err.println(sTemp);
            }
            // Check previous step
            if (iResult == ConstGlobal.RETURN_OK) {
                iResult = processInLoop();
                // Error
                if (iResult != ConstGlobal.RETURN_OK) {
                    sTemp = "process(): Error at processInLoop() operation!";
                    logger.severe(sTemp);
                    System.err.println(sTemp);
                }
            }
            // Check previous step
            if (iResult == ConstGlobal.RETURN_OK) {
                iResult = processLoopAfter();
                // Error
                if (iResult != ConstGlobal.RETURN_OK) {
                    sTemp = "process(): Error at processLoopAfter() operation!";
                    logger.severe(sTemp);
                    System.err.println(sTemp);
                }
            }
        }
        return iResult;
    }

    /**
     * Method: processBefore
     *
     * ..
     *
     * @return int i_result	1 = AllOK;
     */
    public int processBefore() {
        int         iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_SUCCESS;
        return iResult;
    }

    /**
     * Method: processAfter
     *
     * ..
     *
     * @return int i_result	1 = AllOK;
     */
    public int processAfter() {
        int         iResult;

        // Initialization
        iResult = ConstGlobal.RETURN_SUCCESS;
        return iResult;
    }


    /**
     * Method: processInLoop
     *
     * Process_in_Loop ..
     *
     * @return int	1 = AllOK;
     */
    protected int processInLoop() {
        // Local variables
        int         iResult;
        //
        long        iCountDataAll = 0L;
        Date        dtStart;
        Date        dtStartLoop;
        Date        dtStop;
        ProcessBase.RefDataInteger objRefCountData;

        // Initialization
        iResult = ConstGlobal.RETURN_SUCCESS;
        dtStart = new Date();
        objRefCountData = new ProcessBase.RefDataInteger();
        if (GlobalVar.bIsModeVerbose) {
            logger.info("processInLoop(" + sProcessName + "): =-> Start running in Loop - iMaxNumOfLoops: " + iMaxNumOfLoops
                    + "; iTimeElapsedStopLimit: " + iTimeElapsedStopLimit
                    + " --==");
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
                    logger.info("processInLoop(" + sProcessName + "): =-> Loop count: " + objRefCountData.iCountLoop + " ---===");
                }
                if (bShouldStop) {
                    logger.info("processInLoop(" + sProcessName + "): shouldStop: " + bShouldStop);
                    break;
                }
                // Check previous step
                //if (iResult == ConstGlobal.RETURN_OK) {
                    // Run ..
                    iResultTemp = processLoopCycle(objRefCountData);
                    // Error
                    if (iResultTemp != ConstGlobal.RETURN_OK) {
                        sTemp = "processInLoop(" + sProcessName + "): Error at processLoopCycle() operation!";
                        logger.severe(sTemp);
                        System.err.println(sTemp);
                        iResult = iResultTemp;
                    }
                //}
                iCountDataAll += objRefCountData.iCountData;

                objRefCountData.iCountLoop++;
                if (iMaxNumOfLoops > 0) {
                    if (iMaxNumOfLoops <= (objRefCountData.iCountLoop)) {
                        logger.info("processInLoop(" + sProcessName + "): Maximum number of loops reached: " + iMaxNumOfLoops);
                        break;
                    }
                }
                if (checkTimeElapsedStopLimit(dtStart)) {
                    logger.info("processInLoop(" + sProcessName + "): shouldStop: " + bShouldStop
                            + "; from checkTimeElapsedStopLimit(..);");
                    break;
                }
                if (bShouldStop) {
                    logger.info("processInLoop(" + sProcessName + "): shouldStop: " + bShouldStop);
                    break;
                }

                // Check previous step
                if (iResult == ConstGlobal.RETURN_OK) {
                    if (iMaxNumOfLoops != 1L) {
                        StringBuilder   sSleep = new StringBuilder();
                        Date            dtStopLoop = new Date();

                        if (objRefCountData.iCountLoop % 10 == 0) {
                            sSleep.append("-.-\n");
                        }
                        if (UtilString.isEmptyTrim(sProcessName)) {
                            sSleep.append("processInLoop()");
                        } else {
                            sSleep.append(String.format("%12.12s", sProcessName));
                        }
                        sSleep.append(": Sleep ..");
                        sSleep.append(" -> #Loop: ").append(String.format("%05d", objRefCountData.iCountLoop));
                        sSleep.append("\tTime: ").append(UtilDate.toUniversalString(dtStopLoop));
                        sSleep.append("\tElapse(ms): ").append(String.format("%05d", dtStopLoop.getTime() - dtStartLoop.getTime()));
                        //sSleep.append("\tshouldStop: ").append(bShouldStop);
                        sSleep.append("\tshouldWait: ").append(bShouldWait);
                        if (bShouldWriteLoopInfo2stdOut)
                            System.out.println(sSleep.toString());
                        if (bShouldWriteLoopInfo2log)
                            logger.info("processInLoop(" + sProcessName + "): " + sSleep.toString());
                        if ((iPauseBetweenLoop != 0) && (!bShouldWait)) {
                            iResult = UtilCommon.sleepFoxMillis(iPauseBetweenLoop);   // Pause for ? second(s)
                        }
                        if (bShouldWait) {
                            bShouldWait = false;
                            try {
                                synchronized(this) {
                                    wait();
                                }
                            } catch (IllegalMonitorStateException ex) {
                                bShouldStop = true;
                                logger.severe("processInLoop(" + sProcessName + "): Wait NOT possible! -> Terminate .."
                                        + " Msg.: " + ex.getMessage());
                                if (GlobalVar.bIsModeVerbose) {
                                    ex.printStackTrace();
                                }
                            } catch (InterruptedException ex) {
                                bShouldStop = true;
                                logger.warning("processInLoop(" + sProcessName + "): Wait was interrupted!"
                                        + " Msg.: " + ex.getMessage());
                            }
                        }
                    }
                }
            } while ((iResult == ConstGlobal.RETURN_OK) && (!bShouldStop));
        }

        dtStop = new Date();
        logger.info("processInLoop(" + sProcessName + "): Processing done."
                + "\n\tData num.: " + objRefCountData.iCountData + "/" + iCountDataAll
                + "\tLoop#: " + objRefCountData.iCountLoop
                + "\t\tDuration(ms): " + (dtStop.getTime() - dtStart.getTime()));
        return iResult;
    }

    /**
     * Method: processLoopBefore
     *
     * Run_Loop_cycle_before ..
     *
     * @return int	1 = AllOK;
     */
    protected int processLoopBefore() {
        return ConstGlobal.RETURN_OK;
    }

    /**
     * Method: processLoopAfter
     *
     * Run_Loop_cycle_before ..
     *
     * @return int	1 = AllOK;
     */
    protected int processLoopAfter() {
        return ConstGlobal.RETURN_OK;
    }


    /**
     * Method: processLoopCycle
     *
     * Process_Loop_cycle ..
     *
     * @return int	1 = AllOK;
     */
    protected int processLoopCycle(ProcessBase.RefDataInteger aobjRefCountData) {
        aobjRefCountData.iCountData = 0;
        return ConstGlobal.RETURN_OK;
    }


    protected boolean checkTimeElapsedStopLimit(Date adtStart) {
        long        iTimeElapsed = 0L;
        Date        dtStop;

        if (iTimeElapsedStopLimit != 0) {
            dtStop = new Date();
            iTimeElapsed = dtStop.getTime() - adtStart.getTime();
            if (iTimeElapsed > iTimeElapsedStopLimit) {
                logger.warning("checkTimeElapsedStopLimit(" + sProcessName + "): Stop Time limit reached!"
                        + " iTimeElapsedStopLimit: " + iTimeElapsedStopLimit);
                bShouldStop = true;
            }
        }
        return bShouldStop;
    }
}
