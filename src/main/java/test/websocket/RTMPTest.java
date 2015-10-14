package test.websocket;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.eclipse.jetty.websocket.api.Session;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.TimerTask;


public class RTMPTest extends TimerTask {
    FFmpegFrameGrabber grabber;
    CanvasFrame canvas;
    opencv_core.IplImage capturedFrame;
    Session session;

    public RTMPTest(Session mySession) {
        session = mySession;

        grabber = new FFmpegFrameGrabber("rtmp://93.93.85.123/live/1_400_400_750000");

        try {
            grabber.start();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        canvas = new CanvasFrame("JavaCV player");

        capturedFrame = null;

        //cFrame.dispose();
        //grabber.stop();
    }

    public void run()  {
        try {
            capturedFrame = grabber.grab();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        if (capturedFrame == null) {
            System.out.println("!!! Failed cvQueryFrame");
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

            jpgWriteParam.setCompressionQuality(0.5f);

            jpgWriter.setOutput(outputStream);
            jpgWriter.write(null, outputImage, jpgWriteParam);

            String img = new String(Base64.encode(byteArray.toByteArray()));

            session.getRemote().sendString(img);

            jpgWriter.dispose();

        } catch (IOException e) {
            e.printStackTrace();
        }

        canvas.showImage(capturedFrame);
    }

}

