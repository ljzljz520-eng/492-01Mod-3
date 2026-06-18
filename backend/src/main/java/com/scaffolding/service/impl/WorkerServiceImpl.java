package com.scaffolding.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scaffolding.entity.Worker;
import com.scaffolding.mapper.WorkerMapper;
import com.scaffolding.service.WorkerService;
import org.springframework.stereotype.Service;

@Service
public class WorkerServiceImpl extends ServiceImpl<WorkerMapper, Worker> implements WorkerService {
}
