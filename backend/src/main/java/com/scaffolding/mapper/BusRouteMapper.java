package com.scaffolding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scaffolding.entity.BusRoute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BusRouteMapper extends BaseMapper<BusRoute> {

    @Select("SELECT * FROM bus_route " +
            "WHERE park_id = #{parkId} " +
            "AND route_type = #{routeType} " +
            "AND occupied_count < capacity " +
            "AND deleted = 0 " +
            "ORDER BY departure_time ASC " +
            "LIMIT 1")
    BusRoute findAvailableRoute(@Param("parkId") Long parkId,
                                @Param("routeType") String routeType);

    @Update("UPDATE bus_route SET occupied_count = occupied_count + 1 " +
            "WHERE id = #{routeId} AND occupied_count < capacity AND deleted = 0")
    int incrementOccupiedCount(@Param("routeId") Long routeId);

    @Update("UPDATE bus_route SET occupied_count = occupied_count - 1 " +
            "WHERE id = #{routeId} AND occupied_count > 0 AND deleted = 0")
    int decrementOccupiedCount(@Param("routeId") Long routeId);
}
