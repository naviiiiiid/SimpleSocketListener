package message;

import main.socketListener;
import org.apache.log4j.Logger;
import thread.ManualResetEvent;

import java.net.Socket;

/**
 * Created by n-soorani on 2017/03/12.
 */
public class handleClient  implements  Runnable {

    public  Object threadParam;
    private final int threadIndex ;
    private ManualResetEvent manualResetEvent;

    public static final Logger logger = Logger.getLogger(handleClient.class);

    public handleClient(int threadIndex , Object param) {
        setManualResetEvent(new ManualResetEvent(false));
        this.threadIndex = threadIndex;
        this.threadParam = param;
        Thread myThread = new Thread(this, "handleClient" + this.threadIndex);
        myThread.start();
        logger.info("handleClient" + threadIndex + " is Created");
    }

    public void run() {

        try {

            while (true) {
                try {
                    getManualResetEvent().reset();

                    if (threadParam != null) {
                        Socket socketClientParam = (Socket) threadParam;
                        try {

                            handleClientMessage handleClientMessage = new
                                    handleClientMessage(socketClientParam);
                            handleClientMessage.handle();

                        } catch (Exception ex) {

                            logger.error("ex=> " + ex);
                            threadParam = null;
                            socketClientParam.close();
                        }
                    } else {
                        logger.info("Sleep Thread");
                        threadParam = null;
                        socketListener.freeIndexStack.push(this.threadIndex);
                        getManualResetEvent().waitOne();
                        logger.info("Wake UP Thread");
                    }
                } catch (Exception ex) {
                    threadParam = null;
                    logger.error(ex);
                }
            }
        }
        catch (Exception ex){
            logger.error(ex.toString());
        }
    }

    public ManualResetEvent getManualResetEvent() {
        return manualResetEvent;
    }

    public void setManualResetEvent(ManualResetEvent manualResetEvent) {
        this.manualResetEvent = manualResetEvent;
    }
}





