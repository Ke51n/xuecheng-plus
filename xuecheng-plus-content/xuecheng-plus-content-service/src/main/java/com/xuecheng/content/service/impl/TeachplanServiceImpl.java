package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * package:  com.xuecheng.content.service.impl
 * project_name:  microServiceProject
 * 2023/4/19  15:39
 * description:
 *
 * @author wk
 * @version 1.0
 */
@Slf4j
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    /**
     * 删除指定章
     *
     * @param id
     * @return
     */
    @Override
    public int deleteTeachplan(String id) {
        return teachplanMapper.deleteById(id);
    }

    /**
     * 根据id查找课程计划
     *
     * @param courseId
     * @return
     */
    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        List<TeachplanDto> teachplanDtos = teachplanMapper.selectTreeNodes(courseId);
        return teachplanDtos;
    }

    /**
     * 课程计划 新增/修改/保存
     *
     * @param teachplanDto
     */
    @Override
    @Transactional
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        //课程计划id
        Long id = teachplanDto.getId();
        //修改课程计划
        if (id != null) {
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        } else {
            //取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();

            BeanUtils.copyProperties(teachplanDto, teachplanNew);
            //设置排序号
            teachplanNew.setOrderby(count + 1);
            teachplanMapper.insert(teachplanNew);

        }
    }

    /**
     * 获取最新的排序号,新插入计划时默认排到最后
     * 也就是查询满足条件的记录数
     *
     * @param courseId
     * @param parentId
     * @return
     */
    private int getTeachplanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
}
