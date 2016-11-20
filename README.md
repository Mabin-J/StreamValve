StreamValve
===========
Speed Limiter of InputStream/OutputStream


-------
Example
-------
```java
package info.mabin.java.io.example;

import info.mabin.java.io.RandomByteInputStream;
import info.mabin.java.io.ValvedInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

public class ValvedInputStreamExample {
    private static final int DATA_SIZE = 10 * 1024 * 1024;
    private static final int SPEED = 1024 * 1024;

    public static void main(String... args) throws IOException {
        try(
                RandomByteInputStream randomByteInputStream
                        = new RandomByteInputStream(DATA_SIZE, true, true);

                ValvedInputStream valvedInputStream
                        = new ValvedInputStream(randomByteInputStream, SPEED);

                ByteArrayOutputStream byteArrayOutputStream
                        = new ByteArrayOutputStream()
        ){

            long tmpTimestamp = new Date().getTime();

            byte[] buffer = new byte[4096];
            int len;
            int passedBytesAmount = 0;
            while((len = valvedInputStream.read(buffer)) != -1){
                byteArrayOutputStream.write(buffer, 0, len);
                passedBytesAmount += len;

                long currentTimestamp = new Date().getTime();
                long passedTime = currentTimestamp - tmpTimestamp;

                if(passedTime > 1000){
                    System.out.println(String.format(
                            "Current Speed: %,12.2f Bytes per Second",
                            passedBytesAmount / (double) passedTime * 1000
                    ));
                    tmpTimestamp = currentTimestamp;
                    passedBytesAmount = 0;
                }
            }
        }
    }
}
```