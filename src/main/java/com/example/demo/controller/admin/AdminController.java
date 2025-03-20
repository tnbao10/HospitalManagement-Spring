package com.example.demo.controller.admin;

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
import com.example.demo.entities.Faculty;
import com.example.demo.entities.Role;
import com.example.demo.helpers.FileHelper;
import com.example.demo.services.AccountService;
import com.example.demo.services.AppointmentService;
import com.example.demo.services.FacultyService;
import com.example.demo.services.RoleService;

@Controller
@RequestMapping("admin")
public class AdminController {
	
	@Autowired
	private FacultyService facultyService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AppointmentService appointmentService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	/* Faculty Management View */
	@GetMapping({ "faculty-management" })
	public String facultyManagement(ModelMap modelMap) {
		
		modelMap.put("faculty", new Faculty());
		modelMap.put("faculties", facultyService.findAll());
		return "admin/facultyManagement";
	}
	
	/* Post - Add Faculty*/
	@PostMapping({ "add-faculty" })
	public String addFaculty(@ModelAttribute("faculty") Faculty faculty,
							  RedirectAttributes redirectAttributes) {
		
		if(facultyService.save(faculty)) {
			redirectAttributes.addFlashAttribute("msg", "Add Success");
		}else {
			redirectAttributes.addFlashAttribute("msg", "Add Failed");
		}
		return "redirect:/admin/faculty-management";

	}
	
	/* Get - Update Faculty*/
	@GetMapping({ "update-faculty/{id}" })
	public String updateFaculty(@PathVariable("id") int id, ModelMap modelMap) {
		
		modelMap.put("faculty", facultyService.findById(id));
		return "admin/facultyUpdate";
	}
	
	/* Post - Update Faculty*/
	@PostMapping({ "update-faculty" })
	public String updateFaculty(@ModelAttribute("faculty") Faculty faculty,
								 RedirectAttributes redirectAttributes) {
		
		if(facultyService.save(faculty)) {
			redirectAttributes.addFlashAttribute("msg", "Update Success");	
		}else {
			redirectAttributes.addFlashAttribute("msg", "Update Failed");	

		}
		return "redirect:/admin/update-faculty/" + faculty.getId();
	}
	
	/* Delete Faculty*/
	@GetMapping({ "delete-faculty/{id}" })
	public String deleteFaculty(@PathVariable("id") int id,
								 RedirectAttributes redirectAttributes) {
		
		if(facultyService.delete(id)) {
			redirectAttributes.addFlashAttribute("msg", "Delete Success");	
		}else {
			redirectAttributes.addFlashAttribute("msg", "Delete Failed");	

		}
		return "redirect:/admin/faculty-management";
	}
	
	
	/* Doctor Management View*/
	@GetMapping({ "doctor-management" })
	public String doctorManagement(ModelMap modelMap) {
		
		modelMap.put("account", new Account());
		modelMap.put("doctors", accountService.findAllDoctor());
		modelMap.put("faculties", facultyService.findAll());
		return "admin/doctorManagement";
	}
	
	/* Post - Add Doctor */
	@PostMapping({ "add-doctor" })
	public String addDoctor(@ModelAttribute("account") Account account,
							 RedirectAttributes redirectAttributes) {
		
		account.setPassword(encoder.encode(account.getPassword()));
		account.setRole(roleService.findById(2));
		
		if(accountService.save(account)) {
			redirectAttributes.addFlashAttribute("msg", "Add Success");	
		}else {
			redirectAttributes.addFlashAttribute("msg", "Add Failed");	
		}
			
		return "redirect:/admin/doctor-management";
		
	}
	
	/* Get - Update Doctor*/
	@GetMapping({ "update-doctor/{id}" })
	public String updateDoctor(@PathVariable("id") int id,
								ModelMap modelMap) {
		
		modelMap.put("account", accountService.findById(id));
		modelMap.put("faculties", facultyService.findAll());
		
		return "admin/doctorUpdate";
	}
	
	/* Post - Update Doctor*/
	@PostMapping({ "update-doctor" })
	public String updateDoctor(@ModelAttribute("account") Account account,
								RedirectAttributes redirectAttributes) {
		
		Account currentAccount = accountService.findByEmail(account.getEmail());
		account.setRole(currentAccount.getRole());
		
		if (account.getPassword() == null || account.getPassword().isEmpty()) {
		    account.setPassword(currentAccount.getPassword());
		} else {
		    account.setPassword(encoder.encode(account.getPassword()));
		}

		if(accountService.save(account)) {
			redirectAttributes.addFlashAttribute("msg", "Update Success");
		}else {
		    redirectAttributes.addFlashAttribute("msg", "Update Failed");
		}
		
		return "redirect:/admin/update-doctor/" + account.getId();
	}
	
	
	/* Delete Doctor*/
	@GetMapping({ "delete-doctor/{id}" })
	public String updateDoctor(@PathVariable("id") int id,
								RedirectAttributes redirectAttributes) {
		
		if(accountService.delete(id)) {
			redirectAttributes.addFlashAttribute("msg", "Delete Success");
		}else {
			redirectAttributes.addFlashAttribute("msg", "Delete Failed");
		}
		return "redirect:/admin/doctor-management";	
	}
	
	/* Doctor - Medical Appointment*/
	@GetMapping({ "doctor-medical-appointment/{id}" })
	public String doctorMedicalAppoint(@PathVariable("id") int id,
								RedirectAttributes redirectAttributes,
								ModelMap modelMap) {
		
		modelMap.put("doctorId", id);
		modelMap.put("appointments", appointmentService.findByDoctorId(id));
		return "admin/doctorMedicalAppointment";	
	}
	
	/* Doctor - Medical Appointment - Search By Dates */
	@GetMapping({ "search-by-dates-doctor-appointment" })
	public String searchByDates_DoctorAppointment(@RequestParam("doctorId") int doctorId,
												  @RequestParam("startDate") String startDate,
												  @RequestParam("endDate") String endDate,
												ModelMap modelMap) {
		try {
			if(startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
				return "redirect:/admin/doctor-medical-appointment/" + doctorId;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date from = sdf.parse(startDate);
			Date to = sdf.parse(endDate);

			modelMap.put("appointments", appointmentService.searchByDates_DoctorId(doctorId, from, to));
			modelMap.put("doctorId", doctorId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "admin/doctorMedicalAppointment";	
	}
	
	/* Doctor - Medical Appointment - Search By Dates desc */
	@GetMapping({ "search-by-dates-doctor-appointment-desc" })
	public String searchByDates_DoctorAppointment_Desc(@RequestParam("doctorId") int doctorId,
													ModelMap modelMap) {
		
		modelMap.put("appointments", appointmentService.SearchByDates_Desc_DoctorId(doctorId));
		modelMap.put("doctorId", doctorId);

		return "admin/doctorMedicalAppointment";	
	}
	
	/* Patient Management View */
	@GetMapping({ "patient-management" })
	public String patientManagement(ModelMap modelMap) {
		
		modelMap.put("patients", accountService.findAllPatient());
		return "admin/patientManagement";
	}
	
	/* Patient - Medical Appointment*/
	@GetMapping({ "patient-medical-appointment/{id}" })
	public String patientMedicalAppoint(@PathVariable("id") int id,
								RedirectAttributes redirectAttributes,
								ModelMap modelMap) {
		
		modelMap.put("patientId", id);
		modelMap.put("appointments", appointmentService.findByPatientId(id));
		return "admin/patientMedicalAppointment";	
	}
	
	/* Patient - Medical Appointment - Search By Dates */
	@GetMapping({ "search-by-dates-patient-appointment" })
	public String searchByDates_PatientAppointment(@RequestParam("patientId") int patientId,
												  @RequestParam("startDate") String startDate,
												  @RequestParam("endDate") String endDate,
												ModelMap modelMap) {
		try {
			if(startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
				return "redirect:/admin/patient-medical-appointment/" + patientId;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date from = sdf.parse(startDate);
			Date to = sdf.parse(endDate);

			modelMap.put("appointments", appointmentService.searchByDates_PatientId(patientId, from, to));
			modelMap.put("patientId", patientId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "admin/patientMedicalAppointment";	
	}
	
	/* Patient - Medical Appointment - Search By Dates desc */
	@GetMapping({ "search-by-dates-patient-appointment-desc" })
	public String searchByDates_PatientAppointment_Desc(@RequestParam("patientId") int patientId,
													ModelMap modelMap) {
		
		modelMap.put("appointments", appointmentService.SearchByDates_Desc_PatientId(patientId));
		modelMap.put("patientId", patientId);

		return "admin/patientMedicalAppointment";	
	}
	
	
	/* GET - Profile */
	@GetMapping({ "profile" })
	public String profile(Authentication authentication, ModelMap modelmap) {
		
		String email = authentication.getName();
		modelmap.put("account", accountService.findByEmail(email));
		
		return "admin/profile";
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
		
		return "redirect:/admin/profile";
	}
}
