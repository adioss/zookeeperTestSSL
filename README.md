# zookeeper test SSL
Test Zookeeper SSL features on Zookeeper 3.5.2-alpha

## Content
* com.adioss.zookeeper.test.ssl.ZookeeperServer : start a ZK server using NettyServerCnxnFactory
* com.adioss.zookeeper.test.ssl.ZookeeperSenderRetriever: start ZK client using ClientCnxnSocketNetty
  * ensure that a znode is created
  * send data
  * retrieve after few seconds
  * loop until death

Keystores contain keypair generated for CN=localhost (cert req signed by a CA created using keytool (ca.jks))

Trustores contain CA certificate
