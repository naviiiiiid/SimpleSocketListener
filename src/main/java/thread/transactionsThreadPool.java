package thread;

import java.util.ArrayList;

/**
 * Created by n-soorani on 2017/03/24.
 */
public class transactionsThreadPool{

    private final Object monitor  = new Object();
    private Integer currentIndex = -1;

    private ArrayList<transactionThread> threads = null;

    public transactionsThreadPool(ArrayList<transactionThread> ThreadsParam ) {
        threads = ThreadsParam;
    }

    public void startThreadPool() {
        for (int i = 0; i < threads.size(); i++)
            threads.get(i).start();
    }

    private Integer getCurrentIndexRoundRobin() {
        Integer tempIndex;
        try {
            synchronized (monitor) {
                if (currentIndex >= (threads.size() - 1)) {

                    currentIndex = -1;
                }
                currentIndex++;
                tempIndex = currentIndex;
            }
        } catch (Exception ex) {
            throw ex;
        }
        return tempIndex;
    }

    public Integer wakeUpRoundRobin() {
        Integer tempCurrentIndex = -1;
        tempCurrentIndex = getCurrentIndexRoundRobin();
        threads.get(tempCurrentIndex).getManualResetEvent().set();
        return tempCurrentIndex;
    }

}
