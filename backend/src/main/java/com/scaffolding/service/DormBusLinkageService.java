package com.scaffolding.service;

import com.scaffolding.dto.TeamTransferDTO;
import com.scaffolding.dto.UnconfirmedCheckinDTO;
import com.scaffolding.dto.WorkerAssignmentDTO;

import java.util.List;
import java.util.Map;

public interface DormBusLinkageService {

    Map<String, Object> handleEntryConfirmation(Long workerId);

    Map<String, Object> handleTeamTransfer(TeamTransferDTO transferDTO);

    List<UnconfirmedCheckinDTO> getUnconfirmedCheckinListWithDetail();

    WorkerAssignmentDTO getWorkerAssignmentDetail(Long workerId);

    boolean confirmWorkerCheckin(Long workerId);

    boolean sendArrivalReminder(Long workerId);
}
