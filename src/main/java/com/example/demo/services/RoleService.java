package com.example.demo.services;

import java.util.List;

import com.example.demo.entities.Role;

public interface RoleService {
	
	public List<Role> findAll();
	
	public Role findById(int id);


}
