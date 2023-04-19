package com.xuecheng.content.api;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
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
    public void deleteTeachplan(@PathVariable Long id) {
        //这里删除时，若是章需要保证下面的节为空
        //若是节，同时要将其它关联的视频信息也删除
        //1.找到该结点为根的课程计划信息个数
        int childNum = teachplanService.findChildNumById(id);
        if (childNum > 0) {
            throw new XueChengPlusException("课程计划信息还有子级信息，无法操作");
        }
        teachplanService.deleteTeachplan(id);
    }

    /**
     * 上移章/节
     *
     * @param id 章/节计划id
     */
    @Transactional
    @ApiOperation("上移章/节")
    @PostMapping("/teachplan/moveup/{id}")
    public void moveupplan(@PathVariable Long id) {
        //思路：首先找到和当前id同级的所有数据,满足对应同一个课程，同一级，父计划id相同
        //1.找到当前plan
        Teachplan teachplan = teachplanService.getPlanById(id);
        Long courseId = teachplan.getCourseId();
        Integer grade = teachplan.getGrade();
        Long parentid = teachplan.getParentid();
        //2.找到和当前id同级的所有数据
        List<Teachplan> sameLevelRecords = teachplanService.findSameLevelPlans(courseId,grade,parentid);
        //3.若当前plan不在第一个且同级数据多于一个，则和上一个交换orderid，并update到teachplan表
        int preIdx = -1;
        for (int i = 0; i < sameLevelRecords.size(); i++) {
            if (sameLevelRecords.get(i).getId().equals(id)) {
                //当前记录的位置
                preIdx = i - 1;
                break;
            }
        }
        if (preIdx == -1) {
            //当前记录在最上边，无法上移
            return;
        }
        Integer preOrderby = sameLevelRecords.get(preIdx).getOrderby();
        //把上一条记录的orderby字段设为当前的orderby
        sameLevelRecords.get(preIdx).setOrderby(sameLevelRecords.get(preIdx + 1).getOrderby());
        //update上一条记录
        teachplanService.saveTeachplan(sameLevelRecords.get(preIdx));
        //修改当前记录的orderby并保存
        sameLevelRecords.get(preIdx + 1).setOrderby(preOrderby);
        teachplanService.saveTeachplan(sameLevelRecords.get(preIdx + 1));
    }
}
