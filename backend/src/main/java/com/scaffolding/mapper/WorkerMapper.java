package com.scaffolding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scaffolding.dto.WorkerAssignmentDTO;
import com.scaffolding.entity.Worker;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WorkerMapper extends BaseMapper<Worker> {

    @Select("SELECT " +
            "w.id AS workerId, " +
            "w.worker_name AS workerName, " +
            "w.worker_gender AS workerGender, " +
            "w.worker_phone AS workerPhone, " +
            "w.team_id AS teamId, " +
            "t.team_name AS teamName, " +
            "w.park_id AS parkId, " +
            "p.park_name AS parkName, " +
            "da.room_id AS roomId, " +
            "db.building_name AS buildingName, " +
            "dr.room_number AS roomNumber, " +
            "da.bed_number AS bedNumber, " +
            "ba.morning_route_id AS morningRouteId, " +
            "br1.route_name AS morningRouteName, " +
            "br1.departure_time AS morningDepartureTime, " +
            "ba.evening_route_id AS eveningRouteId, " +
            "br2.route_name AS eveningRouteName, " +
            "br2.departure_time AS eveningDepartureTime, " +
            "w.night_shift_count AS nightShiftCount, " +
            "w.checkin_status AS checkinStatus, " +
            "w.checkin_confirm_time AS checkinConfirmTime " +
            "FROM worker w " +
            "LEFT JOIN team t ON w.team_id = t.id " +
            "LEFT JOIN park p ON w.park_id = p.id " +
            "LEFT JOIN dorm_assignment da ON w.id = da.worker_id AND da.assignment_status IN ('ASSIGNED', 'CONFIRMED', 'CHECKED_IN') " +
            "LEFT JOIN dorm_building db ON da.building_id = db.id " +
            "LEFT JOIN dorm_room dr ON da.room_id = dr.id " +
            "LEFT JOIN bus_assignment ba ON w.id = ba.worker_id AND ba.assignment_status = 'ACTIVE' " +
            "LEFT JOIN bus_route br1 ON ba.morning_route_id = br1.id " +
            "LEFT JOIN bus_route br2 ON ba.evening_route_id = br2.id " +
            "WHERE w.id = #{workerId} AND w.deleted = 0")
    WorkerAssignmentDTO getWorkerAssignmentDetail(@Param("workerId") Long workerId);

    @Select("SELECT w.id FROM worker w " +
            "WHERE w.team_id = #{teamId} " +
            "AND w.is_out_of_town = 1 " +
            "AND w.entry_status = 'CONFIRMED' " +
            "AND w.deleted = 0")
    List<Long> getOutOfTownWorkerIdsByTeam(@Param("teamId") Long teamId);
}
