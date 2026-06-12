package com.scaffolding.controller;

import com.scaffolding.common.Result;
import com.scaffolding.dto.TeamTransferDTO;
import com.scaffolding.dto.UnconfirmedCheckinDTO;
import com.scaffolding.dto.WorkerAssignmentDTO;
import com.scaffolding.service.DormBusLinkageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/dorm-bus")
@Api(tags = "宿舍班车联动管理")
public class DormBusController {

    @Autowired
    private DormBusLinkageService dormBusLinkageService;

    @PostMapping("/entry-confirm/{workerId}")
    @ApiOperation("外地工人确认入职后安排床位和班车")
    public Result<Map<String, Object>> handleEntryConfirmation(
            @ApiParam(value = "工人ID", required = true) @PathVariable Long workerId) {
        try {
            Map<String, Object> result = dormBusLinkageService.handleEntryConfirmation(workerId);
            return Result.success("入职安排成功", result);
        } catch (Exception e) {
            log.error("入职安排失败，workerId: {}", workerId, e);
            return Result.error("入职安排失败：" + e.getMessage());
        }
    }

    @PostMapping("/team-transfer")
    @ApiOperation("班组调园-联动调整床位、班车和到岗提醒")
    public Result<Map<String, Object>> handleTeamTransfer(@RequestBody TeamTransferDTO transferDTO) {
        try {
            Map<String, Object> result = dormBusLinkageService.handleTeamTransfer(transferDTO);
            return Result.success("调园安排成功", result);
        } catch (Exception e) {
            log.error("班组调园失败，teamId: {}", transferDTO.getTeamId(), e);
            return Result.error("调园安排失败：" + e.getMessage());
        }
    }

    @GetMapping("/unconfirmed-checkin")
    @ApiOperation("人事查询未确认入住人员列表（带详细信息）")
    public Result<List<UnconfirmedCheckinDTO>> getUnconfirmedCheckinList() {
        try {
            List<UnconfirmedCheckinDTO> list = dormBusLinkageService.getUnconfirmedCheckinListWithDetail();
            return Result.success("查询成功", list);
        } catch (Exception e) {
            log.error("查询未确认入住列表失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/worker/{workerId}")
    @ApiOperation("查询工人住宿和班车安排详情")
    public Result<WorkerAssignmentDTO> getWorkerAssignmentDetail(
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

    @PutMapping("/confirm-checkin/{workerId}")
    @ApiOperation("确认工人入住")
    public Result<?> confirmWorkerCheckin(
            @ApiParam(value = "工人ID", required = true) @PathVariable Long workerId) {
        try {
            boolean success = dormBusLinkageService.confirmWorkerCheckin(workerId);
            if (success) {
                return Result.success("入住确认成功");
            }
            return Result.error("入住确认失败");
        } catch (Exception e) {
            log.error("确认入住失败，workerId: {}", workerId, e);
            return Result.error("确认失败：" + e.getMessage());
        }
    }

    @PostMapping("/arrival-reminder/{workerId}")
    @ApiOperation("发送到岗提醒")
    public Result<?> sendArrivalReminder(
            @ApiParam(value = "工人ID", required = true) @PathVariable Long workerId) {
        try {
            boolean success = dormBusLinkageService.sendArrivalReminder(workerId);
            if (success) {
                return Result.success("到岗提醒已发送");
            }
            return Result.error("提醒发送失败");
        } catch (Exception e) {
            log.error("发送到岗提醒失败，workerId: {}", workerId, e);
            return Result.error("提醒发送失败：" + e.getMessage());
        }
    }
}
