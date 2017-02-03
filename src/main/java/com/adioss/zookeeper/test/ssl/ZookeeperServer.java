package com.adioss.zookeeper.test.ssl;

import java.util.concurrent.*;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;

public class ZookeeperServer {
    private ZookeeperServer() {
        System.setProperty("log4j.configuration", "file:" + ZookeeperServer.class.getClassLoader().getResource("server/log4j.properties").getPath());
    }

    private void start() {
        QuorumPeerMain.main(new String[]{ZookeeperServer.class.getClassLoader().getResource("server/zk.cfg").getPath()});
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new Runnable() {
            public void run() {
                new ZookeeperServer().start();
            }
        });
    }
}
