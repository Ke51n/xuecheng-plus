package com.xuecheng.content.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * package:  com.xuecheng.content.model.dto
 * project_name:  microServiceProject
 * 2023/4/19  15:55
 * description: 保存课程计划dto，包括新增、修改
 *
 * @author wk
 * @version 1.0
 */
@Data
@ToString
public class SaveTeachplanDto {

    /***
     * 教学计划id
     */
    private Long id;

    /**
     * 课程计划名称
     */
    private String pname;

    /**
     * 课程计划父级Id
     */
    private Long parentid;

    /**
     * 层级，分为1、2、3级
     */
    private Integer grade;

    /**
     * 课程类型:1视频、2文档
     */
    private String mediaType;


    /**
     * 课程标识
     */
    private Long courseId;

    /**
     * 课程发布标识
     */
    private Long coursePubId;


    /**
     * 是否支持试学或预览（试看）
     */
    private String isPreview;


}
