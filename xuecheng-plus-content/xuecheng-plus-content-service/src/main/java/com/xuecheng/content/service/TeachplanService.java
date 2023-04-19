package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

/**
 * package:  com.xuecheng.content.service
 * project_name:  microServiceProject
 * 2023/4/19  15:38
 * description:
 *
 * @author wk
 * @version 1.0
 */
public interface TeachplanService {
    /**
     *  查询课程计划树型结构
     * @param courseId
     * @return
     */
    public List<TeachplanDto> findTeachplanTree(long courseId);

}
