package ch.hearc.holygram.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import ch.hearc.holygram.forms.UserForm;
import ch.hearc.holygram.models.Canton;
import ch.hearc.holygram.models.Customer;
import ch.hearc.holygram.models.Exorcist;
import ch.hearc.holygram.models.Role;
import ch.hearc.holygram.models.User;
import ch.hearc.holygram.repositories.CantonRepository;
import ch.hearc.holygram.repositories.CustomerRepository;
import ch.hearc.holygram.repositories.ExorcistRepository;
import ch.hearc.holygram.repositories.RoleRepository;
import ch.hearc.holygram.security.ExorcistValidator;
import ch.hearc.holygram.security.UserValidator;
import ch.hearc.holygram.services.SecurityServiceImpl;
import ch.hearc.holygram.services.UserService;

/**
 * Controller for the user routes, (registration/login)
 */
@Controller
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private SecurityServiceImpl securityService;

	@Autowired
	private ExorcistRepository exorcistRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CantonRepository cantonRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private ExorcistValidator exorcistValidator;

	@GetMapping("/signup")
	public String signup(Model model) {

		Iterable<Canton> cantons = cantonRepository.findAll();
		model.addAttribute("cantons", cantons);

		model.addAttribute("userForm", new UserForm());

		return "signup";
	}

	@PostMapping("/signup")
	public String registration(Model model, UserForm userForm, HttpServletRequest request,
			BindingResult bindingResult) {
		try {

			// Valid the futur user
			userValidator.validate(userForm, bindingResult);

			// Interrupt the sign up
			if (bindingResult.hasErrors()) {
				Iterable<Canton> cantons = cantonRepository.findAll();
				model.addAttribute("cantons", cantons);

				return "signup";
			}

			// Create the user
			User user = new User(userForm.getUsername(), userForm.getPassword(), userForm.getPasswordConfirm(),
					userForm.getEmail());

			String typeAccount = request.getParameter("customRadio");

			if (typeAccount.equals("customer")) {

				// Set role
				Role role = roleRepository.findByName("ROLE_CUSTOMER");
				user.setRole(role);
				user = userService.save(user);

				// Create the customer
				Customer customer = new Customer(user);
				customer = customerRepository.save(customer);

				// Set the liaison in the current user object
				// Hibernate hasn't already create the link user's side
				user.setCustomer(customer);
			} else {

				// Valid the futur exorcist
				exorcistValidator.validate(userForm, bindingResult);

				// Interrupt the sign up
				if (bindingResult.hasErrors()) {
					Iterable<Canton> cantons = cantonRepository.findAll();
					model.addAttribute("cantons", cantons);

					return "signup";
				}
				// Set role
				Role role = roleRepository.findByName("ROLE_EXORCIST");
				user.setRole(role);
				userService.save(user);

				// Create the exorcist
				Canton canton = cantonRepository.findByAcronym(request.getParameter("canton"));

				Exorcist exorcist = new Exorcist(user, request.getParameter("description"),
						request.getParameter("phoneNumber"), canton);

				exorcistRepository.save(exorcist);

				// Set the liaison in the current user object
				// Hibernate hasn't already create the link user's side
				user.setExorcist(exorcist);
			}

			// Authentication
			securityService.autoLogin(user.getUsername(), user.getPasswordConfirm());

		} catch (Exception e) {
		}

		return "redirect:/";
	}

	@GetMapping("/formExorcist")
	public String formExorcist(Model model) {
		Iterable<Canton> cantons = cantonRepository.findAll();
		model.addAttribute("cantons", cantons);

		return "fragments/signup :: exorcist";
	}
}
