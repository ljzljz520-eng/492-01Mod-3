package com.scaffolding.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scaffolding.dto.TeamTransferDTO;
import com.scaffolding.dto.UnconfirmedCheckinDTO;
import com.scaffolding.dto.WorkerAssignmentDTO;
import com.scaffolding.entity.*;
import com.scaffolding.exception.BusinessException;
import com.scaffolding.mapper.*;
import com.scaffolding.service.*;
import com.scaffolding.strategy.BusMatchingStrategy;
import com.scaffolding.strategy.DormAssignmentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DormBusLinkageServiceImpl implements DormBusLinkageService {

    @Autowired
    private WorkerMapper workerMapper;

    @Autowired
    private DormAssignmentStrategy dormAssignmentStrategy;

    @Autowired
    private BusMatchingStrategy busMatchingStrategy;

    @Autowired
    private DormAssignmentService dormAssignmentService;

    @Autowired
    private BusAssignmentService busAssignmentService;

    @Autowired
    private DormAssignmentMapper dormAssignmentMapper;

    @Autowired
    private ParkTransferRecordMapper parkTransferRecordMapper;

    @Autowired
    private TeamMapper teamMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> handleEntryConfirmation(Long workerId) {
        Worker worker = workerMapper.selectById(workerId);
        if (worker == null) {
            throw new BusinessException("工人不存在");
        }
        if (worker.getIsOutOfTown() == null || worker.getIsOutOfTown() != 1) {
            throw new BusinessException("该工人不是外地工人，无需安排宿舍和班车");
        }
        if (!"CONFIRMED".equals(worker.getEntryStatus())) {
            throw new BusinessException("工人尚未确认入职，请先确认入职");
        }
        if (worker.getParkId() == null) {
            throw new BusinessException("工人未分配项目园区，请先分配园区");
        }

        Map<String, Object> result = new HashMap<>();

        DormRoom room = dormAssignmentStrategy.assignRoom(worker);
        Integer bedNumber = dormAssignmentStrategy.assignBedNumber(room.getId());
        DormAssignment dormAssignment = dormAssignmentService.createAssignment(
                workerId, room.getId(), room.getBuildingId(), room.getParkId(), bedNumber, "入职安排"
        );
        result.put("dormAssignment", dormAssignment);

        BusRoute morningRoute = busMatchingStrategy.matchMorningRoute(worker);
        BusRoute eveningRoute = busMatchingStrategy.matchEveningRoute(worker);
        BusAssignment busAssignment = busAssignmentService.createAssignment(
                workerId, morningRoute.getId(), eveningRoute.getId(), worker.getParkId(), "入职安排"
        );
        result.put("busAssignment", busAssignment);

        result.put("room", room);
        result.put("bedNumber", bedNumber);
        result.put("morningRoute", morningRoute);
        result.put("eveningRoute", eveningRoute);

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> handleTeamTransfer(TeamTransferDTO transferDTO) {
        if (transferDTO.getTeamId() == null) {
            throw new BusinessException("班组ID不能为空");
        }
        if (transferDTO.getNewParkId() == null) {
            throw new BusinessException("新园区ID不能为空");
        }

        Team team = teamMapper.selectById(transferDTO.getTeamId());
        if (team == null) {
            throw new BusinessException("班组不存在");
        }

        List<Long> workerIds = transferDTO.getWorkerIds();
        if (workerIds == null || workerIds.isEmpty()) {
            workerIds = workerMapper.getOutOfTownWorkerIdsByTeam(transferDTO.getTeamId());
        }

        if (workerIds.isEmpty()) {
            throw new BusinessException("该班组没有需要安排的外地工人");
        }

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> workerResults = new ArrayList<>();
        List<ParkTransferRecord> transferRecords = new ArrayList<>();

        for (Long workerId : workerIds) {
            try {
                Map<String, Object> workerResult = transferSingleWorker(workerId, transferDTO);
                workerResults.add(workerResult);

                ParkTransferRecord record = (ParkTransferRecord) workerResult.get("transferRecord");
                if (record != null) {
                    transferRecords.add(record);
                }
            } catch (Exception e) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("workerId", workerId);
                errorResult.put("success", false);
                errorResult.put("error", e.getMessage());
                workerResults.add(errorResult);
            }
        }

        team.setParkId(transferDTO.getNewParkId());
        teamMapper.updateById(team);

        result.put("successCount", transferRecords.size());
        result.put("totalCount", workerIds.size());
        result.put("workerResults", workerResults);
        result.put("transferRecords", transferRecords);

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    protected Map<String, Object> transferSingleWorker(Long workerId, TeamTransferDTO transferDTO) {
        Worker worker = workerMapper.selectById(workerId);
        if (worker == null) {
            throw new BusinessException("工人不存在");
        }
        if (worker.getIsOutOfTown() == null || worker.getIsOutOfTown() != 1) {
            throw new BusinessException("该工人不是外地工人，无需调园安排");
        }

        Map<String, Object> result = new HashMap<>();

        DormAssignment oldDormAssignment = dormAssignmentService.getCurrentAssignment(workerId);
        BusAssignment oldBusAssignment = busAssignmentService.getCurrentAssignment(workerId);

        ParkTransferRecord transferRecord = new ParkTransferRecord();
        transferRecord.setTeamId(transferDTO.getTeamId());
        transferRecord.setWorkerId(workerId);
        transferRecord.setOldParkId(worker.getParkId());
        transferRecord.setNewParkId(transferDTO.getNewParkId());
        transferRecord.setTransferTime(LocalDateTime.now());
        transferRecord.setEffectiveTime(transferDTO.getEffectiveTime() != null ? transferDTO.getEffectiveTime() : LocalDateTime.now());
        transferRecord.setTransferReason(transferDTO.getTransferReason());
        transferRecord.setTransferStatus("PROCESSING");

        if (oldDormAssignment != null) {
            transferRecord.setOldRoomId(oldDormAssignment.getRoomId());
            dormAssignmentService.cancelAssignment(workerId, "调园-" + transferDTO.getTransferReason());
        }

        if (oldBusAssignment != null) {
            transferRecord.setOldMorningRouteId(oldBusAssignment.getMorningRouteId());
            transferRecord.setOldEveningRouteId(oldBusAssignment.getEveningRouteId());
            busAssignmentService.cancelAssignment(workerId, "调园-" + transferDTO.getTransferReason());
        }

        worker.setParkId(transferDTO.getNewParkId());
        workerMapper.updateById(worker);

        DormRoom newRoom = dormAssignmentStrategy.assignRoom(worker);
        Integer bedNumber = dormAssignmentStrategy.assignBedNumber(newRoom.getId());
        DormAssignment newDormAssignment = dormAssignmentService.createAssignment(
                workerId, newRoom.getId(), newRoom.getBuildingId(), newRoom.getParkId(), bedNumber,
                "调园-" + transferDTO.getTransferReason()
        );
        transferRecord.setNewRoomId(newRoom.getId());

        BusRoute newMorningRoute = busMatchingStrategy.matchMorningRoute(worker);
        BusRoute newEveningRoute = busMatchingStrategy.matchEveningRoute(worker);
        BusAssignment newBusAssignment = busAssignmentService.createAssignment(
                workerId, newMorningRoute.getId(), newEveningRoute.getId(), transferDTO.getNewParkId(),
                "调园-" + transferDTO.getTransferReason()
        );
        transferRecord.setNewMorningRouteId(newMorningRoute.getId());
        transferRecord.setNewEveningRouteId(newEveningRoute.getId());

        if (Boolean.TRUE.equals(transferDTO.getSendArrivalReminder())) {
            transferRecord.setArrivalReminderStatus("PENDING");
        } else {
            transferRecord.setArrivalReminderStatus("NOT_REQUIRED");
        }

        transferRecord.setTransferStatus("COMPLETED");
        parkTransferRecordMapper.insert(transferRecord);

        if (Boolean.TRUE.equals(transferDTO.getSendArrivalReminder())) {
            sendArrivalReminder(workerId);
        }

        result.put("success", true);
        result.put("workerId", workerId);
        result.put("workerName", worker.getWorkerName());
        result.put("newRoom", newRoom);
        result.put("bedNumber", bedNumber);
        result.put("newMorningRoute", newMorningRoute);
        result.put("newEveningRoute", newEveningRoute);
        result.put("transferRecord", transferRecord);

        return result;
    }

    @Override
    public List<UnconfirmedCheckinDTO> getUnconfirmedCheckinListWithDetail() {
        return dormAssignmentService.getUnconfirmedCheckinList();
    }

    @Override
    public WorkerAssignmentDTO getWorkerAssignmentDetail(Long workerId) {
        WorkerAssignmentDTO dto = workerMapper.getWorkerAssignmentDetail(workerId);
        if (dto != null) {
            LambdaQueryWrapper<ParkTransferRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ParkTransferRecord::getWorkerId, workerId)
                    .orderByDesc(ParkTransferRecord::getTransferTime)
                    .last("LIMIT 1");
            ParkTransferRecord latestTransfer = parkTransferRecordMapper.selectOne(wrapper);
            if (latestTransfer != null) {
                dto.setArrivalReminderStatus(latestTransfer.getArrivalReminderStatus());
            }
        }
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmWorkerCheckin(Long workerId) {
        return dormAssignmentService.confirmCheckin(workerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendArrivalReminder(Long workerId) {
        LambdaQueryWrapper<ParkTransferRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ParkTransferRecord::getWorkerId, workerId)
                .eq(ParkTransferRecord::getArrivalReminderStatus, "PENDING")
                .orderByDesc(ParkTransferRecord::getTransferTime)
                .last("LIMIT 1");
        ParkTransferRecord record = parkTransferRecordMapper.selectOne(wrapper);

        if (record == null) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ParkTransferRecord::getWorkerId, workerId)
                    .orderByDesc(ParkTransferRecord::getTransferTime)
                    .last("LIMIT 1");
            record = parkTransferRecordMapper.selectOne(wrapper);
        }

        if (record == null) {
            throw new BusinessException("该工人没有调园记录，无需发送到岗提醒");
        }

        record.setArrivalReminderStatus("SENT");
        record.setReminderSentTime(LocalDateTime.now());
        parkTransferRecordMapper.updateById(record);

        return true;
    }
}
