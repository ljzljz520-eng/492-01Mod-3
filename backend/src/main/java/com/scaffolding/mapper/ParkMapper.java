package com.scaffolding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scaffolding.entity.Park;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParkMapper extends BaseMapper<Park> {
}
