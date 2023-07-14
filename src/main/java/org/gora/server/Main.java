package org.gora.server;

import lombok.extern.slf4j.Slf4j;
import org.gora.server.common.eEnv;
//import org.gora.server.pipe.MessagePipelineFactory;
import org.gora.server.runner.UdpServer;
import org.gora.server.runner.Receiver;
import org.gora.server.runner.Sender;
import org.gora.server.common.CommonUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Main {
    private static final long SLEEP_MILLIS = 10;

    private static void sleep(){
        try {
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            log.error("Thread sleep 에러");
            log.error(CommonUtils.getStackTraceElements(e));
            Thread.currentThread().interrupt();
        }
    }

    private static void join(List<Thread> threadList){
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.error("스레드 종료 까지 대기 실패");
                log.error(CommonUtils.getStackTraceElements(e));
                log.info("현재 실행중인 스레드 interrupt 실행");
                Thread.currentThread().interrupt();
            }
        }
    }
    private static Thread sender() {
        return new Thread(() -> {
            while(true) {
                sleep();
                Sender.run();
            }
        });
    }
    private static Thread serverListener(){
        return new Thread(() -> {
            int port = Integer.parseInt(CommonUtils.getEnv(eEnv.SERVER_PORT, "11111"));
            UdpServer udpServer = null;
            try {
                udpServer = new UdpServer();
                udpServer.startup(port);
            } catch (Exception e) {
                if (udpServer != null) {
                    udpServer.shutdown();
                }
                log.error(CommonUtils.getStackTraceElements(e));
            }
        });
    }

    public static void main(String[] args) {
        log.info("start realtime");

//        server listener
        List<Thread> threadList = new ArrayList<>(10);
        threadList.add(serverListener());
        log.info("UDP server startUp");

//        send que reader
        threadList.add(sender());

//        receive que reader
        threadList.add(receive());
        start(threadList);
        join(threadList);
    }
    private static void start(List<Thread> threadList){
        for (Thread thread : threadList) {
            thread.start();
        }
    }
    private static Thread receive() {
        return new Thread(() -> {
            while(true) {
                sleep();
                Receiver.run();
            }
        });
    }
}