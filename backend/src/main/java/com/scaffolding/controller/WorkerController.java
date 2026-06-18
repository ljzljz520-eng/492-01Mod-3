package com.scaffolding.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scaffolding.common.PageResult;
import com.scaffolding.common.Result;
import com.scaffolding.dto.WorkerAssignmentDTO;
import com.scaffolding.entity.Worker;
import com.scaffolding.service.DormBusLinkageService;
import com.scaffolding.service.WorkerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/worker")
@Api(tags = "工人管理")
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    @Autowired
    private DormBusLinkageService dormBusLinkageService;

    @PostMapping
    @ApiOperation("新增工人")
    public Result<Worker> save(@RequestBody Worker worker) {
        try {
            worker.setCreateTime(LocalDateTime.now());
            worker.setUpdateTime(LocalDateTime.now());
            if (worker.getEntryStatus() == null) {
                worker.setEntryStatus("PENDING");
            }
            if (worker.getCheckinStatus() == null) {
                worker.setCheckinStatus("UNASSIGNED");
            }
            if (worker.getNightShiftCount() == null) {
                worker.setNightShiftCount(0);
            }
            workerService.save(worker);
            return Result.success("新增成功", worker);
        } catch (Exception e) {
            log.error("新增工人失败", e);
            return Result.error("新增失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("更新工人信息")
    public Result<Worker> update(@PathVariable Long id, @RequestBody Worker worker) {
        try {
            worker.setId(id);
            worker.setUpdateTime(LocalDateTime.now());
            workerService.updateById(worker);
            return Result.success("更新成功", worker);
        } catch (Exception e) {
            log.error("更新工人信息失败", e);
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除工人")
    public Result<?> delete(@PathVariable Long id) {
        try {
            workerService.removeById(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除工人失败", e);
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询工人")
    public Result<Worker> getById(@PathVariable Long id) {
        Worker worker = workerService.getById(id);
        if (worker == null) {
            return Result.error("工人不存在");
        }
        return Result.success(worker);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询工人")
    public Result<PageResult<Worker>> page(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String workerName,
            @RequestParam(required = false) String workerGender,
            @RequestParam(required = false) String entryStatus,
            @RequestParam(required = false) String checkinStatus,
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) Long parkId,
            @RequestParam(required = false) Integer isOutOfTown) {
        Page<Worker> page = new Page<>(current, size);
        LambdaQueryWrapper<Worker> wrapper = new LambdaQueryWrapper<>();
        if (workerName != null && !workerName.trim().isEmpty()) {
            wrapper.like(Worker::getWorkerName, workerName);
        }
        if (workerGender != null && !workerGender.trim().isEmpty()) {
            wrapper.eq(Worker::getWorkerGender, workerGender);
        }
        if (entryStatus != null && !entryStatus.trim().isEmpty()) {
            wrapper.eq(Worker::getEntryStatus, entryStatus);
        }
        if (checkinStatus != null && !checkinStatus.trim().isEmpty()) {
            wrapper.eq(Worker::getCheckinStatus, checkinStatus);
        }
        if (teamId != null) {
            wrapper.eq(Worker::getTeamId, teamId);
        }
        if (parkId != null) {
            wrapper.eq(Worker::getParkId, parkId);
        }
        if (isOutOfTown != null) {
            wrapper.eq(Worker::getIsOutOfTown, isOutOfTown);
        }
        wrapper.orderByDesc(Worker::getCreateTime);
        workerService.page(page, wrapper);

        PageResult<Worker> pageResult = new PageResult<>(
                page.getTotal(),
                page.getRecords(),
                page.getCurrent(),
                page.getSize()
        );
        return Result.success(pageResult);
    }

    @PostMapping("/confirm-entry/{workerId}")
    @ApiOperation("【入口】确认工人入职并自动安排宿舍和班车（外地工人）")
    public Result<Map<String, Object>> confirmEntryAndArrange(
            @ApiParam(value = "工人ID", required = true) @PathVariable Long workerId) {
        try {
            Worker worker = workerService.getById(workerId);
            if (worker == null) {
                return Result.error("工人不存在");
            }

            worker.setEntryStatus("CONFIRMED");
            worker.setUpdateTime(LocalDateTime.now());
            workerService.updateById(worker);

            if (worker.getIsOutOfTown() != null && worker.getIsOutOfTown() == 1) {
                Map<String, Object> arrangeResult = dormBusLinkageService.handleEntryConfirmation(workerId);
                return Result.success("入职确认成功，已自动安排宿舍和班车", arrangeResult);
            } else {
                return Result.success("入职确认成功（本地工人无需安排宿舍和班车）", null);
            }
        } catch (Exception e) {
            log.error("确认工人入职失败，workerId: {}", workerId, e);
            return Result.error("入职确认失败：" + e.getMessage());
        }
    }

    @GetMapping("/{workerId}/assignment")
    @ApiOperation("查询工人住宿和班车安排详情")
    public Result<WorkerAssignmentDTO> getAssignmentDetail(
            @ApiParam(value = "工人ID", required = true) @PathVariable Long workerId) {
        try {
            WorkerAssignmentDTO detail = dormBusLinkageService.getWorkerAssignmentDetail(workerId);
            if (detail == null) {
                return Result.error("未找到该工人的安排信息");
            }
            return Result.success("查询成功", detail);
        } catch (Exception e) {
            log.error("查询工人安排详情失败，workerId: {}", workerId, e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}
