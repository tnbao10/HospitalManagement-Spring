package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Account;
import com.example.demo.entities.Faculty;
import com.example.demo.entities.Role;
import com.example.demo.repositories.FacultyRepository;
import com.example.demo.repositories.RoleRepository;

@Service
public class FacultyServiceImpl implements FacultyService  {

	@Autowired
	private FacultyRepository facultyRepository;
	
	@Override
	public List<Faculty> findAll(){
		return facultyRepository.findAll();
	}
	
	@Override
	public Faculty findById(int id) {
		return facultyRepository.findById(id).get();
	}

	@Override
	public boolean save(Faculty faculty) {
		try {
			facultyRepository.save(faculty);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public boolean delete(int id) {
		try {
			facultyRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			return false;
		}
	}






}