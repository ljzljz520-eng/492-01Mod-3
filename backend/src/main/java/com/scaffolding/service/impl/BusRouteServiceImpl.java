package com.scaffolding.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scaffolding.entity.BusRoute;
import com.scaffolding.mapper.BusRouteMapper;
import com.scaffolding.service.BusRouteService;
import org.springframework.stereotype.Service;

@Service
public class BusRouteServiceImpl extends ServiceImpl<BusRouteMapper, BusRoute> implements BusRouteService {
}
