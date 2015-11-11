package test.websocket;

import com.google.common.util.concurrent.RateLimiter;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;


public class RTMPVideo extends TimerTask {
    private FFmpegFrameGrabber grabber;
    private opencv_core.IplImage capturedFrame;
    private java.util.List<ByteBuffer> websocketData;
    private RateLimiter rateLimiter;

    public RTMPVideo(java.util.List data) {
        websocketData = data;

        grabber = new FFmpegFrameGrabber("rtmp://93.93.85.123/games-platform/2_400_400_750000");

        try {
            grabber.start();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        capturedFrame = null;

        rateLimiter = RateLimiter.create(30);

    }

    public void run() {
        rateLimiter.acquire();
        stream();
    }

    private void stream() {
        try {
            capturedFrame = grabber.grab();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        try {

            BufferedImage image = capturedFrame.getBufferedImage();

            int w = 400;
            int h = 400;
            int newW = 200;
            int newH = 200;

            BufferedImage resizedImg = new BufferedImage(newW, newH, image.getType());

            Graphics2D graphics = resizedImg.createGraphics();

            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.drawImage(image, 0, 0, newW, newH, 0, 0, w, h, null);
            graphics.dispose();

            IIOImage outputImage = new IIOImage(resizedImg, null, null);

            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

            ImageOutputStream outputStream = ImageIO.createImageOutputStream(byteArray);

            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
            jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

            jpgWriteParam.setCompressionQuality(0.6f);

            jpgWriter.setOutput(outputStream);
            jpgWriter.write(null, outputImage, jpgWriteParam);

            websocketData.add(ByteBuffer.wrap(byteArray.toByteArray()));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

