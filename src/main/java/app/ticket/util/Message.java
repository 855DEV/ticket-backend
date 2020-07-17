package app.ticket.util;

import com.alibaba.fastjson.JSONObject;

public class Message extends JSONObject {
    private int code;
    private String message;

    public Message(int code, String message) {
        this.code = code;
        this.message = message;
        super.put("code", code);
        super.put("message", message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
