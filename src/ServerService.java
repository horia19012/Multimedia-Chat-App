import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;

import javax.swing.JTextArea;

public class ServerService {

    private static ServerService instance;
    private SocketIOServer server;
    private JTextArea textArea;
//    private List<UserModel>
    private final int PORT_NUMBER = 9999;

    public static ServerService getInstance(JTextArea textArea) {
        if (instance == null) {
            instance = new ServerService(textArea);
        }
        return instance;
    }

    private ServerService(JTextArea textArea) {
        this.textArea = textArea;
    }

    public SocketIOServer startServer() {
        Configuration config = new Configuration();
        config.setPort(PORT_NUMBER);
        server = new SocketIOServer(config);
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient sioc) {
                textArea.append("One client connected\n");
            }
        });


        server.addEventListener("register", UserModel.class, new DataListener<UserModel>() {
            @Override
            public void onData(SocketIOClient sioc, UserModel t, AckRequest ar) throws Exception {

                textArea.append("User has Register :" + t.getUserName() + " Pass :" + t.getPassword() + "\n");


            }
        });

        server.addEventListener("message", UserModel.class, new DataListener<UserModel>() {
            @Override
            public void onData(SocketIOClient sioc, UserModel t, AckRequest ar) throws Exception {

                textArea.append("User has Register :" + t.getUserName() + " Pass :" + t.getPassword() + "\n");


            }
        });

        server.start();
        textArea.append("Server has Start on port : " + PORT_NUMBER + "\n");
        return server;
    }


}