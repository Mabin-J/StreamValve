package info.mabin.java.io.test;

import info.mabin.java.io.RandomByteInputStream;
import info.mabin.java.io.ValvedInputStream;
import info.mabin.java.io.ValvedOutputStream;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StreamValveLocalTest {
    private static Logger LOG = LoggerFactory.getLogger(StreamValveLocalTest.class);
    private static final int TEST_SIZE = 10 * 1024 * 1024;
    private static final int TEST_SPEED = 1024 * 1024;

    @Test
    public void valvedInputStreamWithSpeedSetTest() throws IOException {
        LOG.info("== Valved InputStream with Speed Set Test ==");

        double estimatedMilliseconds = (TEST_SIZE / (double)TEST_SPEED) * 1000;

        if(LOG.isDebugEnabled()){
            LOG.debug(String.format(
                    "Test Speed: %,.2f Bytes per Second",
                    (double) TEST_SPEED
            ));
            LOG.debug(String.format(
                    "Test Size: %,d Bytes",
                    TEST_SIZE
            ));
        }
        LOG.info(String.format(
                "Estimated Time: %,.2f Seconds",
                estimatedMilliseconds / 1000d
        ));

        RandomByteInputStream randomByteInputStream
                = new RandomByteInputStream(TEST_SIZE, true);

        ValvedInputStream valvedInputStream
                = new ValvedInputStream(randomByteInputStream, TEST_SPEED);

        ByteArrayOutputStream byteArrayOutputStream
                = new ByteArrayOutputStream();


        long startTimestamp = new Date().getTime();
        long tmpTimestamp = startTimestamp;

        int passedBytesAmount = 0;
        int totalPassedByteAmount = 0;




        LOG.info("Speed Check...");

        byte[] buffer = new byte[4096];
        int len;
        while((len = valvedInputStream.read(buffer)) != -1){
            passedBytesAmount += len;
            totalPassedByteAmount += len;

            byteArrayOutputStream.write(buffer, 0, len);

            long currentTimestamp = new Date().getTime();
            long passedTime = currentTimestamp - tmpTimestamp;

            if(passedTime > 1000){
                if(LOG.isDebugEnabled()) {
                    LOG.debug(String.format(
                            "Current Speed: %,12.2f Bytes per Second",
                            passedBytesAmount / (double) passedTime * 1000
                    ));
                } else {
                    System.out.print(".");
                }

                passedBytesAmount = 0;
                tmpTimestamp = currentTimestamp;
            }
        }
        if(!LOG.isDebugEnabled()) {
            System.out.print("\n");
        }

        long totalPassedTime = new Date().getTime() - startTimestamp;
        double realSpeed = totalPassedByteAmount / (double) totalPassedTime * 1000;

        if(LOG.isDebugEnabled()){
            LOG.debug(String.format(
                    "Test Speed: %,.2f Bytes per Second",
                    (double) TEST_SPEED
            ));
            LOG.debug(String.format(
                    "Real Speed: %,.2f Bytes per Second",
                    realSpeed
            ));
            LOG.debug(String.format(
                    "Estimated Time: %,.2f Seconds",
                    estimatedMilliseconds / 1000d
            ));
            LOG.debug(String.format(
                    "Passed Time:    %,.2f Seconds",
                    totalPassedTime / 1000d
            ));
            LOG.debug(String.format(
                    "Ratio(Estimated/Real): %,.6f",
                    estimatedMilliseconds/totalPassedTime
            ));
        }

        Assert.assertEquals(
                "Too much different!",
                estimatedMilliseconds,
                totalPassedTime,
                estimatedMilliseconds / 100
        );
        LOG.info("PASS!");



        LOG.info("Data Check...");
        byte[] createdByteArray = randomByteInputStream.toByteArray();

        byte[] passedByteArray = byteArrayOutputStream.toByteArray();

        Assert.assertArrayEquals(
                "Passed Bytes are not Same with Created Bytes!",
                createdByteArray,
                passedByteArray);
        LOG.info("PASS!");



        byteArrayOutputStream.close();
        valvedInputStream.close();
        randomByteInputStream.close();
    }


    @Test
    public void valvedOutputStreamWithSpeedSetTest() throws IOException {
        LOG.info("== Valved OutputStream with Speed Set Test ==");

        double estimatedMilliseconds = (TEST_SIZE / (double)TEST_SPEED) * 1000;

        if(LOG.isDebugEnabled()){
            LOG.debug(String.format(
                    "Test Speed: %,.2f Bytes per Second",
                    (double) TEST_SPEED
            ));
            LOG.debug(String.format(
                    "Test Size: %,d Bytes",
                    TEST_SIZE
            ));
        }
        LOG.info(String.format(
                "Estimated Time: %,.2f Seconds",
                estimatedMilliseconds / 1000d
        ));

        RandomByteInputStream randomByteInputStream
                = new RandomByteInputStream(TEST_SIZE, true);

        ByteArrayOutputStream byteArrayOutputStream
                = new ByteArrayOutputStream();

        ValvedOutputStream valvedOutputStream
                = new ValvedOutputStream(byteArrayOutputStream, TEST_SPEED);



        long startTimestamp = new Date().getTime();
        long tmpTimestamp = startTimestamp;

        int passedBytesAmount = 0;
        int totalPassedByteAmount = 0;




        LOG.info("Speed Check...");

        byte[] buffer = new byte[4096];
        int len;
        while((len = randomByteInputStream.read(buffer)) != -1){
            passedBytesAmount += len;
            totalPassedByteAmount += len;

            valvedOutputStream.write(buffer, 0, len);

            long currentTimestamp = new Date().getTime();
            long passedTime = currentTimestamp - tmpTimestamp;

            if(passedTime > 1000){
                if(LOG.isDebugEnabled()) {
                    LOG.debug(String.format(
                            "Current Speed: %,12.2f Bytes per Second",
                            passedBytesAmount / (double) passedTime * 1000
                    ));
                } else {
                    System.out.print(".");
                }

                passedBytesAmount = 0;
                tmpTimestamp = currentTimestamp;
            }
        }
        if(!LOG.isDebugEnabled()) {
            System.out.print("\n");
        }

        long totalPassedTime = new Date().getTime() - startTimestamp;
        double realSpeed = totalPassedByteAmount / (double) totalPassedTime * 1000;

        if(LOG.isDebugEnabled()){
            LOG.debug(String.format(
                    "Test Speed: %,.2f Bytes per Second",
                    (double) TEST_SPEED
            ));
            LOG.debug(String.format(
                    "Real Speed: %,.2f Bytes per Second",
                    realSpeed
            ));
            LOG.debug(String.format(
                    "Estimated Time: %,.2f Seconds",
                    estimatedMilliseconds / 1000d
            ));
            LOG.debug(String.format(
                    "Passed Time:    %,.2f Seconds",
                    totalPassedTime / 1000d
            ));
            LOG.debug(String.format(
                    "Ratio(Estimated/Real): %,.6f",
                    estimatedMilliseconds / totalPassedTime
            ));
        }

        Assert.assertEquals(
                "Too much different!",
                estimatedMilliseconds,
                totalPassedTime,
                estimatedMilliseconds / 100);
        LOG.info("PASS!");



        LOG.info("Data Check...");
        byte[] originalByteArray = randomByteInputStream.toByteArray();

        byte[] testByteArray = byteArrayOutputStream.toByteArray();

        Assert.assertArrayEquals(
                "Passed Bytes are not Same with Created Bytes!",
                originalByteArray,
                testByteArray
        );
        LOG.info("PASS!");



        valvedOutputStream.close();
        byteArrayOutputStream.close();
        randomByteInputStream.close();
    }



    @Test
    public void valvedInputStreamWithoutSpeedSetTest() throws IOException {
        LOG.info("== Valved InputStream without Speed Set Test ==");

        RandomByteInputStream randomByteInputStream
                = new RandomByteInputStream(TEST_SIZE, true);

        ValvedInputStream valvedInputStream
                = new ValvedInputStream(randomByteInputStream);

        ByteArrayOutputStream byteArrayOutputStream
                = new ByteArrayOutputStream();



        byte[] buffer = new byte[4096];
        int len;
        while((len = valvedInputStream.read(buffer)) != -1){
            byteArrayOutputStream.write(buffer, 0, len);
        }



        LOG.info("Data Check...");
        byte[] createdByteArray = randomByteInputStream.toByteArray();

        byte[] passedByteArray = byteArrayOutputStream.toByteArray();

        Assert.assertArrayEquals(
                "Passed Bytes are not Same with Created Bytes!",
                createdByteArray,
                passedByteArray);
        LOG.info("PASS!");



        byteArrayOutputStream.close();
        valvedInputStream.close();
        randomByteInputStream.close();
    }


    @Test
    public void valvedOutputStreamWithoutSpeedSetTest() throws IOException {
        LOG.info("== Valved OutputStream without Speed Set Test ==");



        RandomByteInputStream randomByteInputStream
                = new RandomByteInputStream(TEST_SIZE, true);

        ByteArrayOutputStream byteArrayOutputStream
                = new ByteArrayOutputStream();

        ValvedOutputStream valvedOutputStream
                = new ValvedOutputStream(byteArrayOutputStream);



        byte[] buffer = new byte[4096];
        int len;
        while((len = randomByteInputStream.read(buffer)) != -1){
            valvedOutputStream.write(buffer, 0, len);
        }



        LOG.info("Data Check...");
        byte[] originalByteArray = randomByteInputStream.toByteArray();

        byte[] testByteArray = byteArrayOutputStream.toByteArray();

        Assert.assertArrayEquals(
                "Passed Bytes are not Same with Created Bytes!",
                originalByteArray,
                testByteArray
        );
        LOG.info("PASS!");



        valvedOutputStream.close();
        byteArrayOutputStream.close();
        randomByteInputStream.close();
    }
}
