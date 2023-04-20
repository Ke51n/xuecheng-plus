package com.xuecheng.base.exception;

/**
 * package:  com.xuecheng.base.exception
 * project_name:  microServiceProject
 * 2023/4/19  12:43
 * description: 学成在线项目异常类
 *
 * @author wk
 * @version 1.0
 */
public class XueChengPlusException extends RuntimeException {


    private String errMessage;

    public XueChengPlusException() {
        super();
    }

    public XueChengPlusException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    //抛出自定义异常 给前端，具体异常存放在 errMessage 里
    public static void cast(CommonError commonError){
        throw new XueChengPlusException(commonError.getErrMessage());
    }
    public static void cast(String errMessage){
        throw new XueChengPlusException(errMessage);
    }

}