package hyweb.jo.os;

import hyweb.jo.log.JOLogger;
import hyweb.jo.os.ICCCommand;
import hyweb.jo.os.util.ThreadStream;
import java.io.InputStream;

import java.io.OutputStream;
import java.util.Arrays;

/**
 *
 * @author william
 */
public class PBCommand implements ICCCommand {

    @Override
    public void execute(String[] args) throws Exception {
        execute(System.out, args);
    }

    @Override
    public void execute(OutputStream os, String[] args) throws Exception {
        InputStream out  = null ; 
        try{
        JOLogger.info("Execing " + Arrays.toString(args));
        ProcessBuilder pb = new ProcessBuilder(args);
        Process p = pb.start();
        out = p.getInputStream();
        ThreadStream tos = new ThreadStream(out, os);
        tos.start();
        int exitVal = p.waitFor();
        JOLogger.info("ExitValue: " + exitVal);
        } finally{
            if(out!=null){
               out.close();
            }
        }
    }

}
