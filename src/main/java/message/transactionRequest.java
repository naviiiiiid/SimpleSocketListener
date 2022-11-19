package message;


import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by n-soorani on 2017/03/18.
 */


public class transactionRequest {

    private final Object monitor;

    
    private final String message;

    
    private final String uuid;

    
    private final String connectionUuid;

    private final OutputStream os;

    
    private final long enqTime;

    public transactionRequest(){
        this.monitor = null;
        this.message = null;
        this.uuid = null;
        this.os = null;
        this.enqTime = 0;
        this.connectionUuid = null;
    }

    public transactionRequest(Object monitor, String message, String uuid, OutputStream os, long enqTimeParam, String connectionUuid) {
        this.monitor = monitor;
        this.message = message;
        this.uuid = uuid;
        this.os = os;
        this.enqTime = enqTimeParam;
        this.connectionUuid = connectionUuid;
    }


    public Object getMonitor() {
        return monitor;
    }

    public String getMessage() {
        return message;
    }

    public String getUuid() {
        return uuid;
    }

    public String getConnectionUuid() {
        return connectionUuid;
    }

    public OutputStream getOs() {
        return os;
    }

    public long getEnqTime() {
        return enqTime;
    }
}
