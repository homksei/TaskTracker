package com.tasktracker.application;

import static org.assertj.core.api.Assertions.assertThat;


import com.tasktracker.application.controllers.TestController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SmokeTest {
	
	@Autowired
	private TestController controller;
	
	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}
}
