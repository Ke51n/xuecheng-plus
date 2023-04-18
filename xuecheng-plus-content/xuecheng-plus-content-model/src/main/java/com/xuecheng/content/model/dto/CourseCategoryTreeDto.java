package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * package:  com.xuecheng.content.model.dto
 * project_name:  microServiceProject
 * 2023/4/18  20:39
 * description:
 *
 * @author wk
 * @version 1.0
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    //子结点集合 属性
    List<CourseCategoryTreeDto> childrenTreeNodes;

}
