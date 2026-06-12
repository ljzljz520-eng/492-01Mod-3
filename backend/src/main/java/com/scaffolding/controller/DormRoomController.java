package com.scaffolding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scaffolding.common.PageResult;
import com.scaffolding.common.Result;
import com.scaffolding.entity.DormRoom;
import com.scaffolding.mapper.DormRoomMapper;
import com.scaffolding.service.DormRoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/dorm-room")
@Api(tags = "宿舍房间管理")
public class DormRoomController {

    @Autowired
    private DormRoomService dormRoomService;

    @PostMapping
    @ApiOperation("新增宿舍房间")
    public Result<DormRoom> save(@RequestBody DormRoom dormRoom) {
        try {
            dormRoom.setCreateTime(LocalDateTime.now());
            dormRoom.setUpdateTime(LocalDateTime.now());
            if (dormRoom.getOccupiedCount() == null) {
                dormRoom.setOccupiedCount(0);
            }
            dormRoomService.save(dormRoom);
            return Result.success("新增成功", dormRoom);
        } catch (Exception e) {
            log.error("新增宿舍房间失败", e);
            return Result.error("新增失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("更新宿舍房间")
    public Result<DormRoom> update(@PathVariable Long id, @RequestBody DormRoom dormRoom) {
        try {
            dormRoom.setId(id);
            dormRoom.setUpdateTime(LocalDateTime.now());
            dormRoomService.updateById(dormRoom);
            return Result.success("更新成功", dormRoom);
        } catch (Exception e) {
            log.error("更新宿舍房间失败", e);
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除宿舍房间")
    public Result<?> delete(@PathVariable Long id) {
        try {
            dormRoomService.removeById(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除宿舍房间失败", e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询宿舍房间")
    public Result<DormRoom> getById(@PathVariable Long id) {
        DormRoom dormRoom = dormRoomService.getById(id);
        if (dormRoom == null) {
            return Result.error("宿舍房间不存在");
        }
        return Result.success(dormRoom);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询宿舍房间")
    public Result<PageResult<DormRoom>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long parkId,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String roomGender,
            @RequestParam(required = false) Integer isNightShiftRoom) {
        Page<DormRoom> page = new Page<>(current, size);
        LambdaQueryWrapper<DormRoom> wrapper = new LambdaQueryWrapper<>();
        if (parkId != null) {
            wrapper.eq(DormRoom::getParkId, parkId);
        }
        if (buildingId != null) {
            wrapper.eq(DormRoom::getBuildingId, buildingId);
        }
        if (roomGender != null && !roomGender.trim().isEmpty()) {
            wrapper.eq(DormRoom::getRoomGender, roomGender);
        }
        if (isNightShiftRoom != null) {
            wrapper.eq(DormRoom::getIsNightShiftRoom, isNightShiftRoom);
        }
        wrapper.orderByAsc(DormRoom::getFloor, DormRoom::getRoomNumber);
        dormRoomService.page(page, wrapper);

        PageResult<DormRoom> pageResult = new PageResult<>(
                page.getTotal(),
                page.getRecords(),
                page.getCurrent(),
                page.getSize()
        );
        return Result.success(pageResult);
    }

    @GetMapping("/available")
    @ApiOperation("查询可用空房")
    public Result<DormRoom> findAvailableRoom(
            @RequestParam Long parkId,
            @RequestParam String gender,
            @RequestParam(required = false) Integer isNightShiftRoom) {
        try {
            DormRoom room;
            if (isNightShiftRoom != null) {
                room = dormRoomService.findAvailableRoom(parkId, gender, isNightShiftRoom);
            } else {
                room = dormRoomService.findAvailableRoomFlexible(parkId, gender);
            }
            if (room == null) {
                return Result.error("暂无可用房间");
            }
            return Result.success(room);
        } catch (Exception e) {
            log.error("查询可用房间失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}
