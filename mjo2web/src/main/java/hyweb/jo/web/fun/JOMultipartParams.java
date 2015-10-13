package hyweb.jo.web.fun;

import hyweb.jo.org.json.JSONArray;
import hyweb.jo.org.json.JSONObject;
import hyweb.jo.util.ENDE;
import hyweb.jo.JOProcConst;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * @author William
 */
public class JOMultipartParams extends JOWebParams {

    @Override
    public JSONObject exec(JSONObject web) throws Exception {
        HttpServletRequest req = (HttpServletRequest) web.opt(JOProcConst.w_req);
        JSONObject params = new JSONObject();
        String up_base = web.optString(JOProcConst.w_upload);
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List items = upload.parseRequest(req);
        Iterator iterator = items.iterator();

        while (iterator.hasNext()) {
            FileItem item = (FileItem) iterator.next();
            if (item.isFormField()) {
                proc_form_field(params, item);
            } else {
                String fgroup = params.optString("xf_group", null);
                String path = (fgroup != null) ? up_base + "/" + fgroup : up_base;
                fgroup = (fgroup == null) ? "" : fgroup;
                proc_multipart_field(params, new File(path), fgroup, item);
            }
        }
        return params;
    }

    private void proc_form_field(JSONObject params, FileItem item) throws Exception {
        String key = item.getFieldName();
        String value = esapi_get_parameter(item.getString("UTF-8"));
        if (value == null) {
            return;
        }
        if (params.has(key)) {
            Object child = params.opt(key);
            if (child instanceof JSONArray) {
                ((JSONArray) child).put(value);
            } else {
                JSONArray arr = new JSONArray();
                arr.put(child);
                arr.put(value);
                params.put(key, arr);
            }
        } else {
            params.put(key, value);
        }
    }

    private void proc_multipart_field(JSONObject params, File path, String group, FileItem item) throws Exception {
        String fname = item.getName();
        String fext = null;
        String fldId = item.getFieldName();
        String name = null;
        // 修正非預設路徑上傳檔案
        fname = fname.replace('\\', '/');
        int ps = fname.lastIndexOf("/");
        if (ps >= 0) {
            fname = fname.substring(ps + 1);
        }
        int pe = fname.lastIndexOf('.');

        long size = item.getSize();
        if (pe > 0) {
            fext = fname.substring(pe + 1);
            fname = fname.substring(0, pe);
            name = String.valueOf(System.currentTimeMillis()) + "." + fext;
        } else {
            // name = U64.encode(fileName);
            name = String.valueOf(System.currentTimeMillis());
        }

        if (!"".equals(fname)) { // 如果 fname  是空值 表檔案沒有上傳
            if (!path.exists()) {
                path.mkdirs();
            }
            File uploadedFile = new File(path, name);
            if (uploadedFile.isFile() && uploadedFile.exists()) {
                uploadedFile.delete();
            }
            byte[] buf = item.get();
            item.write(uploadedFile);
            String md5 = ENDE.md5(buf);
            JSONObject ext = new JSONObject();
            params.put("$" + fldId + "_ext", ext);
            ext.put("fpath", uploadedFile.getAbsolutePath());
            ext.put("fgroup", group);
            ext.put("flabel", fname);
            ext.put("fsuffix", fext);
            ext.put("fsize", size);
            ext.put("fhash", md5);
            ext.put("fname", name);
            String fld_type = fldId+"_type";
            if(params.has(fld_type)){
                ext.put("ftype", params.opt(fld_type));
            }
            params.put(fldId, fname + "." + fext);

        }
    }

}
