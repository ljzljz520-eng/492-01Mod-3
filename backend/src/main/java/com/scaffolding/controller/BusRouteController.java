package com.scaffolding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scaffolding.common.PageResult;
import com.scaffolding.common.Result;
import com.scaffolding.entity.BusRoute;
import com.scaffolding.mapper.BusRouteMapper;
import com.scaffolding.service.BusRouteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/bus-route")
@Api(tags = "班车线路管理")
public class BusRouteController {

    @Autowired
    private BusRouteService busRouteService;

    @PostMapping
    @ApiOperation("新增班车线路")
    public Result<BusRoute> save(@RequestBody BusRoute busRoute) {
        try {
            busRoute.setCreateTime(LocalDateTime.now());
            busRoute.setUpdateTime(LocalDateTime.now());
            if (busRoute.getOccupiedCount() == null) {
                busRoute.setOccupiedCount(0);
            }
            busRouteService.save(busRoute);
            return Result.success("新增成功", busRoute);
        } catch (Exception e) {
            log.error("新增班车线路失败", e);
            return Result.error("新增失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("更新班车线路")
    public Result<BusRoute> update(@PathVariable Long id, @RequestBody BusRoute busRoute) {
        try {
            busRoute.setId(id);
            busRoute.setUpdateTime(LocalDateTime.now());
            busRouteService.updateById(busRoute);
            return Result.success("更新成功", busRoute);
        } catch (Exception e) {
            log.error("更新班车线路失败", e);
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除班车线路")
    public Result<?> delete(@PathVariable Long id) {
        try {
            busRouteService.removeById(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除班车线路失败", e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询班车线路")
    public Result<BusRoute> getById(@PathVariable Long id) {
        BusRoute busRoute = busRouteService.getById(id);
        if (busRoute == null) {
            return Result.error("班车线路不存在");
        }
        return Result.success(busRoute);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询班车线路")
    public Result<PageResult<BusRoute>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long parkId,
            @RequestParam(required = false) String routeType) {
        Page<BusRoute> page = new Page<>(current, size);
        LambdaQueryWrapper<BusRoute> wrapper = new LambdaQueryWrapper<>();
        if (parkId != null) {
            wrapper.eq(BusRoute::getParkId, parkId);
        }
        if (routeType != null && !routeType.trim().isEmpty()) {
            wrapper.eq(BusRoute::getRouteType, routeType);
        }
        wrapper.orderByDesc(BusRoute::getCreateTime);
        busRouteService.page(page, wrapper);

        PageResult<BusRoute> pageResult = new PageResult<>(
                page.getTotal(),
                page.getRecords(),
                page.getCurrent(),
                page.getSize()
        );
        return Result.success(pageResult);
    }
}
