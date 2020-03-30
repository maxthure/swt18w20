package ferienhaus_vermietung.user.landlord;

import ferienhaus_vermietung.holidayHome.HouseManagement;
import ferienhaus_vermietung.user.RegistrationForm;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@Transactional
public class LandlordManagement {

	private final LandlordRepository landlords;
	private final UserAccountManager userAccounts;
	private final HouseManagement houseManagement;

	/**
	 * @param landlords must not be {@literal null}.
	 * @param userAccounts must not be {@literal null}.
	 * @param houseManagement must not be {@literal null}.
	 */
	LandlordManagement(LandlordRepository landlords, UserAccountManager userAccounts, HouseManagement houseManagement) {

		Assert.notNull(landlords, "LandlordRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManager must not be null!");
		Assert.notNull(houseManagement, "HouseManagement must not be null!");

		this.landlords = landlords;
		this.userAccounts = userAccounts;
		this.houseManagement = houseManagement;
	}

	/**
	 * Creates a new {@link Landlord} using the information given in the {@link RegistrationForm}.
	 *
	 * @param form must not be {@literal null}.
	 * @return {@link Landlord}
	 */
	public Landlord createLandlord(RegistrationForm form) {

		Assert.notNull(form, "Registration form must not be null!");

		UserAccount userAccount = userAccounts.create(form.getEmail(), form.getPassword() , Role.of("ROLE_LANDLORD"));
		userAccount.setFirstname(form.getFirstname());
		userAccount.setLastname(form.getLastname());
		userAccount.setEmail(form.getEmail());
		userAccounts.save(userAccount);

		String[] adress = {form.getStreet(), form.getPostcode(), form.getCity(), form.getCountry()};

		return landlords.save(new Landlord(userAccount,adress));
	}

	/**
	 * Deletes a {@link Landlord} using a username.
	 *
	 * @param landlord_username must not be {@literal null}.
	 */
	public void deleteLandlord(String landlord_username){

		for (Landlord landlord : landlords.findAll()) {
			if (landlord.getUserAccount().getUsername().equals(landlord_username)){
				houseManagement.deleteAllHousesOfLandlord(landlord);
				userAccounts.disable(userAccounts.findByUsername(landlord_username)
						.orElseThrow(() -> new NullPointerException("UserAccount not present!")).getId());
				landlords.delete(landlord);
			}
		}
	}

	/**
	 * Returns all {@link Landlord}s currently available in the system.
	 *
	 * @return {@link Streamable<Landlord>}
	 */
	public Streamable<Landlord> findAll() {
		return Streamable.of(landlords.findAll());
	}
}