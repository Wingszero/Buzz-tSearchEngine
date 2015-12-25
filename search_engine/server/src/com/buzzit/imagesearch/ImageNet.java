package com.buzzit.imagesearch;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 *
 */
public class ImageNet {
    private static final int BUF_SIZE = 8192;
    private static final String CHAR_SET = "UTF-8";

    private String imageServerUrl;

    public String getImageClassification(File image) {
        try {
            String url = getImageServerUrlFromFile();
            System.out.println("imagenet server url: " + url);
            String pair[] = url.split(":");
            Socket socket = new Socket(pair[0], Integer.parseInt(pair[1]));

            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(image));

            int fileSize = (int)image.length();
            out.write(ByteBuffer.allocate(4).putInt(fileSize).array());

            byte buffer[] = new byte[BUF_SIZE];
            int n = 0;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            out.flush();

            String imageCaption = IOUtils.toString(socket.getInputStream()).split("\\s+", 2)[1];
            System.out.println("imagenet result: " + imageCaption);
            return imageCaption;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getImageServerUrlFromFile() {
        try {
            File confFile = new File("image.conf");
            System.out.println("Reading URL from file...");
            BufferedReader in = new BufferedReader(new FileReader(confFile));

            imageServerUrl = in.readLine();
            return imageServerUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
