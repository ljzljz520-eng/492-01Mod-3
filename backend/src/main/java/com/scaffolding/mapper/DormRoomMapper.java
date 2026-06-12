package com.scaffolding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scaffolding.entity.DormRoom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DormRoomMapper extends BaseMapper<DormRoom> {

    @Select("SELECT r.* FROM dorm_room r " +
            "LEFT JOIN dorm_building b ON r.building_id = b.id " +
            "WHERE r.park_id = #{parkId} " +
            "AND r.room_gender = #{gender} " +
            "AND r.is_night_shift_room = #{isNightShiftRoom} " +
            "AND r.occupied_count < r.bed_count " +
            "AND r.deleted = 0 " +
            "AND b.deleted = 0 " +
            "ORDER BY r.floor ASC, r.room_number ASC " +
            "LIMIT 1")
    DormRoom findAvailableRoom(@Param("parkId") Long parkId,
                               @Param("gender") String gender,
                               @Param("isNightShiftRoom") Integer isNightShiftRoom);

    @Select("SELECT r.* FROM dorm_room r " +
            "LEFT JOIN dorm_building b ON r.building_id = b.id " +
            "WHERE r.park_id = #{parkId} " +
            "AND r.room_gender = #{gender} " +
            "AND r.occupied_count < r.bed_count " +
            "AND r.deleted = 0 " +
            "AND b.deleted = 0 " +
            "ORDER BY r.is_night_shift_room DESC, r.floor ASC, r.room_number ASC " +
            "LIMIT 1")
    DormRoom findAvailableRoomFlexible(@Param("parkId") Long parkId,
                                       @Param("gender") String gender);

    @Select("SELECT COALESCE(MAX(da.bed_number), 0) + 1 FROM dorm_assignment da " +
            "WHERE da.room_id = #{roomId} " +
            "AND da.assignment_status IN ('ASSIGNED', 'CONFIRMED', 'CHECKED_IN') " +
            "AND da.deleted = 0")
    Integer getNextAvailableBedNumber(@Param("roomId") Long roomId);

    @Update("UPDATE dorm_room SET occupied_count = occupied_count + 1 " +
            "WHERE id = #{roomId} AND occupied_count < bed_count AND deleted = 0")
    int incrementOccupiedCount(@Param("roomId") Long roomId);

    @Update("UPDATE dorm_room SET occupied_count = occupied_count - 1 " +
            "WHERE id = #{roomId} AND occupied_count > 0 AND deleted = 0")
    int decrementOccupiedCount(@Param("roomId") Long roomId);

    @Select("SELECT COUNT(*) FROM dorm_assignment da " +
            "WHERE da.room_id = #{roomId} " +
            "AND da.bed_number = #{bedNumber} " +
            "AND da.assignment_status IN ('ASSIGNED', 'CONFIRMED', 'CHECKED_IN') " +
            "AND da.deleted = 0")
    int countBedOccupancy(@Param("roomId") Long roomId, @Param("bedNumber") Integer bedNumber);
}
