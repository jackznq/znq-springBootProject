package com.znq.rpc.transport;

import com.znq.common.config.ServerConfig;
import com.znq.common.config.ServiceExportConfig;
import com.znq.rpc.ForestRouter;

/**
 * Created by znq on 2016/12/9.
 */
public class ForestServerFactory {

    private ServerConfig serverConfig;

    public ForestServerFactory(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public ForestServer createServer(ForestRouter router, ServiceExportConfig config) throws InterruptedException {
        ForestServer forestServer = new ForestServer(router,serverConfig, config.getPort());
        return forestServer;
    }


}
