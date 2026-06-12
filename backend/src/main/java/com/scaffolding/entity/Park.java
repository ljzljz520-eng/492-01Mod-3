package com.scaffolding.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("park")
public class Park extends BaseEntity {

    private String parkName;

    private String parkAddress;

    private String parkContact;

    private String parkPhone;

    private String remark;
}
