package com.scaffolding.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("team")
public class Team extends BaseEntity {

    private String teamName;

    private Long parkId;

    private String teamLeader;

    private String leaderPhone;

    private String remark;
}
