package test.websocket;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.*;
import org.eclipse.jetty.websocket.api.Session;
import java.nio.*;
import java.util.*;
import java.util.List;


public class RTMPAudio extends TimerTask {
    private FFmpegFrameGrabber grabber;
    private opencv_core.IplImage capturedFrame;
    private org.bytedeco.javacv.Frame frame;
    private int count = 0;
    private  ByteBuffer combinedBuffer;
    private int bufferCount = 10;
    private java.util.List websocketData;


    public RTMPAudio(List data) {
        websocketData = data;

        grabber = new FFmpegFrameGrabber("rtmp://93.93.85.123:1935/games-platform/2_400_400_750000");

        try {
            grabber.start();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        capturedFrame = null;

        combinedBuffer = ByteBuffer.allocate(1024 * 4 * bufferCount);

    }

    public void run() {

        if (count == bufferCount) {
            combinedBuffer.position(0);

            websocketData.add(combinedBuffer);

            combinedBuffer = ByteBuffer.allocate(1024 * 4 * bufferCount);
            count = 0;
        }

        try {
            frame = grabber.grabFrame();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        if (frame.samples != null) {
            FloatBuffer buffer = (FloatBuffer) frame.samples[0];

            float[] floatBuffer = new float[buffer.remaining()];
            buffer.get(floatBuffer);

            ByteBuffer byteBuffer = ByteBuffer.allocate(floatBuffer.length * 4);

            byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().put(floatBuffer);

            combinedBuffer = combinedBuffer.put(byteBuffer);

            count++;

        }
    }

}

