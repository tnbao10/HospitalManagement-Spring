package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer>{

	@Query("from Account where email = :email")
	public Account findByEmail(@Param("email") String email);
	
	@Query("from Account a where a.role.id = 2")
	public List<Account> findAllDoctor();
	
	@Query("from Account a where a.role.id = 3")
	public List<Account> findAllPatient();
	
	@Query("from Account where faculty.id = :id")
	public List<Account> findDoctorByFacultyId(@Param("id") int id);
}
