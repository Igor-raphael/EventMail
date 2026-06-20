package com.ms.email.services;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ms.email.enums.StatusEmail;
import com.ms.email.models.EmailModel;
import com.ms.email.repositories.EmailRepository;

import jakarta.transaction.Transactional;

@Service
public class EmailService {
	
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	
	final EmailRepository repository;
	
	final JavaMailSender emailSender;
	
	public EmailService(EmailRepository repository, JavaMailSender emailSender) {
		this.repository = repository;
		this.emailSender = emailSender;
	}
	
	@Value(value = "${spring.mail.username}")
	private String emailFrom;
	
	@Transactional
	public EmailModel sendEmail(EmailModel model) {
		
		log.info("Iniciando envio de e-mail para {}", model.getEmailTo());
		
		try {
		model.setSendDateEmail(LocalDateTime.now());
		model.setEmailFrom(emailFrom);
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(model.getEmailTo());
		message.setSubject(model.getSubject());
		message.setText(model.getText());
		emailSender.send(message);
		
		model.setStatusEmail(StatusEmail.SENT);
		
		log.info(
                "E-mail enviado com sucesso para {}",
                model.getEmailTo());
			
		} catch (Exception e) {
			
			log.error(
					"Erro ao enviar e-mail para {}",
	                model.getEmailTo(), e);
			
			model.setStatusEmail(StatusEmail.ERROR);
		
		}finally {
			return repository.save(model);
		}
		
	}

}
