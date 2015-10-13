/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hyweb.jo.web.fun;

import hyweb.jo.IJOFunction;
import hyweb.jo.org.json.JSONObject;
import hyweb.jo.web.JOWebObject;

/**
 *
 * @author William
 */
public class FVerfiyCode implements IJOFunction<Boolean, JOWebObject> {

    @Override
    public Boolean exec(JOWebObject web) throws Exception {
        JSONObject params = web.params();
        String valid = params.optString("VerifyCode", null);
        String s_valid = (String) web.session("$valid");
        return (valid!=null && valid.equals(s_valid)) ; 
    }

}
