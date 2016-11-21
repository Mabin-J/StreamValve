package info.mabin.java.io;

import java.io.IOException;
import java.io.InputStream;


/**
 * Valved InputStream
 */
public class ValvedInputStream extends InputStream{
    private InputStream targetInputStream;
    private StreamValve streamValve;

    /**
     * Constructor of Valved InputStream
     * @param targetInputStream target InputStream
     * @param speedBytesPerSecond Speed. Bytes per Second.
     */
    public ValvedInputStream(InputStream targetInputStream, long speedBytesPerSecond) {
        this.targetInputStream = targetInputStream;

        streamValve = new StreamValve(speedBytesPerSecond);
    }

    @Override
    public int read() throws IOException {
        streamValve.delay();

        return targetInputStream.read();
    }

    @Override
    public void close() throws IOException {
        streamValve.close();

        super.close();
    }



    @Override
    public int available() throws IOException {
        return targetInputStream.available();
    }

    @Override
    public synchronized void mark(int i) {
        targetInputStream.mark(i);
    }

    @Override
    public boolean markSupported() {
        return targetInputStream.markSupported();
    }

    @Override
    public synchronized void reset() throws IOException {
        targetInputStream.reset();
    }

    @Override
    public long skip(long l) throws IOException {
        return targetInputStream.skip(l);
    }
}