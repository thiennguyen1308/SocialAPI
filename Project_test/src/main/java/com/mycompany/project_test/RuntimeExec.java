package com.mycompany.project_test;

/**
 *
 * @author root
 */
//<editor-fold defaultstate="collapsed" desc="IMPORT">
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//</editor-fold>

/**
 *
 * @author LeDinhTuan
 */
public class RuntimeExec {

    //<editor-fold defaultstate="collapsed" desc="GET STREAM WRAPPER">
    public StreamWrapper getStreamWrapper(InputStream is, String type) {
        return new StreamWrapper(is, type);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="STREAM WRAPPER">
    public class StreamWrapper extends Thread {

        InputStream is = null;
        String type = null;
        String message = null;

        public String getMessage() {
            return message;
        }

        StreamWrapper(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        @Override
        public void run() {
            StringBuilder buffer = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                    buffer.append("\n");
                }
                message = buffer.toString();
            } catch (IOException e) {
            }
        }
    }
    //</editor-fold>

}
