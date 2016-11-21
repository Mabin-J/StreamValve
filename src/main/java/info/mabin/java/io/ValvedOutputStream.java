package info.mabin.java.io;

import java.io.IOException;
import java.io.OutputStream;


/**
 * Valved OutputStream
 */
public class ValvedOutputStream extends OutputStream{
    private OutputStream targetOutputStream;
    private StreamValve streamValve;

    /**
     * Constructor of Valved OutputStream
     * @param targetOutputStream target OutputStream
     * @param speedBytesPerSecond Speed. Bytes per Second.
     */
    public ValvedOutputStream(OutputStream targetOutputStream, long speedBytesPerSecond) {
        this.targetOutputStream = targetOutputStream;

        streamValve = new StreamValve(speedBytesPerSecond);
    }

    @Override
    public void write(int b) throws IOException {
        streamValve.delay();

        targetOutputStream.write(b);
    }

    @Override
    public void close() throws IOException {
        streamValve.close();

        super.close();
    }



    @Override
    public void flush() throws IOException {
        targetOutputStream.flush();
    }
}