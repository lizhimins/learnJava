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

package com.study.concurrent;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletableFutureDemo {

    public static void main(String[] args) {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 123;
        });

        future.thenAccept(result -> {
            System.out.println("result：" + result);
        });
        System.out.println("main thread done");

        Callable<String> task = () -> {
            TimeUnit.SECONDS.sleep(1);
            return "callable thread finish";
        };

        ListeningExecutorService executor =
            MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
        ListenableFuture<String> listenableFuture = executor.submit(task);
        listenableFuture.addListener(() -> {
            try {
                System.out.print("result: " + future.get());
                executor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, executor);
    }
}
