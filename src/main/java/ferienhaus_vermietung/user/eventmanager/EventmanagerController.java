package ferienhaus_vermietung.user.eventmanager;

import ferienhaus_vermietung.user.RegistrationForm;
import ferienhaus_vermietung.user.admin.RegistrationManagement;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@SuppressWarnings("SameReturnValue")
@Controller
class EventmanagerController {

	private final EventmanagerManagement eventmanagerManagement;
	private final RegistrationManagement registrationManagement;
	private final UserAccountManager userAccountManager;

	/**
	 * @param eventmanagerManagement must not be {@literal null}.
	 * @param registrationManagement must not be {@literal null}.
	 * @param userAccountManager must not be {@literal null}.
	 */
	EventmanagerController(EventmanagerManagement eventmanagerManagement, RegistrationManagement registrationManagement,
						   UserAccountManager userAccountManager) {

		Assert.notNull(eventmanagerManagement, "EventmanagerManagement must not be null!");
		Assert.notNull(registrationManagement, "RegistrationManagement must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");

		this.eventmanagerManagement = eventmanagerManagement;
		this.registrationManagement = registrationManagement;
		this.userAccountManager = userAccountManager;
	}

	/**
	 * This method creates a registration of an {@link Eventmanager} with the {@link RegistrationForm}, created by the
	 * registration of an {@link Eventmanager}, that still needs approval by the admin.
	 *
	 * @param form must not be {@literal null}.
	 * @param result  must not be {@literal null}.
	 * @param modell  must not be {@literal null}.
	 * @return either a HTML template if there is an error with the registration or a redirect to "/"
	 */
	@PostMapping("/eventmanager/register")
	String registerNew(@Valid RegistrationForm form, Errors result, Model modell) {

		if(userAccountManager.findByUsername(form.getEmail()).isPresent()
				|| registrationManagement.findUsername(form.getEmail())){
			modell.addAttribute("invalid", true);
			modell.addAttribute("form", form);
			modell.addAttribute("this", "eventmanager");
			return "register";
		}

		if (result.hasErrors()) {
			return "register";
		}

		registrationManagement.createEventmanager(form);

		return "redirect:/";
	}

	/**
	 * @param model must not be {@literal null}.
	 * @param form must not be {@literal null}.
	 * @return HTML template
	 */
	@GetMapping("/eventmanager/register")
	String register(Model model, RegistrationForm form) {
		model.addAttribute("form", form);
		model.addAttribute("this", "eventmanager");
		return "register";
	}

	/**
	 * This method deletes a {@link Eventmanager} by its username.
	 *
	 * @param eventmanager_username must not be {@literal null}.
	 * @return a redirect to "/tenants"
	 */
	@PostMapping("/eventmanager/delete")
	String delete(String eventmanager_username){

		eventmanagerManagement.deleteEventmanager(eventmanager_username);

		return  "redirect:/eventmanager";
	}

	/**
	 * @param model must not be {@literal null}.
	 * @return HTML template
	 */
	@GetMapping("/eventmanager")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	String eventmanager(Model model) {

		model.addAttribute("userList", eventmanagerManagement.findAll());
		model.addAttribute("instance", "Eventmanager");

		return "user";
	}
}