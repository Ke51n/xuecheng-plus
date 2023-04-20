package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    /**
     * 查询某课程的课程计划，组成树型结构
     *
     * @param courseId
     * @return
     */
    public List<TeachplanDto> selectTreeNodes(long courseId);

    /**
     * 在teachplan表中根据主键id找到其子记录数
     *
     * @param id
     * @return
     */
    int findChildNumById(Long id);

    /**
     * 在teachplan表中找到和当前id同级的所有数据
     *
     * @param
     * @return
     */
    List<Teachplan> findSameLevelPlans(@Param("courseId") long courseId, @Param("grade") int grade, @Param("parentid") long parentid);


    /**
     * 根据planid找到改条记录
     *
     * @param id
     * @return
     */
    Teachplan getPlanById(Long id);


}
