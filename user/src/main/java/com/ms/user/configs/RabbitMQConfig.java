package com.ms.user.configs;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMQConfig {
	
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, JacksonJsonMessageConverter messageConverter) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(messageConverter);
		return rabbitTemplate;
		
	}
	
	@Bean
	public JacksonJsonMessageConverter messageConverter() {
		JsonMapper jsonMapper = JsonMapper.builder().build();
		return new JacksonJsonMessageConverter(jsonMapper);
	}

}
