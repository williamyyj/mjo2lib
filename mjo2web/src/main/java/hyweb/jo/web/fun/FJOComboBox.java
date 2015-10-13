package hyweb.jo.web.fun;

import hyweb.jo.IJOFunction;
import hyweb.jo.db.DB;
import hyweb.jo.log.JOLogger;
import hyweb.jo.org.json.JSONArray;
import hyweb.jo.org.json.JSONObject;
import hyweb.jo.util.JOCache;
import hyweb.jo.web.JOWebObject;
import java.util.HashMap;
import java.util.List;

public class FJOComboBox implements IJOFunction<String, JOWebObject> {

    private static HashMap<String, List<JSONObject>> cache;

    public String exec(JOWebObject web) throws Exception {
        String id = web.params().optString("id", null);
        String pk = web.params().optString("pk", null);
        String dv = web.params().optString("dv", null);
        boolean reload = web.params().optBoolean("reload");
        return (pk == null) ? combo(web.db(), reload, id, dv) : combo(web.db(), reload, id, dv, pk);
    }

    private List<JSONObject> cache_rows(DB db, boolean reload, String id, String sql, Object... params) throws Exception {
        if (cache == null) {
            cache = new HashMap<String, List<JSONObject>>();
        }

        List<JSONObject> rows = cache.get(key(id, params));
        if (rows == null || reload) {
            rows = (List<JSONObject>) ((params == null) ? db.rows(sql) : db.rows(sql, params));
            cache.put(key(id, params), rows);
            JOLogger.debug("load from db : " + key(id, params));
        } else {
            JOLogger.debug("load from cache : " + key(id, params));
        }
        return rows;
    }

    public JSONArray ja(DB db, boolean reload, String id, String dv, Object... params) throws Exception {
        JSONArray ret = new JSONArray();
        JSONObject jq = JOCache.load(db.base() + "/combo", id);
        JSONArray ops = jq.optJSONArray("option");
        // inject 
        if (ops != null) {
            for (int i = 0; i < ops.length(); i++) {
                JSONObject item = ops.optJSONObject(i);
                combo_json(ret, item.optString("key"), item.optString("value"), dv);
            }
        }
        String sql = jq.optString("@sql");
        String key = jq.optString("@key");
        String value = jq.optString("@value");
        List<JSONObject> rows = cache_rows(db, reload, id, sql, params);
        if (rows != null) {
            for (JSONObject row : rows) {
                String kv = row.optString(key);
                String vv = row.optString(value);
                combo_json(ret, kv, vv, dv);
            }
        }
        return ret;
    }

    private String combo(DB db, boolean reload, String id, String dv, Object... params) throws Exception {
        StringBuilder sb = new StringBuilder();
        JSONObject jq = JOCache.load(db.base() + "/combo", id);
        JSONArray ops = jq.optJSONArray("option");
        // inject 
        if (ops != null) {
            for (int i = 0; i < ops.length(); i++) {
                JSONObject item = ops.optJSONObject(i);
                combo_item(sb, item.optString("key"), item.optString("value"), dv);
            }
        }
        String sql = jq.optString("@sql");
        String key = jq.optString("@key");
        String value = jq.optString("@value");
        List<JSONObject> rows = cache_rows(db, reload, id, sql, params);
        if (rows != null) {
            for (JSONObject row : rows) {
                String kv = row.optString(key);
                String vv = row.optString(value);
                combo_item(sb, kv, vv, dv);
            }
        }
        return sb.toString();
    }

    private void combo_item(StringBuilder sb, String kv, String vv, String dv) {
        if (kv.equals(dv)) {
            sb.append(String.format("<option value='%s' selected>%s</option>\r\n", kv, vv));
        } else {
            sb.append(String.format("<option value='%s'>%s</option>\r\n", kv, vv));
        }
    }

    private void combo_json(JSONArray ret, String kv, String vv, String dv) {
        JSONObject item = new JSONObject() ; 
        item.put("value", kv);
        item.put("label", vv);
        ret.put(item);
    }

    private String key(String id, Object... params) {
        StringBuilder sb = new StringBuilder();
        if (params == null || params.length == 0) {
            sb.append(id.trim());
        } else {
            sb.append(id.trim()).append("_");
            for (Object p : params) {
                sb.append(p).append("_");
            }
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

}
