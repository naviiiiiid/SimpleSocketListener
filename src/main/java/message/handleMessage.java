package message;

import helper.Utility;
import main.socketListener;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by n-soorani on 2017/03/14.
 */
public class handleMessage {

    public static final Logger logger = Logger.getLogger(handleMessage.class);

    private final int _prefixLen;
    private int _prefixDoneCount;
    private int _prefixThisReceive;

    private int _receivedLength;
    private int offsetToOtherOp = 0;
    private int remainByteToProcess = 0;

    private byte[] _prefixArray;
    private final byte[] _receiveBuffer;
    private byte[] _finalMessage;

    private String clientUniqId;

    private final Object monitor;

    private final InputStream inputStream;
    private OutputStream outStream;

    public handleMessage(InputStream inStreamParam, OutputStream outStreamParam, int perfixLen,
                         int receiveBufferSize, String uuidLineParam ) {

        monitor = new Object();
        inputStream = inStreamParam;
        outStream = outStreamParam;
        _prefixLen = perfixLen;
        _prefixArray = new byte[perfixLen];
        _receivedLength = 0;
        clientUniqId = uuidLineParam;
        _receiveBuffer = new byte[receiveBufferSize];
    }

    public void receiveAndProcessMessage() throws  Exception{
        try
        {
            reset();
            receive();
            do
            {

                if (!handlePrefix())
                {
                    throw new Exception("Error in handle prefix");
                }
                if (!handleBody())
                {
                    throw new Exception("Error in handle body");
                }

                String prefix = new String(_prefixArray, StandardCharsets.UTF_8);
                String messageBody = new String(this._finalMessage , StandardCharsets.UTF_8);

                String totalMessage = prefix + messageBody;

                try {

                    transactionRequest request = new transactionRequest( new Object() , totalMessage ,
                            UUID.randomUUID().toString(), outStream  ,  System.currentTimeMillis(),
                            UUID.randomUUID().toString() );

                    socketListener.transactionQueue.enQueue(request);
                    socketListener.transactionQueue.getTransactionsThreadPoolQueue().wakeUpRoundRobin();

                }
                catch (Exception ex) {
                    logger.error("Exception => " + ex + "|" + clientUniqId);
                    throw ex;
                }

            } while (remainByteToProcess > 0);
        }
        catch (Exception ex)
        {
            throw new Exception("Exception => " + ex + "|" + clientUniqId);
        }

    }

    private void reset() {
        _receivedLength = 0;
        offsetToOtherOp = 0;
    }

    private void receive() throws Exception {
        try {
            _receivedLength = inputStream.read(_receiveBuffer, 0, _receiveBuffer.length);
            if (_receivedLength == -1) {
                throw new Exception("Close socket in remote host");
            }
        } catch (Exception ex) {
            throw new Exception("Exception in receive!!! : " + ex);
        }
    }

    private boolean handleBody() throws  Exception{
        int _messageDoneCount = 0;
        boolean isHandled = false;

        try
        {
            while (_receivedLength > 0)
            {
                if (_messageDoneCount == 0)
                {
                    _prefixDoneCount = 0;
                    int mesLen = Integer.parseInt(new String(this._prefixArray, "UTF-8"));
                    _finalMessage = new byte[mesLen];
                }

                if ((_messageDoneCount + _receivedLength - offsetToOtherOp) - _prefixThisReceive >= _finalMessage.length)
                {
                    System.arraycopy(_receiveBuffer, _prefixThisReceive + offsetToOtherOp, _finalMessage,
                            _messageDoneCount, _finalMessage.length - _messageDoneCount);

                    int messageThisOp = _finalMessage.length - _messageDoneCount;
                    offsetToOtherOp = offsetToOtherOp + messageThisOp + _prefixThisReceive;
                    isHandled = true;

                    if (remainByteToProcess > 0)
                    {
                        remainByteToProcess = remainByteToProcess - messageThisOp - _prefixThisReceive;
                    }
                    else
                    {
                        remainByteToProcess = _receivedLength - messageThisOp - _prefixThisReceive;
                    }
                    break;
                }
                else
                {
                    System.arraycopy(_receiveBuffer, _prefixThisReceive + offsetToOtherOp, _finalMessage,
                            _messageDoneCount, _receivedLength - offsetToOtherOp - _prefixThisReceive);

                    _messageDoneCount += (_receivedLength - offsetToOtherOp - _prefixThisReceive);
                    _prefixThisReceive = 0;
                    offsetToOtherOp = 0;
                    remainByteToProcess = 0;
                    receive();
                }
            }
        }
        catch (Exception ex)
        {
            throw new Exception("Exception in HandleMessage :" + ex);
        }
        return isHandled;

    }

    private boolean handlePrefix() throws Exception {

        _prefixDoneCount = 0;
        _prefixThisReceive = 0;
        boolean isHandled = false;

        try
        {
            while (_receivedLength > 0)
            {
                if (_prefixDoneCount == 0) {

                    _prefixArray = new byte[_prefixLen];
                }
                if (_receivedLength - offsetToOtherOp + _prefixDoneCount >= _prefixLen)
                {
                    System.arraycopy(_receiveBuffer, offsetToOtherOp, _prefixArray,
                            _prefixDoneCount, _prefixLen - _prefixDoneCount);
                    _prefixThisReceive = _prefixLen - _prefixDoneCount;

                    isHandled = true;
                    break;
                }
                else
                {
                    System.arraycopy(_receiveBuffer, offsetToOtherOp, _prefixArray,
                            _prefixDoneCount, _receivedLength - offsetToOtherOp);
                    _prefixDoneCount += (_receivedLength - offsetToOtherOp);

                    offsetToOtherOp = 0;
                    remainByteToProcess = 0;
                    receive();
                }
            }
        }
        catch (Exception ex)
        {
            throw new Exception("Exception in HandlePerfix :" + ex);
        }
        return isHandled;
    }

    public String getClientUniqId() {
        return clientUniqId;
    }

    public void setClientUniqId(String clientUniqId) {
        this.clientUniqId = clientUniqId;
    }

    public Logger getLogger() {
        return logger;
    }

    public Object getMonitor() {
        return monitor;
    }

    public OutputStream getOutStream() {
        return outStream;
    }

    public void setOutStream(OutputStream outStream) {
        this.outStream = outStream;
    }
}
