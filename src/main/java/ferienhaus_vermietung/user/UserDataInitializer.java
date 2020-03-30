package ferienhaus_vermietung.user;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Order(10)
class UserDataInitializer implements DataInitializer {

	private final UserAccountManager userAccountManager;

	/**
	 * @param userAccountManager must not be {@literal null}.
	 */
	UserDataInitializer(UserAccountManager userAccountManager) {

		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");

		this.userAccountManager = userAccountManager;
	}

	@Override
	public void initialize() {

		if (userAccountManager.findByUsername("Admin").isPresent()) {
			return;
		}

		UserAccount adminAccount = userAccountManager.create("Admin", "admin", Role.of("ROLE_ADMIN"));
		adminAccount.setFirstname("Heinz");
		adminAccount.setLastname("Petersen");
		adminAccount.setEmail("hein.petersen@example.com");
		userAccountManager.save(adminAccount);

	}
}

