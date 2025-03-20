package com.example.demo.controller.account;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entities.Account;
import com.example.demo.entities.Role;
import com.example.demo.services.AccountService;
import com.example.demo.services.RoleService;

@Controller
@RequestMapping("account")
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	/* Get - login*/
	@GetMapping({ "", "/", "login" })
	public String login(@RequestParam(value = "error", required = false) String error,
						ModelMap modelMap) {
		if(error != null) {
			modelMap.put("msg", "Failed");
		}
		return "account/login";
	}
	
	
	/* Get - Register*/
	@GetMapping({ "register" })
	public String register(ModelMap modelMap) {
		Account account = new Account();
		account.setDob(new Date());
		
		modelMap.put("account", account);
		
		return "account/register";
		
	}
	
	/* Post - Register*/
	@PostMapping({ "register" })
	public String register(@ModelAttribute("account") Account account,
							RedirectAttributes redirectAttributes) {
		
		account.setPassword(encoder.encode(account.getPassword()));
		Role rolePatient = roleService.findById(3);
		account.setRole(rolePatient);
		
		if(accountService.save(account)) {
			redirectAttributes.addFlashAttribute("msg", "Register Success");
			return "redirect:/account/login";
		}else {
			redirectAttributes.addFlashAttribute("msg", "Register Success");
			return "redirect:/account/register";
		}
		
	}
	
	
}
