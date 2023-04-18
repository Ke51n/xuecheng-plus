package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * package:  com.xuecheng.content.service
 * project_name:  microServiceProject
 * 2023/4/18  22:04
 * description:
 *
 * @author wk
 * @version 1.0
 */
public interface CourseCategoryService {
    /**
     * 课程分类树形结构查询
     *
     * @return
     */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
