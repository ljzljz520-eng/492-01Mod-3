package com.scaffolding.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scaffolding.entity.DormRoom;
import com.scaffolding.mapper.DormRoomMapper;
import com.scaffolding.service.DormRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DormRoomServiceImpl extends ServiceImpl<DormRoomMapper, DormRoom> implements DormRoomService {

    @Autowired
    private DormRoomMapper dormRoomMapper;

    @Override
    public DormRoom findAvailableRoom(Long parkId, String gender, Integer isNightShiftRoom) {
        return dormRoomMapper.findAvailableRoom(parkId, gender, isNightShiftRoom);
    }

    @Override
    public DormRoom findAvailableRoomFlexible(Long parkId, String gender) {
        return dormRoomMapper.findAvailableRoomFlexible(parkId, gender);
    }
}
