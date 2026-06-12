package com.scaffolding.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scaffolding.entity.BusAssignment;
import com.scaffolding.exception.BusinessException;
import com.scaffolding.mapper.BusAssignmentMapper;
import com.scaffolding.service.BusAssignmentService;
import com.scaffolding.strategy.BusMatchingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BusAssignmentServiceImpl extends ServiceImpl<BusAssignmentMapper, BusAssignment> implements BusAssignmentService {

    @Autowired
    private BusAssignmentMapper busAssignmentMapper;

    @Autowired
    private BusMatchingStrategy busMatchingStrategy;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusAssignment createAssignment(Long workerId, Long morningRouteId, Long eveningRouteId, Long parkId, String reason) {
        BusAssignment existing = getCurrentAssignment(workerId);
        if (existing != null) {
            cancelAssignment(workerId, "重新分配");
        }

        BusAssignment assignment = new BusAssignment();
        assignment.setWorkerId(workerId);
        assignment.setMorningRouteId(morningRouteId);
        assignment.setEveningRouteId(eveningRouteId);
        assignment.setParkId(parkId);
        assignment.setAssignmentStatus("ACTIVE");
        assignment.setAssignTime(LocalDateTime.now());
        assignment.setEffectiveDate(LocalDateTime.now());
        assignment.setAssignmentReason(reason);

        this.save(assignment);
        return assignment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelAssignment(Long workerId, String reason) {
        BusAssignment assignment = getCurrentAssignment(workerId);
        if (assignment == null) {
            return false;
        }

        assignment.setAssignmentStatus("INACTIVE");
        assignment.setExpiryDate(LocalDateTime.now());
        assignment.setRemark(reason);
        this.updateById(assignment);

        busMatchingStrategy.releaseRoute(assignment.getMorningRouteId());
        busMatchingStrategy.releaseRoute(assignment.getEveningRouteId());

        return true;
    }

    @Override
    public BusAssignment getCurrentAssignment(Long workerId) {
        LambdaQueryWrapper<BusAssignment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusAssignment::getWorkerId, workerId)
                .eq(BusAssignment::getAssignmentStatus, "ACTIVE")
                .orderByDesc(BusAssignment::getAssignTime)
                .last("LIMIT 1");
        return this.getOne(wrapper);
    }
}
