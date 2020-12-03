package com.example.graduate_project.dao.enity;

public class ResponseResult {
    private String message;
    private boolean success;
    private int code;
    private Object data;

    public ResponseResult(ResponseState responseState) {
        this.message = responseState.getMessage();
        this.code = responseState.getCode();
        this.success = responseState.isSuccess();
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public ResponseResult setData(Object data) {
        this.data = data;
        return this;
    }

    public ResponseResult(String message, boolean success, int code) {
        this.message = message;
        this.success = success;
        this.code = code;
    }

    public static ResponseResult SUCCESS() {
        return new ResponseResult(ResponseState.SUCCESS);
    }

    public static ResponseResult SUCCESS(String message) {
        return new ResponseResult(message, true, 20000);
    }


    public static ResponseResult PERMISSION_FORBID() {
        return new ResponseResult(ResponseState.PERMISSION_FORBID);
    }

    public static ResponseResult ACCOUNT_NOT_LOGIN() {
        return new ResponseResult(ResponseState.ACCOUNT_NOT_LOGIN);
    }

    public static ResponseResult GET_RESOURCE_FAILED() {
        return new ResponseResult(ResponseState.GET_RESOURCE_FAILED);
    }


    public static ResponseResult FAILED() {
        return new ResponseResult(ResponseState.FAILED);
    }

    public static ResponseResult USER_NOT_EXIST() {
        return new ResponseResult(ResponseState.USER_NOT_EXIST);
    }

    public static ResponseResult USER_DENIAL() {
        return new ResponseResult(ResponseState.USER_DENIAL);
    }

    public static ResponseResult ACCOUNT_NOT_BE_NONE() {
        return new ResponseResult(ResponseState.ACCOUNT_NOT_BE_NONE);
    }

    public static ResponseResult PASSWORD_NOT_BE_NONE() {
        return new ResponseResult(ResponseState.PASSWORD_NOT_BE_NONE);
    }

    public static ResponseResult FAILED(String message) {
        return new ResponseResult(message, false, 40000);
    }

    public static ResponseResult PASSWORD_MISTAKE() {
        return new ResponseResult(ResponseState.PASSWORD_MISTAKE);
    }

    public static ResponseResult ACCOUNT_IS_EXIST() {
        return new ResponseResult(ResponseState.ACCOUNT_IS_EXIST);
    }

    public static ResponseResult CAPTCHA_CODE_MISTAKE() {
        return new ResponseResult(ResponseState.CAPTCHA_CODE_MISTAKE);
    }

    public static ResponseResult CAPTCHA_CODE_OVERTIME() {
        return new ResponseResult(ResponseState.CAPTCHA_CODE_OVERTIME);
    }

    public static ResponseResult ERROR_404() {
        return new ResponseResult(ResponseState.ERROR_404);
    }

    public static ResponseResult ERROR_405() {
        return new ResponseResult(ResponseState.ERROR_405);
    } public static ResponseResult ERROR_403() {
        return new ResponseResult(ResponseState.ERROR_403);
    }

    public static ResponseResult ERROR_504() {
        return new ResponseResult(ResponseState.ERROR_504);
    }

    public static ResponseResult ERROR_505() {
        return new ResponseResult(ResponseState.ERROR_505);
    }
}
