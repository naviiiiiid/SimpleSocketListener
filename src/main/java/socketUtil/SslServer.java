package socketUtil;

/**
 * Created by n-soorani on 2017/03/15.
 */

import helper.SslUtil;
import main.socketListener;
import org.apache.log4j.Logger;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.GeneralSecurityException;

/**
 * Created by n-soorani on 2017/03/14.
 */
public class SslServer implements SslContextProvider {

    private Logger logger;

    public SslServer(Logger logger) {
        this.logger = logger;
    }


    @Override
    public TrustManager[] getTrustManagers() throws Exception {
        return SslUtil.createTrustManagers(socketListener.myConfig.getProperties("TrustRootPath") ,
                socketListener.myConfig.getProperties("TrustRootPass").toCharArray());
    }

    @Override
    public KeyManager[] getKeyManagers() throws GeneralSecurityException, IOException , Exception {
        return SslUtil.createKeyManagers(socketListener.myConfig.getProperties("keyStorePath"),
                socketListener.myConfig.getProperties("keyStorePass").toCharArray());
    }

    @Override
    public String getProtocol() throws Exception  {
        return socketListener.myConfig.getProperties("Protocol");
    }


    public ServerSocket run(int port , boolean needClientAuth)throws Exception {
        return createSSLSocket(port , needClientAuth);
    }


    private ServerSocket createSSLSocket(int port , boolean needClientAuth) throws Exception {
        SSLServerSocket socket = SslUtil.createSSLServerSocket(port, this);
        socket.setNeedClientAuth(needClientAuth);
        this.logger.info("SSLSocket is Create");
        return socket;
    }
}