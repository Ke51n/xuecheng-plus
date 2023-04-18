package com.xuecheng.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * package:  com.xuecheng.content
 * project_name:  xuecheng-plus
 * 2023/4/18  10:27
 * description: 测试类
 *
 * @author wk
 * @version 1.0
 */
@SpringBootTest
class CourseCategoryMapperTests {

    @Autowired
    CourseCategoryMapper courseCategoryMapper;


    @Test
    void testCourseBaseMapper() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);
        System.out.println("###################################");
    }

}