package com.scaffolding.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("park_transfer_record")
public class ParkTransferRecord extends BaseEntity {

    private Long teamId;

    private Long workerId;

    private Long oldParkId;

    private Long newParkId;

    private Long oldRoomId;

    private Long newRoomId;

    private Long oldMorningRouteId;

    private Long newMorningRouteId;

    private Long oldEveningRouteId;

    private Long newEveningRouteId;

    private String transferStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime transferTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime effectiveTime;

    private String transferReason;

    private String arrivalReminderStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime reminderSentTime;

    private String remark;
}
