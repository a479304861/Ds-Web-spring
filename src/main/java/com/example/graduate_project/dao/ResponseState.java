package com.example.graduate_project.dao;

public enum ResponseState {
   SUCCESS("操作成功",true,20000),
   FAILED("操作失败",false,40000),
   GET_RESOURCE_FAILED("操作失败",false,40001),
   ACCOUNT_NOT_LOGIN("账号未登入",false,40002),
   PERMISSION_FORBID("没有权限",false,40003),
   USER_NOT_EXIST("用户不存在",false,40004),
   USER_DENIAL("用户已被禁止",false,40005),
   ACCOUNT_NOT_BE_NONE("账户不可以为空",false,40006),
   PASSWORD_NOT_BE_NONE("密码不可以为空",false,40007),
   PASSWORD_MISTAKE("密码错误",false,40008),
   ACCOUNT_IS_EXIST("账号存在",false,40009),
   CAPTCHA_CODE_MISTAKE("验证码不正确",false,40010),
   CAPTCHA_CODE_OVERTIME("验证码已过期",false,40011),
   ERROR_404("404",false,40012),
   ERROR_405("405",false,40013),
   ERROR_504("504",false,40014),
   ERROR_505("505",false,40015),
   ERROR_403("403",false,40016),

   ;

   ResponseState(String message, boolean success, int code) {
      this.message = message;
      this.success = success;
      this.code = code;
   }

   private String message;
   private boolean success;
   private int code;

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
}
