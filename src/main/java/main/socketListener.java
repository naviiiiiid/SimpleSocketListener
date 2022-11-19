package main;

import helper.myConfig;
import helper.myQueue;
import helper.myStack;
import listener.Listener;
import message.handleClient;
import message.transactionRequest;
import org.apache.log4j.*;
import thread.transactionThread;
import thread.transactionsThreadPool;

import java.util.*;

/**
 * Created by n-soorani on 2017/03/15.
 */
public class socketListener {

    public static final Logger logger = Logger.getLogger(socketListener.class);
    public static myStack<Integer> freeIndexStack;
    public static helper.myConfig myConfig;
    public static ArrayList<handleClient> handleClientList;
    public static myQueue<transactionRequest> transactionQueue;
    public static thread.transactionsThreadPool transactionsThreadPool;
    public static ArrayList<transactionThread> transactionThreadArray;


    public static void main(String[] args) throws Exception {

        try {

            myConfig = new myConfig("config.properties");

            String ThreadCount = myConfig.getProperties("AcceptThreadCount");
            String ThreadTransactionCount = myConfig.getProperties("TransactionThreadCount");
            String maxSizeQueue = myConfig.getProperties("maxSizeQueue");
            String QueueTimeout = myConfig.getProperties("QueueTimeout");

            freeIndexStack = new myStack<>(logger);
            handleClientList = new ArrayList<>();

            for (int counter = 0; counter < Integer.parseInt(ThreadCount); counter++) {
                handleClient handleClient = new handleClient(counter, null);
                handleClientList.add(handleClient);
            }


            transactionThreadArray = new ArrayList<>();
            for (int counter = 0; counter < Integer.parseInt(ThreadTransactionCount); counter++) {
                transactionThread transactionThread = new transactionThread(counter);
                transactionThreadArray.add(transactionThread);
            }

            transactionsThreadPool = new transactionsThreadPool(transactionThreadArray);
            transactionsThreadPool.startThreadPool();

            transactionQueue = new myQueue<>(Integer.parseInt(maxSizeQueue), transactionsThreadPool,
                    Integer.parseInt(QueueTimeout));

            new Listener();
        }
        catch (Exception ex){
            logger.error(ex.toString());
            throw  ex;
        }

    }
}
