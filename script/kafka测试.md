1.创建主题命令
kafka-topics.sh --create --topic test-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

2.列出主题：
kafka-topics.sh --list --bootstrap-server localhost:9092

3.测试生产和消费消息
kafka-console-producer.sh --topic test-topic --bootstrap-server localhost:9092
输入几条消息（例如 hello kafka），然后按 Ctrl+C 退出。

4.消费消息
 kafka-console-consumer.sh --topic test-topic --bootstrap-server localhost:9092 --from-beginning
参数名：--from-beginning
含义：指示消费者从主题的最早偏移量（offset）开始读取消息，而不是从最新的偏移量开始。
作用：默认情况下，消费者只读取启动后新写入的消息。加上 --from-beginning 后，消费者会从主题的第一个消息（最早的可用偏移量）开始消费，适用于查看历史数据。
注意：
如果主题的消息被清理（超过保留时间或大小限制），最早的消息可能已不可用，此时从最早的可访问偏移量开始。
如果不加此参数，默认行为是从最新偏移量（即实时消息）开始消费。











