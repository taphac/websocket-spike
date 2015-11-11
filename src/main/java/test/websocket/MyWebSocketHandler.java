package test.websocket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * Created by tapha.camara on 08/10/15.
 */
@WebSocket
public class MyWebSocketHandler {
    private Timer audioTimer;
    private Timer videoTimer;
    private List websocketData;
    private Timer dataTimer;

//Make an ArrayList to hold RaceCar objects to determine winners

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());

        List<ByteBuffer> websocketData = Collections.synchronizedList(new ArrayList<ByteBuffer>());

        RTMPAudio audio = new RTMPAudio(websocketData);
        RTMPVideo video = new RTMPVideo(websocketData);
        RTMPSend data = new RTMPSend(session, websocketData);

        audioTimer = new Timer();
        audioTimer.scheduleAtFixedRate(audio, 0, 10);


        videoTimer = new Timer();
        videoTimer.scheduleAtFixedRate(video, 0, 30);

        dataTimer = new Timer();
        dataTimer.scheduleAtFixedRate(data, 0, 15);
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        System.out.println("Message: " + message);
    }
}
