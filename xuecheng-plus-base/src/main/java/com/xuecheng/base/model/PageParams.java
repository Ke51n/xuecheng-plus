package com.xuecheng.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * package:  com.xuecheng.base.model
 * project_name:  xuecheng-plus
 * 2023/4/17  21:04
 * description: 分页查询参数
 *
 * @author wk
 * @version 1.0
 */
@Data
@ToString
public class PageParams {
    //当前页码
    @ApiModelProperty(value = "页码")
    private Long pageNo = 1L;
    //每页记录数默认值
    @ApiModelProperty(value = "每页记录数")
    private Long pageSize = 10L;

    public PageParams(Long pageNo, Long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public PageParams() {
    }

}
