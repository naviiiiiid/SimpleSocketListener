package thread;

/**
 * Created by n-soorani on 2017/03/12.
 */
public class ManualResetEvent {
    private final Object monitor = new Object();
    private volatile boolean open = false;

    public ManualResetEvent(boolean open) {
        this.open = open;
    }

    public void waitOne() throws InterruptedException {

        synchronized (monitor) {;
            while (!open) {
                monitor.wait();
            }
        }
    }

    public boolean waitOne(long miliSeconds) throws InterruptedException {
        synchronized (monitor) {
            if (open)
                return true;
            monitor.wait(miliSeconds);
            return open;
        }
    }

    public void set() {
        synchronized (monitor) {
            open = true;
            monitor.notifyAll();
        }

    }

    public void reset() {
        open = false;
    }
}
