package com.ms.user.producers;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ms.user.dtos.EmailDto;
import com.ms.user.models.UserModel;

@Component
public class UserProducer {

	final RabbitTemplate rabbitTemplate;
	
	public UserProducer(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}
	
	@Value(value = "${broker.queue.email.name}")
	private String routingKey;
	
	public void publishMessageEmail(UserModel model) {
		
		var emailDto = new EmailDto();
		
		emailDto.setUserId(model.getUserId());
		emailDto.setEmailTo(model.getEmail());
		emailDto.setSubject("Cadastro realizado com sucesso!");
		emailDto.setText(model.getName() + ", seja bem vindo(a)!");
		
		
		rabbitTemplate.convertAndSend("", routingKey, emailDto);
		
		}

	
}
