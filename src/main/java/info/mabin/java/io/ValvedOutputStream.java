package info.mabin.java.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;


/**
 * Valved OutputStream
 */
public class ValvedOutputStream extends OutputStream {
    private static final Logger LOG = LoggerFactory.getLogger(ValvedOutputStream.class);

    /** Check Period. Milliseconds unit */
    private static final int CHECK_PERIOD = 100;

    private OutputStream targetOutputStream;

    private int sleepTime = 1;
    private long bytesPerMillisecond = 0;

    private long passedBytesAmount = 0;
    private long allowedBytesAmount = Long.MAX_VALUE;
    private volatile long nextAllowedBytesAmount = Long.MAX_VALUE;

    private Thread speedCheckerThread;


    /**
     * Constructor of Valved OutputStream
     * @param targetOutputStream target outputStream
     * @param speedBytesPerSecond Speed. Bytes per Second.
     */
    public ValvedOutputStream(OutputStream targetOutputStream, long speedBytesPerSecond) {
        if(LOG.isTraceEnabled()) {
            LOG.debug("Init");
        }

        this.targetOutputStream = targetOutputStream;
        this.bytesPerMillisecond = speedBytesPerSecond / 1000;
        if(this.bytesPerMillisecond == 0){
            this.bytesPerMillisecond = 1;
        }

        if(bytesPerMillisecond > 125000){
            this.nextAllowedBytesAmount
                    = this.allowedBytesAmount
                    = Long.MAX_VALUE;
        } else {
            this.nextAllowedBytesAmount
                    = this.allowedBytesAmount
                    = (long) ((125000 * bytesPerMillisecond) / (float)(125000 - bytesPerMillisecond));
        }

        if(LOG.isTraceEnabled()){
            LOG.debug("Bytes Per Millisecond: " + bytesPerMillisecond);
            LOG.debug("nextAllowedBytesAmount: " + nextAllowedBytesAmount);
        }

        this.speedCheckerThread = new SpeedCheckerThread();
        this.speedCheckerThread.start();

        if(LOG.isTraceEnabled()) {
            LOG.debug("Inited");
        }
    }

    @Override
    public void write(int b) throws IOException {
        while(allowedBytesAmount <= 0){
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                if(LOG.isTraceEnabled()) {
                    LOG.trace("Interrupted", e);
                }
            }

            if(nextAllowedBytesAmount > 0){
                allowedBytesAmount = nextAllowedBytesAmount;
            }
        }

        allowedBytesAmount--;
        passedBytesAmount++;

        targetOutputStream.write(b);
    }

    @Override
    public void close() throws IOException {
        speedCheckerThread.interrupt();

        super.close();
    }


    private class SpeedCheckerThread extends Thread {
        private final Logger LOG = LoggerFactory.getLogger(SpeedCheckerThread.class);
        private final int LOOP_COUNT_FOR_LOG = (int) (1000 / (float)CHECK_PERIOD);

        private SpeedCheckerThread(){
            super("ValvedOutputStreamSpeedChecker");
        }

        @Override
        public void run() {
            int count4Log = 0;

            while(!this.isInterrupted()){
                try {
                    Thread.sleep(CHECK_PERIOD);
                } catch (InterruptedException e) {
                    if(LOG.isTraceEnabled()) {
                        LOG.debug("SpeedCheckerThread is Interrupted!", e);
                    }
                }

                if(LOG.isTraceEnabled() && passedBytesAmount == 0){
                    LOG.debug("passed");
                    LOG.debug("NextAllowByteAmount: " + nextAllowedBytesAmount);
                    LOG.debug("bytesPerMillisecond: " + bytesPerMillisecond);
                    LOG.debug("sleepTime: " + sleepTime);
                    if(nextAllowedBytesAmount < 1){
                        nextAllowedBytesAmount = 1;
                    }
                    if(bytesPerMillisecond < 1){
                        bytesPerMillisecond = 1;
                    }
                    continue;
                }

                float avgSample = passedBytesAmount;

                nextAllowedBytesAmount *= bytesPerMillisecond / (avgSample / (float)CHECK_PERIOD);
                if(nextAllowedBytesAmount > bytesPerMillisecond * 2){
                    nextAllowedBytesAmount = bytesPerMillisecond * 2;
                }

                if(nextAllowedBytesAmount == 0){
                    nextAllowedBytesAmount = 1;
                    sleepTime++;
                } else if(sleepTime > 1){
                    sleepTime--;
                } else if(nextAllowedBytesAmount >= bytesPerMillisecond * 2){
                    sleepTime = 0;
                } else {
                    sleepTime = 1;
                }

                if(LOG.isTraceEnabled() && count4Log++ > LOOP_COUNT_FOR_LOG){
                    LOG.debug("avgSample: " + avgSample);
                    LOG.debug("avgSpeed: " + (avgSample / (float)CHECK_PERIOD));
                    LOG.debug("nextAllowByteAmount: " + nextAllowedBytesAmount);
                    LOG.debug("sleepTime: " + sleepTime);
                    count4Log = 0;
                }

                passedBytesAmount = 0;
            }
        }
    }
}
