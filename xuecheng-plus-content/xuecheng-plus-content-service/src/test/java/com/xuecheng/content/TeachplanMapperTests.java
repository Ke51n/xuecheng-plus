package com.xuecheng.content;

import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * package:  com.xuecheng.content
 * project_name:  xuecheng-plus
 * 2023/4/18  10:27
 * description: TeachplanMapperTests测试类
 *
 * @author wk
 * @version 1.0
 */
@SpringBootTest
class TeachplanMapperTests {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Test
    void testTeachPlanMapper() {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(117);
        System.out.println(teachplanDtos);
        System.out.println("===========");
    }

}