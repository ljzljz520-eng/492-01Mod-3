package com.scaffolding.strategy;

import com.scaffolding.entity.DormRoom;
import com.scaffolding.entity.Worker;
import com.scaffolding.exception.BusinessException;
import com.scaffolding.mapper.DormRoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DormAssignmentStrategy {

    @Autowired
    private DormRoomMapper dormRoomMapper;

    private static final int NIGHT_SHIFT_THRESHOLD = 3;

    @Transactional(rollbackFor = Exception.class)
    public DormRoom assignRoom(Worker worker) {
        Long parkId = worker.getParkId();
        String gender = worker.getWorkerGender();
        Integer nightShiftCount = worker.getNightShiftCount();

        if (parkId == null) {
            throw new BusinessException("工人未分配项目地点，无法安排床位");
        }
        if (gender == null || gender.trim().isEmpty()) {
            throw new BusinessException("工人性别信息缺失，无法安排床位");
        }

        Integer isNightShiftRoom = (nightShiftCount != null && nightShiftCount >= NIGHT_SHIFT_THRESHOLD) ? 1 : 0;

        DormRoom room = dormRoomMapper.findAvailableRoom(parkId, gender, isNightShiftRoom);

        if (room == null) {
            room = dormRoomMapper.findAvailableRoomFlexible(parkId, gender);
            if (room == null) {
                throw new BusinessException("该园区暂无" + getGenderText(gender) + "空闲床位，请先增加宿舍配置");
            }
        }

        int updated = dormRoomMapper.incrementOccupiedCount(room.getId());
        if (updated == 0) {
            throw new BusinessException("床位分配失败，房间" + room.getRoomNumber() + "已满");
        }

        return room;
    }

    public Integer assignBedNumber(Long roomId) {
        Integer bedNumber = dormRoomMapper.getNextAvailableBedNumber(roomId);
        if (bedNumber == null) {
            bedNumber = 1;
        }

        int maxRetries = 10;
        int retryCount = 0;
        while (retryCount < maxRetries) {
            int count = dormRoomMapper.countBedOccupancy(roomId, bedNumber);
            if (count == 0) {
                return bedNumber;
            }
            bedNumber++;
            retryCount++;
        }

        throw new BusinessException("无法分配床位号，请检查房间配置");
    }

    @Transactional(rollbackFor = Exception.class)
    public void releaseRoom(Long roomId) {
        if (roomId != null) {
            dormRoomMapper.decrementOccupiedCount(roomId);
        }
    }

    private String getGenderText(String gender) {
        if ("男".equals(gender) || "MALE".equalsIgnoreCase(gender)) {
            return "男性";
        } else if ("女".equals(gender) || "FEMALE".equalsIgnoreCase(gender)) {
            return "女性";
        }
        return "";
    }
}
