package info.mabin.java.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StreamValve implements AutoCloseable{
    private static final Logger LOG = LoggerFactory.getLogger(StreamValve.class);
    /** Check Period. Milliseconds unit */
    private static final int CHECK_PERIOD = 100;

    private long bytesPerMillisecond = 0;

    private long allowedBytesAmount = Long.MAX_VALUE;
    private volatile long nextAllowedBytesAmount = Long.MAX_VALUE;

    private int sleepTime = 1;
    private long passedBytesAmount = 0;

    private SpeedCheckerThread speedCheckerThread;



    StreamValve(long speedBytesPerSecond){
        bytesPerMillisecond = speedBytesPerSecond / 1000;
        if(bytesPerMillisecond == 0){
            bytesPerMillisecond = 1;
        }

        if(bytesPerMillisecond > 125000){
            nextAllowedBytesAmount
                    = this.allowedBytesAmount
                    = Long.MAX_VALUE;
        } else {
            nextAllowedBytesAmount
                    = this.allowedBytesAmount
                    = (long) ((125000 * bytesPerMillisecond) / (float)(125000 - bytesPerMillisecond));
        }

        if(LOG.isTraceEnabled()){
            LOG.trace("Bytes Per Millisecond: " + bytesPerMillisecond);
            LOG.trace("nextAllowedBytesAmount: " + nextAllowedBytesAmount);
        }



        speedCheckerThread = new SpeedCheckerThread();
        speedCheckerThread.start();
    }



    @Override
    public void close(){
        speedCheckerThread.interrupt();
    }



    void delay(){
        while(allowedBytesAmount <= 0){
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                if(LOG.isDebugEnabled()) {
                    LOG.debug("Interrupted", e);
                }
            }

            if(nextAllowedBytesAmount > 0){
                allowedBytesAmount = nextAllowedBytesAmount;
            }
        }

        allowedBytesAmount--;
        passedBytesAmount++;
    }



    private class SpeedCheckerThread extends Thread {
        private final Logger LOG = LoggerFactory.getLogger(SpeedCheckerThread.class);
        private final int LOOP_COUNT_FOR_LOG = (int) (1000 / (float) CHECK_PERIOD);

        private SpeedCheckerThread(){
            super("StreamValveSpeedCheckerThread");
        }

        @Override
        public void run() {
            int count4Log = 0;

            while(!this.isInterrupted()){
                try {
                    Thread.sleep(CHECK_PERIOD);
                } catch (InterruptedException e) {
                    if(LOG.isDebugEnabled()) {
                        LOG.debug("SpeedCheckerThread is Interrupted!", e);
                    }
                }

                if(LOG.isTraceEnabled() && passedBytesAmount == 0){
                    LOG.trace("passed");
                    LOG.trace("NextAllowByteAmount: " + nextAllowedBytesAmount);
                    LOG.trace("bytesPerMillisecond: " + bytesPerMillisecond);
                    LOG.trace("sleepTime: " + sleepTime);
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
                    LOG.trace("avgSample: " + avgSample);
                    LOG.trace("avgSpeed: " + (avgSample / (float)CHECK_PERIOD));
                    LOG.trace("nextAllowByteAmount: " + nextAllowedBytesAmount);
                    LOG.trace("sleepTime: " + sleepTime);
                    count4Log = 0;
                }

                passedBytesAmount = 0;
            }

        }
    }
}
