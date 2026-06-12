package com.scaffolding.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("worker")
public class Worker extends BaseEntity {

    private String workerName;

    private String workerGender;

    private String workerPhone;

    private String idCard;

    private String hometown;

    private Integer isOutOfTown;

    private String entryStatus;

    private Long teamId;

    private Long parkId;

    private Integer nightShiftCount;

    private String checkinStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime checkinConfirmTime;

    private String remark;
}
