package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.TeacherManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * package:  com.xuecheng.content.service.impl
 * project_name:  microServiceProject
 * 2023/4/20  12:34
 * description:
 *
 * @author wk
 * @version 1.0
 */
@Service
@Slf4j
public class TeachManageServiceImpl implements TeacherManageService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    /**
     * 根据课程id获取该课程的所有老师信息
     *
     * @param courseId
     * @return
     */
    @Override
    public List<CourseTeacher> getTeachersByCourseId(Integer courseId) {
        return courseTeacherMapper.getTeachersByCourseId(courseId);
    }

    /**
     * 修改/新增老师信息，把请求体中的json转为po类
     *
     * @param courseTeacher
     */
    @Override
    @Transactional
    public void updateTeacherInfo(CourseTeacher courseTeacher) {
        if (courseTeacher.getId() != null) {
            //根据id修改老师信息
            UpdateWrapper<CourseTeacher> updateWrapper = new UpdateWrapper();
            updateWrapper.eq("id", courseTeacher.getId());
            courseTeacherMapper.update(courseTeacher, updateWrapper);
        } else {
            //新增一个老师信息
            courseTeacherMapper.insert(courseTeacher);
        }
    }

    /**
     * 根据课程id和老师id删除老师信息
     *
     * @param courseId
     * @param teacherId
     */
    @Override
    public void deleteTeacherInfo(int courseId, int teacherId) {
        QueryWrapper<CourseTeacher> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId).eq("id", teacherId);
        int row = courseTeacherMapper.delete(wrapper);
        if (row < 0) {
            throw new XueChengPlusException("删除教师信息出错，请重试");
        }
    }
}
