package com.example.demo.services;

import java.util.List;

import com.example.demo.entities.Faculty;

public interface FacultyService {
	
	public List<Faculty> findAll();
	
	public Faculty findById(int id);
	
	public boolean save(Faculty faculty);
	
	public boolean delete(int id);

}
