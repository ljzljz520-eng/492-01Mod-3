package com.scaffolding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DormAssignmentDetailDTO {

    private Long id;

    private Long workerId;

    private String workerName;

    private String workerGender;

    private String workerPhone;

    private Long roomId;

    private String roomNumber;

    private Long buildingId;

    private String buildingName;

    private Long parkId;

    private String parkName;

    private Integer floor;

    private Integer bedNumber;

    private String roomGender;

    private Integer isNightShiftRoom;

    private String assignmentStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime assignTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime confirmTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime checkinTime;

    private String assignmentReason;

    private String remark;
}
