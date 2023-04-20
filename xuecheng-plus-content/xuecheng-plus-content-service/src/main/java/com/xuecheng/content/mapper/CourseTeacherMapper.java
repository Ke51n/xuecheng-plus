package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface CourseTeacherMapper extends BaseMapper<CourseTeacher> {

    /**
     * 根据课程id获取该课程的所有老师信息
     *
     * @param courseId
     * @return
     */
    List<CourseTeacher> getTeachersByCourseId(int courseId);

    /**
     * 修改老师信息，把请求体中的json转为po类
     * @param courseTeacher
     */
    void updateTeacherInfo(CourseTeacher courseTeacher);
}
