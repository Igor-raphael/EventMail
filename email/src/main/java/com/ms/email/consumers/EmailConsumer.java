package com.ms.email.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.ms.email.dtos.EmailRecordDto;
import com.ms.email.models.EmailModel;
import com.ms.email.services.EmailService;

@Component
public class EmailConsumer {
	
	final EmailService service;
	
	public EmailConsumer(EmailService service) {
		this.service = service;
	}
	
	@RabbitListener(queues = "${broker.queue.email.name}")
	public void listenEmailQueue(@Payload EmailRecordDto emailRecord) {
		
		var emailModel = new EmailModel();
		BeanUtils.copyProperties(emailRecord, emailModel);
		service.sendEmail(emailModel);
		
		//Test-Terminal
		//System.out.println(emailRecord.emailTo());
		//System.out.println(emailRecord.subject());
		//System.out.println(emailRecord.text());
	}

}
