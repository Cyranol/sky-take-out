package com.sky.mapper;

import com.sky.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper {


    /**
     * 根据分类id查询关联了套餐的分类数量
     * @param id
     * @return
     */
    @Select("select count(0) from setmeal where category_id = #{id}")
    Integer countByCategoryId(Integer id);
}
