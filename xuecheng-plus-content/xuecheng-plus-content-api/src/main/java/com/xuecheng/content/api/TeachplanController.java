package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Arg;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(value = "课程计划编辑接口", tags = "课程计划编辑接口")
@RestController
public class TeachplanController {
    @Autowired
    TeachplanService teachplanService;

    /**
     * 查询课程计划树形结构
     *
     * @param courseId
     * @return
     */
    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId", name = "课程Id", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTreeNodes(@PathVariable Long courseId) {
        //ApiImplicitParam 注解，指定一个请求参数的各个方面
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        return teachplanTree;
    }


    /**
     * 课程计划创建或修改
     *
     * @param saveTeachplanDto
     */
    @ApiOperation("课程计划创建或修改")
    @PostMapping("/teachplan")
    public void saveTeachplan(@RequestBody SaveTeachplanDto saveTeachplanDto) {
        teachplanService.saveTeachplan(saveTeachplanDto);
    }


    /**
     * 根据id删除章
     *
     * @param id
     */
    @ApiOperation("课程计划创建或修改")
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable String id) {
        teachplanService.deleteTeachplan(id);
    }

}
