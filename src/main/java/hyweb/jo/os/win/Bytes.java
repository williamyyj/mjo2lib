package hyweb.jo.os.win;

//  該實現類中需要用到一個自己編寫byte的工具類，該類的代碼如下所示 :

public class Bytes {  
    public static String substring(String src, int start_idx, int end_idx){  
        byte[] b = src.getBytes();  
        String tgt = "";  
        for(int i=start_idx; i<=end_idx; i++){  
            tgt +=(char)b[i];  
        }  
        return tgt;  
    }  
} 
