package com.scaffolding.strategy;

import com.scaffolding.entity.BusRoute;
import com.scaffolding.entity.Worker;
import com.scaffolding.exception.BusinessException;
import com.scaffolding.mapper.BusRouteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
public class BusMatchingStrategy {

    @Autowired
    private BusRouteMapper busRouteMapper;

    private static final List<String> MORNING_KEYWORDS = Arrays.asList("MORNING", "早班", "上班", "早上", "上午", "GO_WORK");
    private static final List<String> EVENING_KEYWORDS = Arrays.asList("EVENING", "晚班", "下班", "晚上", "下午", "OFF_WORK");

    @Transactional(rollbackFor = Exception.class)
    public BusRoute matchMorningRoute(Worker worker) {
        Long parkId = worker.getParkId();
        if (parkId == null) {
            throw new BusinessException("工人未分配项目地点，无法匹配班车");
        }

        BusRoute route = tryMatchWithKeywords(parkId, MORNING_KEYWORDS);

        if (route == null) {
            route = busRouteMapper.findAnyAvailableRoute(parkId);
            if (route == null) {
                int totalRoutes = busRouteMapper.countRoutesByPark(parkId);
                if (totalRoutes == 0) {
                    throw new BusinessException("该园区(ID:" + parkId + ")尚未配置任何班车线路。" +
                            "请先在【班车线路管理】中配置早班班车(routeType可填: MORNING、早班、上班等)");
                } else {
                    throw new BusinessException("该园区所有班车线路均已满员，请增加班车容量或新增线路。" +
                            "当前共配置" + totalRoutes + "条线路");
                }
            }
        }

        int updated = busRouteMapper.incrementOccupiedCount(route.getId());
        if (updated == 0) {
            throw new BusinessException("早班班车[" + route.getRouteName() + "]已满员，请增加班车配置");
        }

        return route;
    }

    @Transactional(rollbackFor = Exception.class)
    public BusRoute matchEveningRoute(Worker worker) {
        Long parkId = worker.getParkId();
        if (parkId == null) {
            throw new BusinessException("工人未分配项目地点，无法匹配班车");
        }

        BusRoute route = tryMatchWithKeywords(parkId, EVENING_KEYWORDS);

        if (route == null) {
            route = busRouteMapper.findAnyAvailableRoute(parkId);
            if (route == null) {
                int totalRoutes = busRouteMapper.countRoutesByPark(parkId);
                if (totalRoutes == 0) {
                    throw new BusinessException("该园区(ID:" + parkId + ")尚未配置任何班车线路。" +
                            "请先在【班车线路管理】中配置晚班班车(routeType可填: EVENING、晚班、下班等)");
                } else {
                    throw new BusinessException("该园区所有班车线路均已满员，请增加班车容量或新增线路。" +
                            "当前共配置" + totalRoutes + "条线路");
                }
            }
        }

        int updated = busRouteMapper.incrementOccupiedCount(route.getId());
        if (updated == 0) {
            throw new BusinessException("晚班班车[" + route.getRouteName() + "]已满员，请增加班车配置");
        }

        return route;
    }

    private BusRoute tryMatchWithKeywords(Long parkId, List<String> keywords) {
        for (String keyword : keywords) {
            BusRoute route = busRouteMapper.findAvailableRoute(parkId, keyword);
            if (route != null) {
                return route;
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void releaseRoute(Long routeId) {
        if (routeId != null) {
            busRouteMapper.decrementOccupiedCount(routeId);
        }
    }
}
