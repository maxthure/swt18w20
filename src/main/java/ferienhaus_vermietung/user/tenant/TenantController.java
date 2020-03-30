package ferienhaus_vermietung.user.tenant;

import ferienhaus_vermietung.user.RegistrationForm;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@SuppressWarnings("SameReturnValue")
@Controller
class TenantController {

	private final TenantManagement tenantManagement;
	private final UserAccountManager userAccountManager;

	/**
	 * @param tenantManagement must not be {@literal null}.
	 * @param userAccountManager must not be {@literal null}.
	 */
	TenantController(TenantManagement tenantManagement, UserAccountManager userAccountManager) {

		Assert.notNull(tenantManagement, "TenantManagement must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");

		this.tenantManagement = tenantManagement;
		this.userAccountManager = userAccountManager;
	}

	/**
	 * This method creates a new {@link Tenant} with the {@link RegistrationForm} created by the registration of a {@link Tenant}.
	 *
	 * @param form must not be {@literal null}.
	 * @param result must not be {@literal null}.
	 * @param model must not be {@literal null}.
	 * @return either a HTML template if there is an error with the registration or a redirect to "/"
	 */
	@PostMapping("/tenant/register")
	String registerNew(@Valid @ModelAttribute("form") RegistrationForm form, Errors result, Model model) {


		if(userAccountManager.findByUsername(form.getEmail()).isPresent()){
			model.addAttribute("invalid", true);
			model.addAttribute("form", form);
			model.addAttribute("this", "tenant");
			return "register";
		}

		if (result.hasErrors()) {
			return "register";
		}

		tenantManagement.createTenant(form);

		return "redirect:/";
	}

	/**
	 * @param model must not be {@literal null}.
	 * @param form must not be {@literal null}.
	 * @return HTML template
	 */
	@GetMapping("/tenant/register")
	String register(Model model, RegistrationForm form) {
		model.addAttribute("form", form);
		model.addAttribute("this", "tenant");
		return "register";
	}

	/**
	 * This method deletes a {@link Tenant} by its username.
	 *
	 * @param tenant_username must not be {@literal null}.
	 * @return a redirect to "/tenants"
	 */
	@PostMapping("/tenant/delete")
	String delete(String tenant_username){

		tenantManagement.deleteTenant(tenant_username);

		return  "redirect:/tenants";
	}

	/**
	 * @param model must not be {@literal null}.
	 * @return HTML template
	 */
	@GetMapping("/tenants")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	String tenants(Model model) {

		model.addAttribute("userList", tenantManagement.findAll());
		model.addAttribute("instance", "Tenants");

		return "user";
	}
}