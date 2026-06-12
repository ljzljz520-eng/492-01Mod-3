package com.scaffolding.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bus_assignment")
public class BusAssignment extends BaseEntity {

    private Long workerId;

    private Long morningRouteId;

    private Long eveningRouteId;

    private Long parkId;

    private String assignmentStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime assignTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime effectiveDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expiryDate;

    private String assignmentReason;

    private String remark;
}
