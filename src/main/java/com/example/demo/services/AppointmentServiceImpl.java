package com.example.demo.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Appointment;
import com.example.demo.entities.Faculty;
import com.example.demo.entities.Role;
import com.example.demo.repositories.AppointmentRepository;
import com.example.demo.repositories.RoleRepository;

@Service
public class AppointmentServiceImpl implements AppointmentService  {

	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Override
	public List<Appointment> findAll(){
		return appointmentRepository.findAll();
	}

	@Override
	public Appointment findById(int id) {
		return appointmentRepository.findById(id).get();
	}
	
	@Override
	public List<Appointment> findByPatientEmail(String email) {
		return appointmentRepository.findByPatientEmail(email);
	}
	
	@Override
	public List<Appointment> findByDoctorId(int id) {
		return appointmentRepository.findByDoctorId(id);
	}

	@Override
	public List<Appointment> findByPatientId(int id) {
		return appointmentRepository.findByPatientId(id);

	}

	@Override
	public List<Appointment> searchByDates_DoctorId(int id, Date startDate, Date endDate) {
		return appointmentRepository.searchByDates_DoctorId(id, startDate, endDate);
	}
	
	@Override
	public List<Appointment> searchByDates_PatientId(int id, Date startDate, Date endDate) {
		return appointmentRepository.searchByDates_PatientId(id, startDate, endDate);
	}
	
	@Override
	public List<Appointment> SearchByDates_Desc_DoctorId(int id) {
		return appointmentRepository.SearchByDates_Desc_DoctorId(id);
	}

	@Override
	public List<Appointment> SearchByDates_Desc_PatientId(int id) {
		return appointmentRepository.SearchByDates_Desc_PatientId(id);

	}

	@Override
	public boolean save(Appointment appointment) {
		try {
			appointmentRepository.save(appointment);
			return true;
		} catch (Exception e) {
			return false;
		}
	}




}