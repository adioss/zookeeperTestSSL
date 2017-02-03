# zookeeper test SSL
Test Zookeeper SSL features

## Content
* com.adioss.zookeeper.test.ssl.ZookeeperServer : start the zk server using NettyServerCnxnFactory
* com.adioss.zookeeper.test.ssl.ZookeeperSenderRetriever: start zk client using ClientCnxnSocketNetty
  * ensure that a znode is created
  * send data
  * retrieve after few seconds
  * loop until death

Keystores generated for CN=localhost (cert req signed by a CA created using keytool (ca.jks))

Trustores contain CA certificate
