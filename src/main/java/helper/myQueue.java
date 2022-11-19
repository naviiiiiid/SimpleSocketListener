package helper;

import message.handleMessage;
import thread.transactionsThreadPool;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by n-soorani on 2017/03/15.
 */
public class myQueue<T> {

    public static final Logger logger = Logger.getLogger(myQueue.class);

    private final Queue<T> transactionQueue;
    private final Object monitor = new Object();
    private final int maxSize;

    private transactionsThreadPool transactionsThreadPoolQueue;

    public myQueue(Integer maxQueueSize , transactionsThreadPool  transactionsThreadPoolQueueParam) {
        transactionQueue = new LinkedList<>();
        this.maxSize = maxQueueSize;
        this.transactionsThreadPoolQueue = transactionsThreadPoolQueueParam;
    }

    public void enQueue(T item) {
        if (!isBusy()) {
            synchronized (monitor) {
                transactionQueue.add(item);
            }
            logger.info("enQueue : item =>" + item);
        } else {
            logger.info("isBusy => True =>" + item);
        }
    }

    public T deQueue() {
        T item = null;
        synchronized (monitor) {
            item = transactionQueue.poll();
        }
        if(item == null){
            logger.info("Queue is Empty");
        }else{
            logger.info("item =>" + item);
        }
        return item;
    }

    public boolean isBusy() {

        synchronized (monitor) {
            return transactionQueue.size() >= this.maxSize;
        }
    }

    public transactionsThreadPool getTransactionsThreadPoolQueue() {
        return transactionsThreadPoolQueue;
    }

    public void setTransactionsThreadPoolQueue(transactionsThreadPool transactionsThreadPoolQueue) {
        this.transactionsThreadPoolQueue = transactionsThreadPoolQueue;
    }
}