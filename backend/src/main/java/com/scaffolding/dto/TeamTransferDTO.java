package com.scaffolding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TeamTransferDTO {

    private Long teamId;

    private String teamName;

    private Long oldParkId;

    private String oldParkName;

    private Long newParkId;

    private String newParkName;

    private String transferReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime effectiveTime;

    private List<Long> workerIds;

    private Boolean sendArrivalReminder;
}
