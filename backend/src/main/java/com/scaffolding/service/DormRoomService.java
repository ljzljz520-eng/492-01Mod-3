package com.scaffolding.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scaffolding.entity.DormRoom;

public interface DormRoomService extends IService<DormRoom> {

    DormRoom findAvailableRoom(Long parkId, String gender, Integer isNightShiftRoom);

    DormRoom findAvailableRoomFlexible(Long parkId, String gender);
}
