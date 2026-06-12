package com.scaffolding.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scaffolding.entity.BusAssignment;

public interface BusAssignmentService extends IService<BusAssignment> {

    BusAssignment createAssignment(Long workerId, Long morningRouteId, Long eveningRouteId, Long parkId, String reason);

    boolean cancelAssignment(Long workerId, String reason);

    BusAssignment getCurrentAssignment(Long workerId);
}
