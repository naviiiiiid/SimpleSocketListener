package listener;

import main.socketListener;
import socketUtil.SslServer;
import socketUtil.TcpServer;

import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable {

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
                SslServer sslServer = new SslServer(socketListener.logger);
                serverSocket = sslServer.run(port,
                        (Boolean.parseBoolean(socketListener.myConfig.getProperties("needClientAuth"))));
            } else {
                TcpServer tcpServer = new TcpServer(socketListener.logger);
                serverSocket = tcpServer.run(port);
            }
        }
        catch (Exception ex) {
            socketListener.logger.error("Exception : => " + ex);
        }


        while (true) {
            if (serverSocket == null) {
                socketListener.logger.error("serverSocket is Null!!");
                break;
            }else {
                try {
                    socketListener.logger.info("waiting Accept Socket Client!!! ");
                    Socket client = serverSocket.accept();

                    socketListener.logger.info("Accepted Client Ip : " + client.getRemoteSocketAddress().toString());
                    Integer indexFree = socketListener.freeIndexStack.pop();
                    socketListener.handleClientList.get(indexFree).threadParam = client;
                    socketListener.handleClientList.get(indexFree).getManualResetEvent().set();

                } catch (Exception ex) {
                    socketListener.logger.error(ex);
                }
            }
        }
    }
}
