package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;

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
     * 根据 id 删除章
     * @param id
     */
    int deleteTeachplan(String id);

    /**
     * 查询课程计划树型结构
     *
     * @param courseId
     * @return
     */
    List<TeachplanDto> findTeachplanTree(long courseId);

    /**
     * 保存或修改课程计划
     *
     * @param teachplan
     */
    void saveTeachplan(SaveTeachplanDto teachplan);
}
