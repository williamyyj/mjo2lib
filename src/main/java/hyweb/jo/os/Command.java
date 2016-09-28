package hyweb.jo.os;

import java.io.ByteArrayOutputStream;

/**
 * @author william
 */
public class Command {

    public static void main(String[] args) throws Exception {
        String[] p = new String[]{"cmd", "/c", "netstat", "-an"};
        ICCCommand cmd = new PBCommand();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        cmd.execute(bos, p);
        bos.close();
        System.out.println(bos);
    }
    
}
