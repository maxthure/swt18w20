package ferienhaus_vermietung.user.landlord;

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
class LandlordController {

	private final LandlordManagement landlordManagement;
	private final RegistrationManagement registrationManagement;
	private final UserAccountManager userAccountManager;

	/**
	 * @param landlordManagement must not be {@literal null}.
	 * @param registrationManagement must not be {@literal null}.
	 * @param userAccountManager must not be {@literal null}.
	 */
	LandlordController(LandlordManagement landlordManagement, RegistrationManagement registrationManagement,
					   UserAccountManager userAccountManager) {

		Assert.notNull(landlordManagement, "LandlordManagement must not be null!");
		Assert.notNull(registrationManagement, "RegistrationManagement must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");

		this.landlordManagement = landlordManagement;
		this.registrationManagement = registrationManagement;
		this.userAccountManager = userAccountManager;
	}

	/**
	 * This method creates a registration of an {@link Landlord} with the {@link RegistrationForm}, created by the
	 * registration of an {@link Landlord}, that still needs approval by the admin.
	 *
	 * @param form must not be {@literal null}.
	 * @param result  must not be {@literal null}.
	 * @param model  must not be {@literal null}.
	 * @return either a HTML template if there is an error with the registration or a redirect to "/"
	 */
	@PostMapping("/landlord/register")
	String registerNew(@Valid RegistrationForm form, Errors result, Model model) {

		if(userAccountManager.findByUsername(form.getEmail()).isPresent()
				|| registrationManagement.findUsername(form.getEmail())){
			model.addAttribute("invalid", true);
			model.addAttribute("form", form);
			model.addAttribute("this", "landlord");
			return "register";
		}

		if (result.hasErrors()) {
			return "register";
		}

		registrationManagement.createLandlord(form);

		return "redirect:/";
	}

	/**
	 * @param model must not be {@literal null}.
	 * @param form must not be {@literal null}.
	 * @return HTML template
	 */
	@GetMapping("/landlord/register")
	String register(Model model, RegistrationForm form) {
		model.addAttribute("form", form);
		model.addAttribute("this", "landlord");
		return "register";
	}

	/**
	 * This method deletes a {@link Landlord} by its username.
	 *
	 * @param landlord_username must not be {@literal null}.
	 * @return a redirect to "/tenants"
	 */
	@PostMapping("/landlord/delete")
	String delete(String landlord_username){

		landlordManagement.deleteLandlord(landlord_username);

		return  "redirect:/landlords";
	}

	/**
	 * @param model must not be {@literal null}.
	 * @return HTML template
	 */
	@GetMapping("/landlords")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	String landlords(Model model) {

		model.addAttribute("userList", landlordManagement.findAll());
		model.addAttribute("instance", "Landlords");

		return "user";
	}
}