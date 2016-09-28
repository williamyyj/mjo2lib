package hyweb.jo.os;

import java.io.OutputStream;

/**
 * @author william
 */
public interface ICCCommand {

    public void execute(String[] args) throws Exception;

    public void execute(OutputStream os, String[] args) throws Exception;

}
