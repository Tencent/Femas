package com.tencent.tsf.femas.governance.route;

import com.tencent.tsf.femas.common.context.Context;
import com.tencent.tsf.femas.common.context.factory.ContextFactory;
import com.tencent.tsf.femas.common.entity.Service;
import com.tencent.tsf.femas.common.entity.ServiceInstance;
import com.tencent.tsf.femas.common.exception.FemasRuntimeException;
import com.tencent.tsf.femas.common.tag.TagRule;
import com.tencent.tsf.femas.common.tag.engine.TagEngine;
import com.tencent.tsf.femas.common.util.CollectionUtil;
import com.tencent.tsf.femas.governance.config.impl.ServiceRouterConfigImpl;
import com.tencent.tsf.femas.governance.event.RouterEventCollector;
import com.tencent.tsf.femas.governance.plugin.context.ConfigContext;
import com.tencent.tsf.femas.governance.route.entity.RouteDest;
import com.tencent.tsf.femas.governance.route.entity.RouteRule;
import com.tencent.tsf.femas.governance.route.entity.RouteRuleGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FemasDefaultRouteRuleRouter implements Router {

    private static final Logger logger = LoggerFactory.getLogger(FemasDefaultRouteRuleRouter.class);

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private volatile Context commonContext = ContextFactory.getContextInstance();

    @Override
    public Collection<ServiceInstance> route(Service service, Collection<ServiceInstance> instances) {
        RouteRuleGroup routeRuleGroup = RouterRuleManager.getRouteRuleGroup(service);

        if (CollectionUtil.isEmpty(instances) || routeRuleGroup == null) {
            if (CollectionUtil.isEmpty(instances)) {
                RouterEventCollector
                        .addRouterEvent(service, routeRuleGroup, Context.getAllSystemTags(), "no available instance!");
            }
            return instances;
        }

        boolean hit = false;
        if (!CollectionUtil.isEmpty(routeRuleGroup.getRuleList())) {
            for (RouteRule routeRule : routeRuleGroup.getRuleList()) {
                if (checkRouteRuleHit(routeRule)) {
                    hit = true;

                    // ???????????????????????????????????????
                    List<ServiceInstance> instanceList = chooseInstanceByRouteRule(routeRule, instances);

                    if (!CollectionUtil.isEmpty(instanceList)) {
                        return instanceList;
                    }
                }
            }
        }

        /**
         * ???????????????????????????????????????
         */
        if (hit && routeRuleGroup.getFallback() == false) {
            RouterEventCollector
                    .addRouterEvent(service, routeRuleGroup, Context.getAllSystemTags(), "no available instance!");
            throw new RuntimeException("No available instances.");
        }
        if (hit) {
            RouterEventCollector
                    .addRouterEvent(service, routeRuleGroup, Context.getAllSystemTags(), "tolerant protection");
        }

        return instances;
    }

    @Override
    public String name() {
        return "FEMAS-DEFAULT-ROUTE-RULE-ROUTER";
    }

    @Override
    public int priority() {
        return 200;
    }

    /**
     * @param routeRule
     * @return
     */
    private List<ServiceInstance> chooseInstanceByRouteRule(RouteRule routeRule,
            Collection<ServiceInstance> instances) {
        Map<RouteDest, List<ServiceInstance>> routeDestInstanceMap = new HashMap<>();

        for (ServiceInstance instance : instances) {
            // ????????????routeDest
            // TODO ????????????????????? routeDest ???????????????????????????????????????????????? break
            for (RouteDest routeDest : routeRule.getDestList()) {
                if (matchRouteDest(instance, routeDest)) {
                    routeDestInstanceMap.computeIfAbsent(routeDest, k -> new ArrayList());
                    routeDestInstanceMap.get(routeDest).add(instance);
                } else {
                    routeDestInstanceMap.computeIfAbsent(routeDest, k -> new ArrayList());
                }
            }
        }

        // ??????????????????rule???????????????????????????????????????null
        if (routeDestInstanceMap.isEmpty()) {
            return null;
        }

        RouteDest dest = randomRouteDest(routeDestInstanceMap.keySet());
        if (dest != null) {
            return routeDestInstanceMap.get(dest);
        }

        return null;
    }

    private Boolean matchRouteDest(ServiceInstance endpoint, RouteDest routeDest) {
        TagRule destItemList = routeDest.getDestItemList();

        // SysTag ?????????group???version???
        // UserTag?????????????????????????????????
        return TagEngine.checkRuleHit(destItemList, endpoint.getAllMetadata(), endpoint.getTags());
    }

    /**
     * ??? routeDestList ???????????????routeDestList?????????????????????????????? routeDest
     *
     * @return
     */
    private RouteDest randomRouteDest(Collection<RouteDest> routeDestCollection) {
        if (!CollectionUtil.isEmpty(routeDestCollection)) {
            int sum = 0;
            Map<RouteDest, Integer> weightMap = new HashMap<>();
            for (RouteDest routeDest : routeDestCollection) {
                weightMap.put(routeDest, routeDest.getDestWeight());
                sum += routeDest.getDestWeight();
            }

            int random = RANDOM.nextInt(sum);
            int current = 0;
            for (Map.Entry<RouteDest, Integer> entry : weightMap.entrySet()) {
                current += entry.getValue();
                if (random < current) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }

    /**
     * ?????? RouteRuleTagList ??????????????????
     * TAG ???????????????TAG??????????????????????????????????????????????????????true??? ?????????false
     *
     * @param routeRule ????????????TAG??????
     * @return ????????????
     */
    private Boolean checkRouteRuleHit(RouteRule routeRule) {
        if (routeRule.getTagRule() != null) {
            return TagEngine.checkRuleHitByCurrentTags(routeRule.getTagRule());
        }

        // routeTagList ?????? routeTag ??????????????????????????????????????????routeTagList
        return true;
    }

    @Override
    public String getName() {
        return "FemasDefaultRoute";
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void init(ConfigContext conf) throws FemasRuntimeException {
        ServiceRouterConfigImpl routerConfig = (ServiceRouterConfigImpl) conf.getConfig().getServiceRouter();
        if (routerConfig == null || routerConfig.getRouteRule() == null) {
            return;
        }
        Service service = new Service();
        RouteRuleGroup routeRuleGroup = routerConfig.getRouteRule();
        service.setName(routeRuleGroup.getServiceName());
        service.setNamespace(routeRuleGroup.getNamespace());
        try {
            RouterRuleManager.refreshRouteRule(service, routeRuleGroup);
        } catch (Exception e) {
            throw new FemasRuntimeException("route rule init refresh error");
        }
        logger.info("init circuit breaker rule: {}", routeRuleGroup.toString());
    }

    @Override
    public void destroy() {

    }
}
