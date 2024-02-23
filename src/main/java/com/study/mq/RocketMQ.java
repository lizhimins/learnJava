/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.study.mq;

import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.utils.ThreadUtils;
import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class RocketMQ {

    private static final Logger log = LoggerFactory.getLogger(RocketMQ.class);

    private static RPCHook getAclRPCHook(String accessKey, String secretKey) {
        return new AclClientRPCHook(new SessionCredentials(accessKey, secretKey));
    }

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {

        int total = 100;
        String nameServerAddress = "xxx.cn-hangzhou.rmq.aliyuncs.com:8080";
        String topic = "TopicTest";
        String accessKey = "xxx";
        String secretKey = "xxx";
        String groupId = "PID_GROUP_TEST";

        RPCHook rpcHook = getAclRPCHook(accessKey, secretKey);
        AtomicLong times = new AtomicLong(0L);
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("", "test", rpcHook);
        consumer.setNamesrvAddr(nameServerAddress);
        consumer.subscribe(topic, "*");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt messageExt : msgs) {
                log.info("receive index: {}, messageId: {}", times.incrementAndGet(), messageExt.getMsgId());
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();

        DefaultMQProducer producer = new DefaultMQProducer(groupId, rpcHook);
        producer.setVipChannelEnabled(false);
        producer.setNamesrvAddr(nameServerAddress);
        producer.start();

        byte[] bytes = "PAYLOAD".getBytes(RemotingHelper.DEFAULT_CHARSET);
        for (int i = 0; i < total; i++) {
            try {
                Message msg = new Message(topic, bytes);
                SendResult sendResult = producer.send(msg);
                if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                    System.out.printf("send messageId: %s%n", sendResult.getMsgId());
                }
            } catch (Exception e) {
                log.warn("send error", e);
            }
            TimeUnit.SECONDS.sleep(1);
        }

        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
            ThreadUtils.newGenericThreadFactory("MessageCounter", true));

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            long receive = times.get();
            if (receive >= total * 3) {
                System.out.printf("Terminating after 120s...%n");
                try {
                    TimeUnit.SECONDS.sleep(120);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.exit(0);
            } else {
                System.out.printf("receive message total: %d%n", receive);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
}
