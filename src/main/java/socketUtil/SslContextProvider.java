package socketUtil;

/**
 * Created by n-soorani on 2017/03/15.
 */

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by n-soorani on 2017/03/14.
 */
public interface SslContextProvider {
    TrustManager[] getTrustManagers() throws Exception;

    KeyManager[] getKeyManagers() throws GeneralSecurityException, IOException , Exception;

    String getProtocol()throws Exception;
}

