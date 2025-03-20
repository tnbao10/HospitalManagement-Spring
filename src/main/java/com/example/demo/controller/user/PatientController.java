package com.example.demo.controller.user;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.Account;
import com.example.demo.entities.Appointment;
import com.example.demo.entities.Role;
import com.example.demo.helpers.FileHelper;
import com.example.demo.services.AccountService;
import com.example.demo.services.AppointmentService;
import com.example.demo.services.FacultyService;

@Controller
@RequestMapping("patient")
public class PatientController {

	@Autowired
	private FacultyService facultyService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AppointmentService appointmentService;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	
	/* Faculty List View */
	@GetMapping({ "faculty-list" })
	public String facultyList(ModelMap modelMap) {
		
		modelMap.put("faculties", facultyService.findAll());
		
		return "patient/facultyList";
	}
	
	/* Faculty List Doctor View */
	@GetMapping({ "doctor-faculty-list/{id}" })
	public String facultyListDoctor(@PathVariable("id") int id,
									ModelMap modelMap) {
		
		modelMap.put("doctors", accountService.findDoctorByFacultyId(id));
		
		return "patient/facultyListDoctor";
	}
	
	/* Get - Make Appointment  */
	@GetMapping({ "make-appointment" })
	public String makeAppointment(ModelMap modelMap) {
		
		modelMap.put("doctors", accountService.findAllDoctor());
		modelMap.put("appointment", new Appointment());
		
		return "patient/makeAppointment";
	}
	
	/* Post - Make Appointment  */
	@PostMapping({ "make-appointment" })
	public String makeAppointment(Authentication authentication,
								  @ModelAttribute("appointment") Appointment appointment,
								  @RequestParam("doctorId") int doctorId,
								  RedirectAttributes redirectAttributes) {
		String email = authentication.getName();
		Account accountPatient = accountService.findByEmail(email);
		Account accountDoctor = accountService.findById(doctorId);

		appointment.setAccountByPatientId(accountPatient);
		appointment.setAccountByDoctorId(accountDoctor);

		
		if(appointmentService.save(appointment)) {
			redirectAttributes.addFlashAttribute("msg","Make Appointment Success");
		}else {
			redirectAttributes.addFlashAttribute("msg","Make Appointment Failed");
		}
		
		
		return "redirect:/patient/make-appointment";
	}
	
	/* Get - Appointment History View  */
	@GetMapping({ "appointment-history" })
	public String appointmentHistory(Authentication authentication, ModelMap modelMap) {
		String email = authentication.getName();
		
		modelMap.put("patientId", accountService.findByEmail(email).getId());
		modelMap.put("appointments", appointmentService.findByPatientEmail(email));
		return "patient/appointmentHistory";
	}
	
	/* Medical Appointment - Search By Dates */
	@GetMapping({ "search-by-dates-medical-appointment" })
	public String searchByDates_PatientAppointment(@RequestParam("patientId") int patientId,
												  @RequestParam("startDate") String startDate,
												  @RequestParam("endDate") String endDate,
												ModelMap modelMap) {
		try {
			if(startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
				return "redirect:/patient/appointment-history";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date from = sdf.parse(startDate);
			Date to = sdf.parse(endDate);

			modelMap.put("appointments", appointmentService.searchByDates_PatientId(patientId, from, to));
			modelMap.put("patientId", patientId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "patient/appointmentHistory";	
	}
	
	/* Patient - Medical Appointment - Search By Dates desc */
	@GetMapping({ "search-by-dates-medical-appointment-desc" })
	public String searchByDates_PatientAppointment_Desc(@RequestParam("patientId") int patientId,
													ModelMap modelMap) {
		
		modelMap.put("appointments", appointmentService.SearchByDates_Desc_PatientId(patientId));
		modelMap.put("patientId", patientId);

		return "patient/appointmentHistory";	
	}
	

	/* GET - Profile */
	@GetMapping({ "profile" })
	public String profile(Authentication authentication, ModelMap modelmap) {
		
		String email = authentication.getName();
		modelmap.put("account", accountService.findByEmail(email));
		
		return "patient/profile";
	}
	
	/* POST - Profile */
	@PostMapping({ "profile" })
	public String profile(@ModelAttribute("account") Account account,
						  @RequestParam("file") MultipartFile file,
						   RedirectAttributes redirectAttributes) {
		
		Account currentAccount = accountService.findByEmail(account.getEmail());
		Role role = currentAccount.getRole();
		account.setRole(role);
		
		if(file != null && !file.isEmpty()) {
			try {
				String fileName = FileHelper.generateFileName(file.getOriginalFilename());
				File imagesFolder = new ClassPathResource("static/dist/assets/images").getFile();
				Path path = Paths.get(imagesFolder.getAbsoluteFile() + File.separator + fileName);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				account.setPhoto(fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			account.setPhoto("no-image.jpg");
		}
		
		
		if(account.getPassword().isEmpty()) {
			account.setPassword(currentAccount.getPassword());
		}else {
			account.setPassword(encoder.encode(account.getPassword()));
		}
		
		if(accountService.save(account)) {
			redirectAttributes.addFlashAttribute("msg", "Update Success");
		}else {
			redirectAttributes.addFlashAttribute("msg", "Update Failed");

		}
		
		return "redirect:/patient/profile";
	}
}
