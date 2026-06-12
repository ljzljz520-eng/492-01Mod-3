package com.scaffolding.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dorm_building")
public class DormBuilding extends BaseEntity {

    private String buildingName;

    private Long parkId;

    private String buildingGender;

    private Integer totalFloors;

    private String managerName;

    private String managerPhone;

    private String remark;
}
