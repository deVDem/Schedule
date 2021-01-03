package ru.devdem.reminder.controllers;

import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

import ru.devdem.reminder.BuildConfig;

public class HelperConnection {
    private static final String TAG = "HelperConnection";
    private Socket mSocket;
    private static String mHost ;
    private static int mPort;

    public HelperConnection() {
        mHost = BuildConfig.DEBUG ? "deVDemPC1.devdem.ru" : "schedule.devdem.ru";
        mPort = BuildConfig.DEBUG ? 3480 : 3475;
    }

    public void openConnection() throws Exception {
        closeConnection();
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            mSocket = new Socket(mHost, mPort);
            mSocket.setKeepAlive(true);
        } catch (Exception e) {
            throw new Exception("Error in open socket: "+ Arrays.toString(e.getStackTrace()));
        }
    }

    public boolean connected() {
        if(mSocket!= null) {
            try {
                return mSocket.getKeepAlive();
            } catch (SocketException e) {
                return false;
            }
        }
        else return false;
    }

    public void closeConnection() {
        if (mSocket != null && !mSocket.isClosed()) {
            try {
                mSocket.close();
            } catch (Exception e) {
                Log.e(TAG, "Error in closing socket:"
                        + Arrays.toString(e.getStackTrace()));
            } finally {
                mSocket = null;
            }
        }
        mSocket = null;
    }

    public void sendData(byte[] data) throws Exception {
        if (mSocket == null || mSocket.isClosed()) {
            throw new Exception("Error while sending data. " +
                    "Socket is not created or opened");
        }
        try {
            mSocket.getOutputStream().write(data);
            mSocket.getOutputStream().flush();
        } catch (Exception e) {
            throw new Exception("Error while sending data : "
                    + e.getMessage());
        }
    }

    public InputStream getInputStream() throws Exception {
        return mSocket.getInputStream();
    }

    public OutputStream getOutputStream() throws Exception {
        return mSocket.getOutputStream();
    }


    int off;
    public byte[] getData() throws Exception {
        if (mSocket == null || mSocket.isClosed()) {
            throw new Exception("Error while getting data. " +
                    "Socket is not created or opened");
        }
        try {
            byte[] buffer = new byte[256];
            mSocket.getInputStream().read(buffer, 0, 256);
            return buffer;
        } catch (Exception e) {
            throw new Exception("Error while getting data: "+e.getMessage());
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        closeConnection();
    }
}
