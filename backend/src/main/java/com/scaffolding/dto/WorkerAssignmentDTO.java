package com.scaffolding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkerAssignmentDTO {

    private Long workerId;

    private String workerName;

    private String workerGender;

    private String workerPhone;

    private Long teamId;

    private String teamName;

    private Long parkId;

    private String parkName;

    private Long roomId;

    private String buildingName;

    private String roomNumber;

    private Integer bedNumber;

    private Long morningRouteId;

    private String morningRouteName;

    private String morningDepartureTime;

    private Long eveningRouteId;

    private String eveningRouteName;

    private String eveningDepartureTime;

    private Integer nightShiftCount;

    private String checkinStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime checkinConfirmTime;

    private String arrivalReminderStatus;
}
