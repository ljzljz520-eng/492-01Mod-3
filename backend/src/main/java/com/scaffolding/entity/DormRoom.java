package com.scaffolding.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dorm_room")
public class DormRoom extends BaseEntity {

    private String roomNumber;

    private Long buildingId;

    private Long parkId;

    private Integer floor;

    private String roomGender;

    private Integer bedCount;

    private Integer occupiedCount;

    private Integer isNightShiftRoom;

    private String remark;
}
