package com.example.demo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import com.example.demo.templates.SwingGUIView;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DemoApplication.class)
                .headless(false)
                .web(WebApplicationType.NONE) // Важно: отключаем веб-сервер
                .run(args);
    }

    @Bean
    public CommandLineRunner run(SwingGUIView swingGUIView) {
        return args -> {
            System.out.println("CommandLineRunner executed - starting GUI");
            swingGUIView.start();
        };
    }
}