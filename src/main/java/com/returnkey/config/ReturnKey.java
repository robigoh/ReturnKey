/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.returnkey.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 * @author Robi Goh
 */
@SpringBootApplication
@EntityScan("com.returnkey.model")
@ComponentScan({"com.returnkey.controller", "com.returnkey.services", "com.returnkey.config"})
public class ReturnKey {

    public static void main(String[] args) {
        SpringApplication.run(ReturnKey.class, args);
    }
}
