package tech.huit.web;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Created by huit on 2017/6/2.
 */
public class ResponseStatus {
    public boolean status;//成功与过与否
    public Object data;//返回的数据
    public String errorCode;//定义一个code是方便沟通
    public String errorMsg;//错误描述

    public ResponseStatus() {
    }

    public ResponseStatus(boolean status) {
        this.status = status;
    }

    public ResponseStatus(boolean status, Object data) {
        this.status = status;
        this.data = data;
    }

    public ResponseStatus(boolean status, String errorCode) {
        this.status = status;
        this.errorCode = errorCode;
    }

    public ResponseStatus(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ResponseStatus(boolean status, String errorCode, String errorMsg) {
        this.status = status;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE, true, true);
    }
}
