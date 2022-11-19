package listener;

import main.socketListener;
import org.apache.log4j.Logger;
import socketUtil.SslServer;
import socketUtil.TcpServer;
import wrapper.wrapperService;

import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable {

    public static final Logger logger = Logger.getLogger(Listener.class);

    public Listener() {
        Thread listenerThread = new Thread(this, "Listener");
        listenerThread.start();
    }

    @Override
    public void run(){

        ServerSocket serverSocket = null;

        try {
            boolean sslEnable = Boolean.parseBoolean(socketListener.myConfig.getProperties("sslEnable"));
            int port =  Integer.parseInt(socketListener.myConfig.getProperties("port"));

            if (sslEnable) {
                SslServer sslServer = new SslServer(logger);
                serverSocket = sslServer.run(port,
                        (Boolean.parseBoolean(socketListener.myConfig.getProperties("needClientAuth"))));
            } else {
                TcpServer tcpServer = new TcpServer(logger);
                serverSocket = tcpServer.run(port);
            }
        }
        catch (Exception ex) {
            logger.error("Exception : => " + ex);
        }


        while (true) {
            if (serverSocket == null) {
                logger.error("serverSocket is Null!!");
                break;
            }else {
                try {
                    logger.info("waiting Accept Socket Client!!! ");
                    Socket client = serverSocket.accept();

                    logger.info("Accepted Client Ip : " + client.getRemoteSocketAddress().toString());
                    Integer indexFree = socketListener.freeIndexStack.pop();
                    socketListener.handleClientList.get(indexFree).threadParam = client;
                    socketListener.handleClientList.get(indexFree).getManualResetEvent().set();

                } catch (Exception ex) {
                    logger.error(ex);
                }
            }
        }
    }
}
