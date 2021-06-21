package com.stupica.processRunner;


public class ProcessCore {

    /**
     * Flag: is program/process running in loops?
     */
    public boolean bIsProcessInLoop = false;
    protected long iMaxNumOfLoops = 1;
    protected int  iPauseBetweenLoop = 1000 * 2;    // 2 sec .. is default;
    protected int  iPauseAtStart = 0;               // Pause before (actual) start processing;

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

    /**
     * Flag: should ignore all error(s) in Loop cycle?
     */
    protected boolean bShouldIgnoreLoopError = false;


    // inner class
    public class RefDataInteger {
        public long iCountLoop = 0L;
        public int  iCountData = 0;
    }


    public void setMaxNumOfLoops(long aiVal) {
        iMaxNumOfLoops = aiVal;
    }
    public void setPauseBetweenLoop(int aiVal) {
        iPauseBetweenLoop = aiVal;
    }
}
