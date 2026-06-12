package com.scaffolding.strategy;

import com.scaffolding.entity.BusRoute;
import com.scaffolding.entity.Worker;
import com.scaffolding.exception.BusinessException;
import com.scaffolding.mapper.BusRouteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BusMatchingStrategy {

    @Autowired
    private BusRouteMapper busRouteMapper;

    private static final String ROUTE_TYPE_MORNING = "MORNING";
    private static final String ROUTE_TYPE_EVENING = "EVENING";

    @Transactional(rollbackFor = Exception.class)
    public BusRoute matchMorningRoute(Worker worker) {
        Long parkId = worker.getParkId();
        if (parkId == null) {
            throw new BusinessException("工人未分配项目地点，无法匹配班车");
        }

        BusRoute route = busRouteMapper.findAvailableRoute(parkId, ROUTE_TYPE_MORNING);
        if (route == null) {
            throw new BusinessException("该园区暂无早班班车线路，请先配置班车");
        }

        int updated = busRouteMapper.incrementOccupiedCount(route.getId());
        if (updated == 0) {
            throw new BusinessException("早班班车" + route.getRouteName() + "已满员，请增加班车配置");
        }

        return route;
    }

    @Transactional(rollbackFor = Exception.class)
    public BusRoute matchEveningRoute(Worker worker) {
        Long parkId = worker.getParkId();
        if (parkId == null) {
            throw new BusinessException("工人未分配项目地点，无法匹配班车");
        }

        BusRoute route = busRouteMapper.findAvailableRoute(parkId, ROUTE_TYPE_EVENING);
        if (route == null) {
            throw new BusinessException("该园区暂无晚班班车线路，请先配置班车");
        }

        int updated = busRouteMapper.incrementOccupiedCount(route.getId());
        if (updated == 0) {
            throw new BusinessException("晚班班车" + route.getRouteName() + "已满员，请增加班车配置");
        }

        return route;
    }

    @Transactional(rollbackFor = Exception.class)
    public void releaseRoute(Long routeId) {
        if (routeId != null) {
            busRouteMapper.decrementOccupiedCount(routeId);
        }
    }
}
