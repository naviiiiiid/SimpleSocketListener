package socketUtil;

import org.apache.log4j.Logger;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.GeneralSecurityException;

/**
 * Created by n-soorani on 2017/03/14.
 */
public class TcpServer {

    private Logger logger;

    public TcpServer(Logger logger){
        this.logger = logger;
   }

    public ServerSocket run(int port)throws Exception {
        return createTcpSocket(port);
    }


    private ServerSocket createTcpSocket(int port) throws Exception {
        this.logger.info("TcpSocket is Create");
        return new ServerSocket(port , 100 );
    }
}
