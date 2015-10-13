package test.websocket;

import javax.servlet.annotation.WebServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * Created by tapha.camara on 08/10/15.
 */

@WebServlet(urlPatterns="/toWebSocket")
public class MyWebSocketServlet extends WebSocketServlet {

    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(MyWebSocketHandler.class);
    }
}

