package com.ms.user.services;

import org.springframework.stereotype.Service;

import com.ms.user.models.UserModel;
import com.ms.user.producers.UserProducer;
import com.ms.user.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class SaveUser {

	final UserRepository repository;
	final UserProducer producer;
	
	public SaveUser(UserRepository userRepository, UserProducer producer) {
		this.repository = userRepository;
		this.producer = producer;
	}
	
	
	@Transactional
	public UserModel saveUser(UserModel user) {
		user = repository.save(user);
		producer.publishMessageEmail(user);
		
		return user;
		
		
	}
	
}
