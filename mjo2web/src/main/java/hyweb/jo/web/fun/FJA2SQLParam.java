/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyweb.jo.web.fun;

import hyweb.jo.IJOFunction;
import hyweb.jo.org.json.JSONArray;

/**
 *
 * @author William
 */
public class FJA2SQLParam implements IJOFunction<String, Object[]> {

    public String exec(Object... args) throws Exception {
        Object m =  args[0];
        String dt =((args.length>=2) ?  (String) args[1] : "string") ; 
        if ( m instanceof JSONArray){
            return exec((JSONArray)m,dt);
        } else if (m!=null) {
            if("string".equals(dt)){
                return "('"+ m + "')" ; 
            } else {
                return "(" + m  + ")";
            }
        }
        return null;
    }

    private String exec(JSONArray ja, String dt) {
        StringBuilder sb = new StringBuilder();
        if(ja!=null){
            sb.append('(');
            for(int i=0; i<ja.length();i++){
                to_sql_param(sb,dt,ja.opt(i));
            }
            sb.setLength(sb.length()-1);
            sb.append(')');
        }
        return sb.toString();
    }

    private void to_sql_param(StringBuilder sb, String dt, Object o) {
        if ("string".equals(dt)) {
            sb.append('\'').append(o).append('\'').append(',');
        } else {
            sb.append(o).append(',');
        }
    }
}
