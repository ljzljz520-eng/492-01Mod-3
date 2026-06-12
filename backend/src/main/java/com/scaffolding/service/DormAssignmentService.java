package com.scaffolding.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scaffolding.dto.UnconfirmedCheckinDTO;
import com.scaffolding.entity.DormAssignment;

import java.util.List;

public interface DormAssignmentService extends IService<DormAssignment> {

    DormAssignment createAssignment(Long workerId, Long roomId, Long buildingId, Long parkId, Integer bedNumber, String reason);

    boolean confirmCheckin(Long workerId);

    boolean cancelAssignment(Long workerId, String reason);

    List<UnconfirmedCheckinDTO> getUnconfirmedCheckinList();

    DormAssignment getCurrentAssignment(Long workerId);
}
