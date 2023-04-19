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
     * 根据planid找到该条记录
     *
     * @param id
     */
    Teachplan getPlanById(Long id);

    /**
     * 根据 id 删除章
     *
     * @param id
     */
    int deleteTeachplan(Long id);

    /**
     * 查询课程计划树型结构
     *
     * @param courseId
     * @return
     */
    List<TeachplanDto> findTeachplanTree(long courseId);

    /**
     * 保存或修改课程计划 根据SaveTeachplanDto
     *
     * @param teachplan
     */
    void saveTeachplan(SaveTeachplanDto teachplan);

    /**
     * 保存或修改课程计划 根据Teachplan
     *
     * @param teachplan
     */
    void saveTeachplan(Teachplan teachplan);

    /**
     * 根据teachplan表中的id主键字段找到这条记录的子记录数
     *
     * @param id
     * @return
     */
    int findChildNumById(Long id);

    /**
     * 在teachplan表中找到和当前id同级的所有数据
     *
     * @param
     */
    List<Teachplan> findSameLevelPlans(Long courseId, Integer grade, Long parentid);

    /**
     * 根据id找到当前plan的父结点id
     *
     * @param id
     */
    long findParentId(Long id);

}
