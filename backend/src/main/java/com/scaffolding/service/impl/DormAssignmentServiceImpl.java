package com.scaffolding.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scaffolding.dto.UnconfirmedCheckinDTO;
import com.scaffolding.entity.DormAssignment;
import com.scaffolding.entity.Worker;
import com.scaffolding.exception.BusinessException;
import com.scaffolding.mapper.DormAssignmentMapper;
import com.scaffolding.mapper.WorkerMapper;
import com.scaffolding.service.DormAssignmentService;
import com.scaffolding.strategy.DormAssignmentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DormAssignmentServiceImpl extends ServiceImpl<DormAssignmentMapper, DormAssignment> implements DormAssignmentService {

    @Autowired
    private DormAssignmentMapper dormAssignmentMapper;

    @Autowired
    private WorkerMapper workerMapper;

    @Autowired
    private DormAssignmentStrategy dormAssignmentStrategy;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DormAssignment createAssignment(Long workerId, Long roomId, Long buildingId, Long parkId, Integer bedNumber, String reason) {
        DormAssignment existing = getCurrentAssignment(workerId);
        if (existing != null) {
            cancelAssignment(workerId, "重新分配");
        }

        DormAssignment assignment = new DormAssignment();
        assignment.setWorkerId(workerId);
        assignment.setRoomId(roomId);
        assignment.setBuildingId(buildingId);
        assignment.setParkId(parkId);
        assignment.setBedNumber(bedNumber);
        assignment.setAssignmentStatus("ASSIGNED");
        assignment.setAssignTime(LocalDateTime.now());
        assignment.setAssignmentReason(reason);

        this.save(assignment);

        Worker worker = workerMapper.selectById(workerId);
        if (worker != null) {
            worker.setCheckinStatus("PENDING");
            workerMapper.updateById(worker);
        }

        return assignment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmCheckin(Long workerId) {
        DormAssignment assignment = getCurrentAssignment(workerId);
        if (assignment == null) {
            throw new BusinessException("该工人暂无入住安排");
        }
        if ("CHECKED_IN".equals(assignment.getAssignmentStatus())) {
            throw new BusinessException("该工人已确认入住");
        }

        assignment.setAssignmentStatus("CHECKED_IN");
        assignment.setConfirmTime(LocalDateTime.now());
        assignment.setCheckinTime(LocalDateTime.now());
        this.updateById(assignment);

        Worker worker = workerMapper.selectById(workerId);
        if (worker != null) {
            worker.setCheckinStatus("CONFIRMED");
            worker.setCheckinConfirmTime(LocalDateTime.now());
            workerMapper.updateById(worker);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelAssignment(Long workerId, String reason) {
        DormAssignment assignment = getCurrentAssignment(workerId);
        if (assignment == null) {
            return false;
        }

        assignment.setAssignmentStatus("CANCELLED");
        assignment.setCheckoutTime(LocalDateTime.now());
        assignment.setRemark(reason);
        this.updateById(assignment);

        dormAssignmentStrategy.releaseRoom(assignment.getRoomId());

        Worker worker = workerMapper.selectById(workerId);
        if (worker != null) {
            worker.setCheckinStatus("UNASSIGNED");
            workerMapper.updateById(worker);
        }

        return true;
    }

    @Override
    public List<UnconfirmedCheckinDTO> getUnconfirmedCheckinList() {
        List<UnconfirmedCheckinDTO> list = dormAssignmentMapper.selectUnconfirmedCheckinList();
        LocalDateTime now = LocalDateTime.now();
        for (UnconfirmedCheckinDTO dto : list) {
            if (dto.getAssignTime() != null) {
                dto.setDaysSinceAssign(Duration.between(dto.getAssignTime(), now).toDays());
            }
            dto.setEntryStatusText(getEntryStatusText(dto.getEntryStatus()));
            dto.setCheckinStatusText(getCheckinStatusText(dto.getCheckinStatus()));
        }
        return list;
    }

    private String getEntryStatusText(String status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case "PENDING":
                return "待确认入职";
            case "CONFIRMED":
                return "已确认入职";
            case "REJECTED":
                return "已拒绝入职";
            default:
                return status;
        }
    }

    private String getCheckinStatusText(String status) {
        if (status == null) {
            return "未安排";
        }
        switch (status) {
            case "UNASSIGNED":
                return "未安排";
            case "PENDING":
                return "待确认入住";
            case "CONFIRMED":
                return "已确认入住";
            case "CANCELLED":
                return "已取消";
            default:
                return status;
        }
    }

    @Override
    public DormAssignment getCurrentAssignment(Long workerId) {
        LambdaQueryWrapper<DormAssignment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DormAssignment::getWorkerId, workerId)
                .in(DormAssignment::getAssignmentStatus, "ASSIGNED", "CONFIRMED", "CHECKED_IN")
                .orderByDesc(DormAssignment::getAssignTime)
                .last("LIMIT 1");
        return this.getOne(wrapper);
    }
}
