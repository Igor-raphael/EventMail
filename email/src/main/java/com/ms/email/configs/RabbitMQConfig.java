package com.ms.email.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMQConfig {

	@Value("${broker.queue.email.name}")
	private String queue;
	
	@Bean
	public Queue queue() {
		return new Queue(queue, true);
	}
	
	@Bean
	public JacksonJsonMessageConverter messageConverter() {
		JsonMapper jsonMapper = JsonMapper.builder().build();
		JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter(jsonMapper);
		converter.setAlwaysConvertToInferredType(true);
		return converter;
	}
	
	 @Bean
	    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
	            ConnectionFactory connectionFactory,
	            JacksonJsonMessageConverter messageConverter) {
	        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
	        factory.setConnectionFactory(connectionFactory);
	        factory.setMessageConverter(messageConverter);
	        return factory;
	    }
	
}
