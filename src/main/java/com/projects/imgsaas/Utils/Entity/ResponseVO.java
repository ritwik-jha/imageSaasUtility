package com.projects.imgsaas.Utils.Entity;

public class ResponseVO {
    private boolean status;
    private String message;
    private Object obj;

    public ResponseVO(boolean status, String message, Object obj){
        this.status = status;
        this.message = message;
        this.obj = obj;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
