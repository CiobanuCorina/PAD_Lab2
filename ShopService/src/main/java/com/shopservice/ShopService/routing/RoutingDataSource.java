package com.shopservice.ShopService.routing;

import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.logging.Logger;

public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        logger.info(RoutingDataSourceContext.getDataSourceRoutingKey());
        return RoutingDataSourceContext.getDataSourceRoutingKey();
    }
}
