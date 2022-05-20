package com.atguigu.gulimall.search.thread;/**
 * @author chenfl
 * @create 2022-03-04-17:01
 */

import java.util.concurrent.*;

/**
 * @author chenfl
 * @description
 * @date 2022/3/4 17:01
 */
public class test {

    //当前系统中池只有一两个，每隔异步任务，提交给线程池让他自己去执行就行
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main方法。。。。。。开始");
        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询图片信息");
            return "img";
        }, executorService);

        CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询规格信息");
            return "256G";
        }, executorService);

        CompletableFuture<Object> future3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询介绍");
            return "华为";
        }, executorService);

        CompletableFuture<Object> objectCompletableFuture = CompletableFuture.anyOf(future1, future2, future3);

        // voidCompletableFuture.join();
        objectCompletableFuture.get();

        System.out.println("main方法。。。。。。结束"+objectCompletableFuture.get());
    }
}