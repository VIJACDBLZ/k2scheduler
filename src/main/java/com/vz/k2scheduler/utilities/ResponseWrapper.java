package com.vz.k2scheduler.utilities;

import java.io.Serializable;

public class ResponseWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    public static ResponseWrapper failure(String msg) {
        return new ResponseWrapper(false, msg);
    }
    public static ResponseWrapper failure(Exception e) {
        return new ResponseWrapper(false, e.getMessage());
    }
    public static ResponseWrapper failure() {
        return new ResponseWrapper(false);
    }
    public static ResponseWrapper success() {
        return new ResponseWrapper(true);
    }
    public static ResponseWrapper success(String msg) {
        return new ResponseWrapper(true, msg);
    }
    public ResponseWrapper(boolean valid, String msg) {
        super();
        this.valid = valid;
        this.msg = msg;
    }
    public ResponseWrapper(boolean valid) {
        super();
        this.valid = valid;
    }
    public boolean isValid() {
        return valid;
    }
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }

    boolean valid;
    String msg;
    Object data;

    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.valid = true;
        this.data = data;
    }

}
