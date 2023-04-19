package com.xuecheng.base.exception;

import lombok.Data;

/**
 * package:  com.xuecheng.base.exception
 * project_name:  microServiceProject
 * 2023/4/19  12:42
 * description: 通用错误信息
 *
 * @author wk
 * @version 1.0
 */
public enum CommonError {

    UNKOWN_ERROR("执行过程异常，请重试。"),
    PARAMS_ERROR("非法参数"),
    OBJECT_NULL("对象为空"),
    QUERY_NULL("查询结果为空"),
    REQUEST_NULL("请求参数为空");

    private String errMessage;

    public String getErrMessage() {
        return errMessage;
    }

    private CommonError( String errMessage) {
        this.errMessage = errMessage;
    }

}
