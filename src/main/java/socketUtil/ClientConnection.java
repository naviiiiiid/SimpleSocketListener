package socketUtil;

import helper.Utility;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.UUID;

/**
 * Created by n-soorani on 2017/03/15.
 */
public class ClientConnection {

    private Socket _socket;
    private int _perfixLen;
    private byte[] _perfix;
    private byte[] _receiveBuffer;
    private int _recieveBufferSize;

    private String _ipAddr;
    private Integer _port;

    private int _perfixDoneCount;
    private int _messageDoneCount;
    private int _perfixThisReceive;
    private byte[] _finalMessage;

    private UUID uuid;
    private Logger _logger;
    private boolean debugEnable;

    private InputStream in;
    private OutputStream os;

    public ClientConnection(String ipAddr, int port, int perfixLen, int buffersizeReceive, int timeout, Logger loggerHelper , String debugEnable) throws IOException {
        try {
            _perfixLen = perfixLen;
            _ipAddr = ipAddr;
            _port = port;
            _socket = new Socket(_ipAddr, _port);
            _socket.setSoTimeout(timeout);
            _perfixDoneCount = 0;
            _perfix = new byte[_perfixLen];
            _recieveBufferSize = buffersizeReceive;
            uuid = UUID.randomUUID();
            _receiveBuffer = new byte[_recieveBufferSize];
            _logger = loggerHelper;
            this.debugEnable = Boolean.parseBoolean(debugEnable);
        } catch (Exception ex) {
            closeSocket();
            throw ex;
        }
    }


    private void setStream()throws Exception {

        try {
            in = new DataInputStream(_socket.getInputStream());
            os = new DataOutputStream(_socket.getOutputStream());
            this._logger.info("setStream to IP:" + _ipAddr + " port: " + _port + "|" + uuid);
        } catch (Exception ex) {
            if (in != null)
                in.close();
            if (os != null)
                os.close();
            throw ex;
        }

    }


    public String sendRequestISOMessage(byte[] buffer)throws Exception
    {
        try
        {
            setStream();
            sendToListener(buffer);
            receiveFromListener();

            String perfix = new String(_perfix , "UTF-8");
            String messageBody = new String(_finalMessage , "UTF-8");

            return perfix + messageBody;
        }
        catch (Exception ex)
        {
            closeSocket();
            throw ex;
        }

        finally {
            if (in != null)
                in.close();
            if (os != null)
                os.close();
            if(_socket != null){
                _socket.close();
            }
        }
    }


    public String sendRequestMessage(String message)throws Exception
    {
        try
        {
            String finalMessageWithPerfix = Utility.LPad((message.length())+""  , _perfixLen , '0') + message;
            setStream();
            sendToListener(finalMessageWithPerfix.getBytes("UTF-8"));
            receiveFromListener();
            String perfix = new String(_perfix , "UTF-8");
            String messageBody = new String(_finalMessage , "UTF-8");
            return perfix + messageBody;
        }
        catch (Exception ex)
        {
            closeSocket();
            throw ex;
        }
        finally {
            if (in != null)
                in.close();
            if (os != null)
                os.close();
            if(_socket != null){
                _socket.close();
            }
        }
    }


    private int sendToListener(byte[] _buffer)throws Exception
    {

        int byteToSend = 0;
        try
        {
            String finalMessageToSend  = "";
            os.write(_buffer);
            if (this.debugEnable)
            {
                finalMessageToSend = "Message send To listener : " + new String(_buffer , "UTF-8");
            }
            _logger.info(finalMessageToSend+ "|" + uuid);
        }
        catch (Exception ex)
        {
            throw ex;
        }
        return byteToSend;
    }


    private void receiveFromListener() throws Exception {
        try {
            String finalMessage = "";

            int byteReceived = receive();
            if (byteReceived > 0) {
                if (!handlePerfix(byteReceived)) {
                    throw new Exception("Error in read perfix from buffer");
                }

                if (!handleMessage(byteReceived)) {
                    throw new Exception("Error in read message from buffer");
                }

                if (this.debugEnable) {
                    finalMessage = "final receive Message is: " + new String(_finalMessage, "UTF-8");

                    _logger.info(finalMessage);
                }

                normalClose();


            } else {
                throw new Exception("byteReceived==0 from Listener!!!");
            }
        } catch (Exception ex)
        {
            throw ex;
        }
    }


    private boolean handlePerfix(int byteReceive)throws Exception
    {
        boolean IsReady = false;
        try
        {
            while (byteReceive > 0)
            {
                _perfixThisReceive = 0;
                if (byteReceive + _perfixDoneCount >= _perfixLen)
                {
                    System.arraycopy(_receiveBuffer, 0, _perfix, _perfixDoneCount, _perfixLen - _perfixDoneCount);
                    _perfixThisReceive = _perfixLen - _perfixDoneCount;
                    _perfixDoneCount = _perfixLen;
                    IsReady = true;
                    break;
                }
                else
                {
                    System.arraycopy(_receiveBuffer, 0, _perfix, _perfixDoneCount, byteReceive);
                    _perfixDoneCount += byteReceive;
                    _perfixThisReceive = byteReceive;
                    IsReady = false;
                    byteReceive = receive();
                }
            }
        }
        catch (Exception ex)
        {
            throw new Exception("Exception in HandlePerfix :" + ex);
        }
        return IsReady;
    }


    private boolean handleMessage(int receiveLen) throws Exception
    {
        boolean isReadMess = false;
        String strMessLen = "";
        try
        {
            while (receiveLen > 0)
            {
                if (_messageDoneCount == 0)
                {
                    strMessLen = new String(_perfix , "UTF-8");
                    int mesLen = Integer.parseInt(strMessLen);
                    _finalMessage = new byte[mesLen];
                }
                if (_messageDoneCount + receiveLen >= _finalMessage.length)
                {
                    isReadMess = true;
                    System.arraycopy(_receiveBuffer, _perfixThisReceive, _finalMessage,
                            _messageDoneCount, receiveLen - _perfixThisReceive);
                    break;
                }
                else
                {
                    System.arraycopy(_receiveBuffer, _perfixThisReceive, _finalMessage,
                            _messageDoneCount, receiveLen - _perfixThisReceive);
                    _messageDoneCount += (receiveLen - _perfixThisReceive);
                    _perfixThisReceive = 0;
                    receiveLen = receive();
                }
            }
        }
        catch (Exception ex)
        {
            throw new Exception("Exception in HandleMessage :" + ex + "| Perfix is: " + strMessLen);
        }
        return isReadMess;
    }


    private int receive()throws Exception {
        return in.read(_receiveBuffer);
    }


    private void normalClose() throws Exception {
        try {
            _socket.shutdownInput();
            _socket.shutdownOutput();
            _socket.setSoLinger(true, 0);
            _socket.close();
        } catch (Exception ex) {
            throw ex;
        }
    }


    private void closeSocket()
    {
        try
        {
            _socket.setSoLinger(true , 0);
            _socket.close();
            this._logger.error("closeSocket IP:" + this._ipAddr + " port: " + this._port);
        }
        catch (Exception ex)
        {
            _socket = null;
        }
    }

}
