package org.gora;

import lombok.extern.slf4j.Slf4j;
import org.gora.constant.eEnv;
import org.gora.pipe.MessagePipelineFactory;
import org.gora.runner.Server;
import org.gora.runner.Receiver;
import org.gora.runner.Sender;
import org.gora.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Main {
    private static Thread serverListener(){
        return new Thread(() -> {
            int port = Integer.parseInt(CommonUtils.getEnv(eEnv.PORT, "11111"));
            Server server = null;
            try {
                server = new Server(MessagePipelineFactory.class);
                server.startup(port);
            } catch (Exception e) {
                if (server != null) {
                    server.shutdown();
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

        join(threadList);
    }

    private static Thread receive() {
        return new Thread(() -> {
            while(true) {
                sleep(10);
                Receiver.run();
            }
        });
    }
    private static void sleep(long millis){
        try {
            Thread.sleep(millis);
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
                sleep(10);
                Sender.run();
            }
        });
    }
}