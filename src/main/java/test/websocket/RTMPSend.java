package test.websocket;

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
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.TimerTask;


public class RTMPSend extends TimerTask {
    private Session session;
    private java.util.List<ByteBuffer> websocketData;

    public RTMPSend(Session mySession, java.util.List data) {
        session = mySession;
        websocketData = data;
    }

    public void run() {
        try {
            if (websocketData.size() > 0) {
                session.getRemote().sendBytes(websocketData.get(0));
                websocketData.remove(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}

