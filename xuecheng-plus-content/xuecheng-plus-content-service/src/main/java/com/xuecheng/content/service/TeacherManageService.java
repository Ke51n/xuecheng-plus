package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * package:  com.xuecheng.content.service
 * project_name:  microServiceProject
 * 2023/4/20  12:31
 * description: 教师管理服务
 *
 * @author wk
 * @version 1.0
 */
public interface TeacherManageService {


    /**
     * 根据课程id获取该课程的所有老师信息
     *
     * @param courseId
     * @return
     */
    List<CourseTeacher> getTeachersByCourseId(Integer courseId);

    /**
     * 修改老师信息，把请求体中的json转为po类
     * @param courseTeacher
     */
    void updateTeacherInfo(CourseTeacher courseTeacher);

    /**
     * 根据课程id和老师id删除老师信息
     * @param courseId
     * @param teacherId
     */
    void deleteTeacherInfo(int courseId, int teacherId);
}
