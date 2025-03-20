package com.example.demo.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Account;
import com.example.demo.entities.Appointment;
import com.example.demo.entities.Faculty;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer>{
	
	@Query("from Appointment where accountByPatientId.email = :email")
	public List<Appointment> findByPatientEmail(@Param("email") String email);
	
	@Query("from Appointment where accountByDoctorId.id = :id")
	public List<Appointment> findByDoctorId(@Param("id") int id);
	
	@Query("from Appointment where accountByPatientId.id = :id")
	public List<Appointment> findByPatientId(@Param("id") int id);
	
	@Query("from Appointment where accountByDoctorId.id = :id and date >= :startDate and date <= :endDate")
	public List<Appointment> searchByDates_DoctorId(@Param("id") int id,
												  @Param("startDate") Date startDate,
												  @Param("endDate") Date endDate);
	
	@Query("from Appointment where accountByPatientId.id = :id and date >= :startDate and date <= :endDate")
	public List<Appointment> searchByDates_PatientId(@Param("id") int id,
												  @Param("startDate") Date startDate,
												  @Param("endDate") Date endDate);
	
	@Query("from Appointment where accountByDoctorId.id = :doctorId order by date desc")
	public List<Appointment> SearchByDates_Desc_DoctorId(@Param("doctorId") int id);
	
	@Query("from Appointment where accountByPatientId.id = :patientId order by date desc")
	public List<Appointment> SearchByDates_Desc_PatientId(@Param("patientId") int id);



} 
