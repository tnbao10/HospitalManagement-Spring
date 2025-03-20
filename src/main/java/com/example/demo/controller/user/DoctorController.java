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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.Account;
import com.example.demo.entities.Role;
import com.example.demo.helpers.FileHelper;
import com.example.demo.services.AccountService;
import com.example.demo.services.AppointmentService;

@Controller
@RequestMapping("doctor")
public class DoctorController {

	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AppointmentService appointmentService;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	/* GET - Medical Appointment View*/
	@GetMapping({ "medical-appointment" })
	public String medicalAppointment(Authentication authentication, ModelMap modelMap) {
		String email = authentication.getName();
		Account account = accountService.findByEmail(email);
		
		modelMap.put("doctorId", account.getId());
		modelMap.put("appointments", appointmentService.findByDoctorId(account.getId()));
		
		return "doctor/medicalAppointment";
	}
	
	/* Doctor - Medical Appointment - Search By Dates */
	@GetMapping({ "search-by-dates-medical-appointment" })
	public String searchByDates_DoctorAppointment(@RequestParam("doctorId") int doctorId,
												  @RequestParam("startDate") String startDate,
												  @RequestParam("endDate") String endDate,
												ModelMap modelMap) {
		try {
			if(startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
				return "redirect:/doctor/medical-appointment";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date from = sdf.parse(startDate);
			Date to = sdf.parse(endDate);

			modelMap.put("appointments", appointmentService.searchByDates_DoctorId(doctorId, from, to));
			modelMap.put("doctorId", doctorId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "doctor/medicalAppointment";	
	}
	
	/* Doctor - Medical Appointment - Search By Dates desc */
	@GetMapping({ "search-by-dates-medical-appointment-desc" })
	public String searchByDates_DoctorAppointment_Desc(@RequestParam("doctorId") int doctorId,
													ModelMap modelMap) {
		
		modelMap.put("appointments", appointmentService.SearchByDates_Desc_DoctorId(doctorId));
		modelMap.put("doctorId", doctorId);

		return "doctor/medicalAppointment";	
	}
	

	
	
	/* GET - Profile */
	@GetMapping({ "profile" })
	public String profile(Authentication authentication, ModelMap modelmap) {
		
		String email = authentication.getName();
		modelmap.put("account", accountService.findByEmail(email));
		
		return "doctor/profile";
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
		
		return "redirect:/doctor/profile";
	}
	
}
