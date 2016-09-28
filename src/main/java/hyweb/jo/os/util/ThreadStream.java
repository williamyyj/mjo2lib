package hyweb.jo.os.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 *
 * @author william
 */
public class ThreadStream extends Thread {

    private InputStream is;
    private OutputStream os;
    public final static int buf_size = 4096;
    private final static  int EOF = -1;
    
    public ThreadStream(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[8192];
            int count = 0;
            int num = 0;
            while (EOF != (num = is.read(buf))) {
                os.write(buf, 0, num);
                count += num;
            }
            os.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
