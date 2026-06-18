package com.scaffolding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UnconfirmedCheckinDTO {

    private Long workerId;

    private String workerName;

    private String workerGender;

    private String workerPhone;

    private String idCard;

    private String hometown;

    private String teamName;

    private String parkName;

    private String parkAddress;

    private String buildingName;

    private String roomNumber;

    private Integer bedNumber;

    private Integer nightShiftCount;

    private String entryStatus;

    private String entryStatusText;

    private String checkinStatus;

    private String checkinStatusText;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime assignTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime entryConfirmTime;

    private Long daysSinceAssign;

    private String assignmentReason;

    private String remark;
}
