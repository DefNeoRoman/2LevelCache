package com.example.LevelCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {
	@Autowired
	SimpleService service;
	public static void main(String[] args) {
		ConfigurableApplicationContext context =  SpringApplication.run(Application.class, args);
//
		context.getBean(SimpleService.class).fillRepository();
		System.out.println(context.getBean(SimpleService.class).getAll());
	}
}
