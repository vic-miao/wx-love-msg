package com.xiake.listener;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author vic_miao
 * @description
 * @date 2021年12月08日 10:06
 */
@Component
public class SystemRunListener implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=================启动成功============================");
    }
}
