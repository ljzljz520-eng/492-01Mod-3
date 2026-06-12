package com.scaffolding.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bus_schedule")
public class BusSchedule extends BaseEntity {

    private Long routeId;

    private Long parkId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate scheduleDate;

    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime actualDepartureTime;

    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime actualArrivalTime;

    private String status;

    private String remark;
}
