package com.example.demo.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.demo.entities.Account;

public interface AccountService extends UserDetailsService {

	public Account findByEmail(String ema1il);
	
	public Account findById(int id);
	
	public List<Account> findAllDoctor();
	
	public List<Account> findAllPatient();
	
	public boolean save(Account account);
	
	public boolean delete(int id);
	
	public List<Account> findDoctorByFacultyId(int id);

}
