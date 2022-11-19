package thread;

import main.socketListener;
import message.handleMessage;
import message.transactionRequest;
import org.apache.log4j.Logger;
import wrapper.wrapperService;

import java.nio.charset.StandardCharsets;

/**
 * Created by n-soorani on 2017/03/15.
 */
public class transactionThread implements Runnable {

    public static final Logger logger = Logger.getLogger(handleMessage.class);

    private static final Integer  currentIndex = -1;

    private Thread myThread;
    private Integer threadIndex;
    private ManualResetEvent ManualResetEvent;

    public transactionThread(Integer indexParam) {
        threadIndex = indexParam;
        setManualResetEvent(new ManualResetEvent(false));
        myThread = new Thread(this, "transactionThread" + threadIndex);
    }


    public void start(){
        myThread.start();
    }

    @Override
    public void run() {

        while (true) {
            try {
                getManualResetEvent().reset();
                transactionRequest item = (transactionRequest) socketListener.transactionQueue.deQueue();

                if (item != null) {
                    try {

                        Integer queueTimeout = Integer.parseInt(socketListener.myConfig.getProperties("QueueTimeout"));
                        long timeWait = System.currentTimeMillis() - item.getEnqTime();
                        if (timeWait >= queueTimeout) {
                            throw new Exception("Queue TimeOut!!! + Time Wait is : " + timeWait +
                                    "|" + item.getUuid() + "|" + item.getConnectionUuid());
                        }

                        String responseFromClient = wrapperService.sendToService(item.getMessage());

                        logger.info("response from Client => " + responseFromClient
                                + "|" + item.getUuid() + "|" + item.getConnectionUuid());
                        write(responseFromClient, item);

                    } catch (Exception ex) {
                        logger.info("ex=> " + ex + "|" + item.getUuid());
                    }
                } else {
                    logger.info("Sleep Thread : " + threadIndex);
                    getManualResetEvent().waitOne();
                    logger.info("Wake UP  Thread : " + threadIndex);
                }
            } catch (Exception ex) {
                logger.error("Error in Thread");
            }
        }
    }

    public void write(String response , transactionRequest req) {
        try {

            if (response.equals("")) {
                throw new Exception("response is null!!" + "|" + req.getUuid());
            }

            synchronized (req.getMonitor()) {
                byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
                req.getOs().write(responseBytes, 0, responseBytes.length);
            }

        } catch (Exception ex) {
            logger.error("ex : " + ex + "|" + req.getUuid());
        }
    }

    public thread.ManualResetEvent getManualResetEvent() {
        return ManualResetEvent;
    }

    public void setManualResetEvent(thread.ManualResetEvent manualResetEvent) {
        ManualResetEvent = manualResetEvent;
    }

}
