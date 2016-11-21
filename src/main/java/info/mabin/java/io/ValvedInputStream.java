package info.mabin.java.io;

import java.io.IOException;
import java.io.InputStream;


/**
 * Valved InputStream
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ValvedInputStream extends InputStream{
    private InputStream targetInputStream;
    private StreamValve streamValve;



    /**
     * Constructor of Valved InputStream
     * @param targetInputStream Target InputStream
     */
    public ValvedInputStream(InputStream targetInputStream){
        this.targetInputStream = targetInputStream;
        streamValve = new StreamValve();
    }

    /**
     * Constructor of Valved InputStream with Speed Option.<br/>
     * Speed have to be Bytes per Second.
     * @param targetInputStream Target InputStream
     * @param speedBytesPerSecond Streaming Speed. (Bytes per Second)
     */
    public ValvedInputStream(InputStream targetInputStream, long speedBytesPerSecond) {
        this(targetInputStream);
        setSpeed(speedBytesPerSecond);
    }



    /**
     * Set Streaming Speed.<br/>
     * Speed have to be Bytes per Second.
     * @param speedBytesPerSecond Streaming Speed. (Bytes per Second)
     */
    public void setSpeed(long speedBytesPerSecond){
        streamValve.setSpeed(speedBytesPerSecond);
    }

    @Override
    public int read() throws IOException {
        streamValve.flowControl();

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