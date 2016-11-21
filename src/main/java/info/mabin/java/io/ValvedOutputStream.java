package info.mabin.java.io;

import java.io.IOException;
import java.io.OutputStream;


/**
 * Valved OutputStream
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class ValvedOutputStream extends OutputStream{
    private OutputStream targetOutputStream;
    private StreamValve streamValve;



    /**
     * Constructor of Valved OutputStream
     * @param targetOutputStream Target OutputStream
     */
    public ValvedOutputStream(OutputStream targetOutputStream){
        this.targetOutputStream = targetOutputStream;
        streamValve = new StreamValve();
    }

    /**
     * Constructor of Valved OutputStream with Speed Option.<br/>
     * Speed have to be Bytes per Second.
     * @param targetOutputStream Target OutputStream
     * @param speedBytesPerSecond Streaming Speed. (Bytes per Second)
     */
    public ValvedOutputStream(OutputStream targetOutputStream, long speedBytesPerSecond) {
        this(targetOutputStream);
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
    public void write(int b) throws IOException {
        streamValve.flowControl();

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