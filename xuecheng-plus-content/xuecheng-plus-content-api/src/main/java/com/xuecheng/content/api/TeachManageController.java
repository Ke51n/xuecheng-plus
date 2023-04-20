package com.xuecheng.content.api;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeacherManageService;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * package:  com.xuecheng.content.api
 * project_name:  microServiceProject
 * 2023/4/19  15:31
 * description:  课程计划编辑接口
 *
 * @author wk
 * @version 1.0
 */
@Api(value = "教师管理接口", tags = "教师管理接口")
@RestController
public class TeachManageController {

    @Autowired
    TeacherManageService teacherManageService;

    /**
     * 根据课程id获取该课程的所有老师信息
     *
     * @param courseId
     * @return
     */
    @GetMapping("/courseTeacher/list/{courseId}")
    List<CourseTeacher> getTeachersByCourseId(@PathVariable int courseId) {
        return teacherManageService.getTeachersByCourseId(courseId);
    }


    /**
     * 修改/新增老师信息，把请求体中的json转为po类
     *
     * @param courseTeacher
     */
    @PostMapping("/courseTeacher")
    void updateTeacherInfo(@RequestBody CourseTeacher courseTeacher) {
        teacherManageService.updateTeacherInfo(courseTeacher);
    }

    /**
     * 根据课程id和老师id删除老师信息
     *
     * @param courseId
     * @param teacherId
     */
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    void deleteTeacher(@PathVariable int courseId, @PathVariable int teacherId) {
        teacherManageService.deleteTeacherInfo(courseId, teacherId);
    }

}
