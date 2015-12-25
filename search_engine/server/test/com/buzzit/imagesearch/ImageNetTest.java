package com.buzzit.imagesearch;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * To do this test, first run the corresponding imageNetServer.
 */
public class ImageNetTest {
    private static final String IMAGE_PATH = "../testdir/doge.jpeg";
    private static final String IMAGE_PATH1 = "../testdir/cat.jpeg";
    @Test
    public void testImageClassification() {
        try {
            File imageFile = new File(IMAGE_PATH);
            ImageNet imageNet = new ImageNet();
            String imageCaption = imageNet.getImageClassification(imageFile);
            System.out.println(imageCaption);

            imageFile = new File(IMAGE_PATH1);
            imageCaption = imageNet.getImageClassification(imageFile);
            System.out.println(imageCaption);
        } catch (Exception e) {
            assertEquals("success", e.getMessage());
        }
    }
}