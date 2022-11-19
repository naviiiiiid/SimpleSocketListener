package message;

import main.socketListener;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by n-soorani on 2017/03/14.
 */
public class handleClientMessage {
    private final Socket clientSocket;
    public static final Logger logger = Logger.getLogger(handleClientMessage.class);

    public handleClientMessage(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void handle() throws Exception {

        InputStream is = null;
        OutputStream os = null;

        try {

           String uuidLine =  UUID.randomUUID().toString();

            logger.info("create Input & Output Stream | " + uuidLine +
                    "|Elements in Stack is: " + socketListener.freeIndexStack.toString());

            is = new DataInputStream(clientSocket.getInputStream());
            os = new DataOutputStream(clientSocket.getOutputStream());
            handleMessage handleMessage = new handleMessage(is, os ,
                    Integer.parseInt(socketListener.myConfig.getProperties("prefixLen")),
                    Integer.parseInt(socketListener.myConfig.getProperties("receiveBufferSize"))
                    ,uuidLine
                    );

            while (true) {
                handleMessage.receiveAndProcessMessage();
            }

        } catch (Exception ex) {
            throw ex;
        } finally {
            if (is != null)
                is.close();
            if (os != null)
                os.close();
        }
    }
}
