package com.xiyuan.sense.util;

import java.util.HashMap;

/**
 * Created by xiyuan_fengyu on 2017/7/30.
 */
public class ResponseUtil {

    public static HashMap<String, Object> success(String message) {
        return create(true, null, message, null);
    }

    public static HashMap<String, Object> success(String message, Object data) {
        return create(true, null, message, data);
    }

    public static HashMap<String, Object> fail(String error, String message, Object data) {
        return create(false, error, message, data);
    }

    public static HashMap<String, Object> fail(String error, String message) {
        return create(false, error, message, null);
    }

    public static HashMap<String, Object> fail(String message) {
        return create(false, null, message, null);
    }

    public static HashMap<String, Object> create(boolean success, String error, String message, Object data) {
        HashMap<String, Object> res = new HashMap<>();
        res.put("success", success);
        if (error != null) {
            res.put("error", error);
        }
        res.put("message", message);
        if (data != null) {
            res.put("data", data);
        }
        return res;
    }

}
