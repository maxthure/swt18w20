package ferienhaus_vermietung.user.admin;

import ferienhaus_vermietung.user.RegistrationForm;
import ferienhaus_vermietung.user.eventmanager.EventmanagerManagement;
import ferienhaus_vermietung.user.landlord.LandlordManagement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@SuppressWarnings("unused")
@Controller
class AdminController {

	private final EventmanagerManagement eventmanagerManagement;
	private final LandlordManagement landlordManagement;
	private final RegistrationManagement registrationManagement;

	/**
	 * @param eventmanagerManagement must not be {@literal null}.
	 * @param landlordManagement must not be {@literal null}.
	 * @param registrationManagement must not be {@literal null}.
	 */
	AdminController(EventmanagerManagement eventmanagerManagement, LandlordManagement landlordManagement,
					RegistrationManagement registrationManagement) {

		Assert.notNull(eventmanagerManagement, "EventmanagerManagement must not be null!");
		Assert.notNull(landlordManagement, "LandlordManagement must not be null!");
		Assert.notNull(registrationManagement, "RegistrationManagement must not be null!");

		this.eventmanagerManagement = eventmanagerManagement;
		this.landlordManagement = landlordManagement;
		this.registrationManagement = registrationManagement;
	}

	/**
	 * This method takes the {@link RegistrationForm} created by the registration of a landlord or eventmanager and adds
	 * it to the list of registrations that still need approval by the admin.
	 *
	 * @param form must not be {@literal null}.
	 * @return a redirect to "/registrations"
	 */
	@PostMapping("/registrations")
	String registerNew(RegistrationForm form) {

		registrationManagement.removeRegistration(form.getEmail());

		if(form.getCompany() != null){
			eventmanagerManagement.createEventmanager(form);
		} else{
			landlordManagement.createLandlord(form);
		}

		return "redirect:/registrations";
	}

	/**
	 * This method removes a {@link RegistrationForm} and deletes it from the list of registrations that still need
	 * approval by the admin.
	 *
	 * @param form must not be {@literal null}.
	 * @return a redirect to "/registrations"
	 */
	@PostMapping("/registrations/delete")
	String delete(RegistrationForm form) {

		registrationManagement.removeRegistration(form.getEmail());

		return "redirect:/registrations";
	}

	/**
	 * This method generates the view for the registrations that need approval.
	 *
	 * @param model must not be {@literal null}.
	 * @return HTML template
	 */
	@GetMapping("/registrations")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	String user(Model model) {

		model.addAttribute("usersList", registrationManagement.getRegistrations());

		return "registrations";
	}
}