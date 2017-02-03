package com.adioss.zookeeper.test.ssl;

import java.util.*;
import java.util.concurrent.*;
import org.apache.log4j.BasicConfigurator;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.client.ZKClientConfig;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperSenderRetriever {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperSenderRetriever.class);
    private static final String ZNODE_PATH = "/test";
    private static final String ZK_CLIENT_CONFIG = ZookeeperSenderRetriever.class.getClassLoader().getResource("client/zkClient.cfg").getPath();
    private static final String ZK_CLIENT_LOG4J = ZookeeperSenderRetriever.class.getClassLoader().getResource("client/log4j.properties").getPath();

    private ZookeeperSenderRetriever() {
        System.setProperty("log4j.configuration", "file:" + ZK_CLIENT_LOG4J);
        BasicConfigurator.configure();
    }

    private void sendAndRetrieve(String host) {
        try {
            LOG.info("Starting zk client");
            ZooKeeper zookeeperClient = createZookeeperClient(host);
            LOG.info("Zk client started");
            Stat stat = isZNodeExists(zookeeperClient, ZNODE_PATH);

            if (stat != null) {
                LOG.info("Node exists and the node version is " + stat.getVersion());
            } else {
                LOG.info("Node does not exists");
                createZNode(zookeeperClient, ZNODE_PATH, getRandomStringData());
            }
            while (true) {
                updateZNodeData(zookeeperClient, ZNODE_PATH, getRandomStringData());
                Thread.sleep(5000);
                getZNodeData(zookeeperClient, ZNODE_PATH);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    private byte[] getZNodeData(final ZooKeeper zookeeperClient, final String zNodePath) throws Exception {
        return zookeeperClient.getData(zNodePath, new Watcher() {
            public void process(WatchedEvent we) {
                if (we.getType() == Event.EventType.None) {
                    switch (we.getState()) {
                        case Expired:
                            break;
                    }
                } else {
                    try {
                        LOG.info("=========> Data retrieved : " + new String(zookeeperClient.getData(zNodePath, false, null), "UTF-8"));
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage());
                    }
                }
            }
        }, null);
    }

    private static void createZNode(ZooKeeper zookeeperClient, String zNodePath, byte[] data) throws Exception {
        zookeeperClient.create(zNodePath, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    private static void updateZNodeData(ZooKeeper zookeeperClient, String zNodePath, byte[] data) throws Exception {
        zookeeperClient.setData(zNodePath, data, zookeeperClient.exists(zNodePath, true).getVersion());
        LOG.info("=========> Data sent : " + new String(data, "UTF-8"));
    }

    private static Stat isZNodeExists(ZooKeeper zookeeperClient, String zNodePath) throws Exception {
        return zookeeperClient.exists(zNodePath, true);
    }

    private ZooKeeper createZookeeperClient(String host) throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(host, 5000, new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }
            }
        }, new ZKClientConfig(ZK_CLIENT_CONFIG));
        countDownLatch.await();
        return zooKeeper;
    }

    private byte[] getRandomStringData() {
        return UUID.randomUUID().toString().getBytes();
    }

    public static void main(String... args) {
        new ZookeeperSenderRetriever().sendAndRetrieve("localhost:2181");
    }
}
