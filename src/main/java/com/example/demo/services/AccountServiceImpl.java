package com.example.demo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Account;
import com.example.demo.repositories.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;
	
	@Override
	public Account findByEmail(String email) {
		return accountRepository.findByEmail(email);
	}
	
	@Override
	public List<Account> findAllDoctor() {
		return accountRepository.findAllDoctor();
	}
	
	@Override
	public Account findById(int id) {
		return accountRepository.findById(id).get();
	}
	
	@Override
	public List<Account> findAllPatient() {
		return accountRepository.findAllPatient();
	}
	
	@Override
	public List<Account> findDoctorByFacultyId(int id) {
		return accountRepository.findDoctorByFacultyId(id);
	}

	@Override
	public boolean save(Account account) {
	    try {
	        accountRepository.save(account);  // Lưu tài khoản
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	@Override
	public boolean delete(int id) {
		try {
			accountRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Account account = findByEmail(email);
		if(account == null) {
			throw new UsernameNotFoundException("Email Không tồn tại");
		}else {
			List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();	
			roles.add(new SimpleGrantedAuthority(account.getRole().getName()));
			return new User(account.getEmail(), account.getPassword(), roles);
		}
	}


}
