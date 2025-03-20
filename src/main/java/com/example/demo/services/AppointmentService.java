package com.example.demo.services;

import java.util.Date;
import java.util.List;

import com.example.demo.entities.Appointment;

public interface AppointmentService {
	
	public List<Appointment> findAll();
	
	public Appointment findById(int id);
	
	public List<Appointment> findByPatientEmail(String email);

	public List<Appointment> findByDoctorId(int id);

	public List<Appointment> findByPatientId(int id);
	
	public List<Appointment> searchByDates_DoctorId(int id, Date startDate, Date endDate);
	
	public List<Appointment> searchByDates_PatientId(int id, Date startDate, Date endDate);

	public List<Appointment> SearchByDates_Desc_DoctorId(int id);
	
	public List<Appointment> SearchByDates_Desc_PatientId(int id);


	public boolean save(Appointment appointment);



}
