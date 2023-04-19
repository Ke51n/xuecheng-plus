package com.xuecheng.base.exception;

import lombok.Data;

import java.io.Serializable;

/**
 * package:  com.xuecheng.base.exception
 * project_name:  microServiceProject
 * 2023/4/19  12:46
 * description:
 *
 * @author wk
 * @version 1.0
 */
@Data
public class RestErrorResponse implements Serializable {

    private String errMessage;

    public RestErrorResponse(String errMessage) {
        this.errMessage = errMessage;
    }
}