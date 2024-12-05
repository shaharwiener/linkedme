package com.linkedme;

import com.linkedme.persistence.entity.Role;
import com.linkedme.persistence.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class LinkedmeApplication implements CommandLineRunner {

	@Autowired
	private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(LinkedmeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		roleRepository.save(Role.builder().name(Role.ROLE_USER).build());
		roleRepository.save(Role.builder().name(Role.ROLE_ADMIN).build());
	}
}
