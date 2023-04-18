package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * package:  com.xuecheng.content.api
 * project_name:  xuecheng-plus
 * 2023/4/17  22:03
 * description:
 *
 * @author wk
 * @version 1.0
 */
@Api(value = "课程信息管理接口", tags = "课程信息管理接口")
@RestController
public class CourseBaseInfoController {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto) {
        // RequestBody 前端请求的json字符串转为java对象
        // required = false,该json参数非必要，当用户不传参时，默认查询所有

        //真实请求，测试接口实现类
        PageResult<CourseBase> pageResult = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);

        //模拟数据
       /* CourseBase courseBase = new CourseBase();
        CourseBase courseBase1 = new CourseBase();
        courseBase.setName("测试名称11");
        courseBase1.setName("测试名称22");
        courseBase.setCreateDate(LocalDateTime.now());
        courseBase1.setCreateDate(LocalDateTime.of(2023, 4, 17, 12, 12, 12));
        List<CourseBase> courseBases = new ArrayList();
        courseBases.add(courseBase);
        courseBases.add(courseBase1);
        PageResult pageResult = new PageResult<CourseBase>(courseBases, 10, 1, 10);
        */
        return pageResult;
//        return null;
    }

    @ApiOperation("测试1")
    @GetMapping("/test")
    public String test() {
        return "hello";
    }


}
