package com.ms.user.services;

import org.springframework.stereotype.Service;

import com.ms.user.models.UserModel;
import com.ms.user.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class SaveUser {

	final UserRepository repository;
	
	public SaveUser(UserRepository userRepository) {
		this.repository = userRepository;
	}
	
	
	@Transactional
	public UserModel saveUser(UserModel user) {
		return repository.save(user);
		
		
	}
	
}
