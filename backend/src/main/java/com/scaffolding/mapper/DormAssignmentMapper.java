package com.scaffolding.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scaffolding.dto.UnconfirmedCheckinDTO;
import com.scaffolding.entity.DormAssignment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DormAssignmentMapper extends BaseMapper<DormAssignment> {

    @Select("SELECT " +
            "w.id AS workerId, " +
            "w.worker_name AS workerName, " +
            "w.worker_gender AS workerGender, " +
            "w.worker_phone AS workerPhone, " +
            "w.id_card AS idCard, " +
            "w.hometown AS hometown, " +
            "t.team_name AS teamName, " +
            "p.park_name AS parkName, " +
            "p.park_address AS parkAddress, " +
            "b.building_name AS buildingName, " +
            "r.room_number AS roomNumber, " +
            "da.bed_number AS bedNumber, " +
            "w.night_shift_count AS nightShiftCount, " +
            "w.entry_status AS entryStatus, " +
            "w.checkin_status AS checkinStatus, " +
            "da.assign_time AS assignTime, " +
            "da.assignment_reason AS assignmentReason, " +
            "da.remark AS remark " +
            "FROM dorm_assignment da " +
            "LEFT JOIN worker w ON da.worker_id = w.id " +
            "LEFT JOIN team t ON w.team_id = t.id " +
            "LEFT JOIN park p ON da.park_id = p.id " +
            "LEFT JOIN dorm_building b ON da.building_id = b.id " +
            "LEFT JOIN dorm_room r ON da.room_id = r.id " +
            "WHERE da.assignment_status = 'ASSIGNED' " +
            "AND w.entry_status = 'CONFIRMED' " +
            "AND w.checkin_status != 'CONFIRMED' " +
            "AND w.deleted = 0 " +
            "AND da.deleted = 0 " +
            "ORDER BY da.assign_time ASC")
    List<UnconfirmedCheckinDTO> selectUnconfirmedCheckinList();
}
