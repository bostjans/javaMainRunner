package com.stupica.processRunner;


import com.stupica.GlobalVar;
import com.stupica.core.UtilDate;
import com.stupica.core.UtilString;

import java.util.Date;


public class ProcessCore {

    /**
     * Flag: is program/process running in loops?
     */
    public boolean bIsProcessInLoop = false;
    protected long iMaxNumOfLoops = 1;
    protected int  iPauseBetweenLoop = 1000 * 4;    // 4 sec .. is default;
    protected int  iPauseAtStart = 0;               // Pause before (actual) start processing;

    /**
     * Flag: should write loop information to StdOut?
     * Sample:
     *    programName: 1 ->Loop#: 07684 >Time: 2022-01-15_10:50:51 >Elapse(ms): 11444 &Sleep ..
     */
    protected boolean bShouldWriteLoopInfo2stdOut = true;
    /**
     * Flag: should write loop information to log?
     */
    protected boolean bShouldWriteLoopInfo2log = false;
    /**
     * Frequency for "write loop info"
     */
    protected int iWriteLoopInfoFrequency = 1;

    /**
     * Flag: should ignore all error(s) in Loop cycle?
     */
    protected boolean bShouldIgnoreLoopError = false;


    // inner class
    public class RefDataInteger {
        public long iCountLoop = 0L;
        public int  iCountLoopFreq = 0;
        public int  iCountData = 0;
    }


    public void setMaxNumOfLoops(long aiVal) {
        iMaxNumOfLoops = aiVal;
    }
    public void setPauseBetweenLoop(int aiVal) {
        iPauseBetweenLoop = aiVal;
    }

    protected String loopInfoText(String asProcName, String asFormat, int aiInstanceNum, long aiCountLoop,
                                  long adtStartLoop, long adtStopLoop, boolean abShouldWait) {
        String  sResult;

        sResult = loopInfoText(asProcName, asFormat, aiInstanceNum, aiCountLoop, adtStartLoop, adtStopLoop);
        sResult += " >shouldWait: " + abShouldWait;
        return sResult;
    }
    protected String loopInfoText(String asProcName, String asFormat, int aiInstanceNum, long aiCountLoop, long adtStartLoop, long adtStopLoop) {
        StringBuilder   sSleep = new StringBuilder();

        if (UtilString.isEmptyTrim(asProcName))
            sSleep.append("runInLoop()");
        else
            sSleep.append(String.format(asFormat, asProcName));
        if (aiInstanceNum >= 0)
            sSleep.append(":").append(String.format("%2d", aiInstanceNum));
        sSleep.append(" ->Loop#: ").append(String.format("%05d", aiCountLoop));
        sSleep.append(" >Time: ").append(UtilDate.toUniversalString(new Date(adtStopLoop)));
        sSleep.append(" >Elapse(ms): ").append(String.format("%05d", adtStopLoop - adtStartLoop));
        sSleep.append(" &Sleep ..");
        return sSleep.toString();
    }
    protected String loopInfoText(int aiInstanceNum, long aiCountLoop, long adtStartLoop, long adtStopLoop) {
        if (UtilString.isEmptyTrim(GlobalVar.getInstance().sProgName)) {
            return loopInfoText("runInLoop()", "%22.22s", aiInstanceNum, aiCountLoop, adtStartLoop, adtStopLoop);
        } else {
            return loopInfoText(GlobalVar.getInstance().sProgName, "%22.22s", aiInstanceNum, aiCountLoop, adtStartLoop, adtStopLoop);
        }
    }
}
