package com.scaffolding.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bus_route")
public class BusRoute extends BaseEntity {

    private String routeName;

    private Long parkId;

    private String routeType;

    private String startPoint;

    private String endPoint;

    private LocalTime departureTime;

    private LocalTime arrivalTime;

    private String busNumber;

    private String driverName;

    private String driverPhone;

    private Integer capacity;

    private Integer occupiedCount;

    private String remark;
}
