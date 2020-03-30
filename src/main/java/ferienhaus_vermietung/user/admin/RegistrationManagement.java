package ferienhaus_vermietung.user.admin;

import ferienhaus_vermietung.user.RegistrationForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;

@Service
@Transactional
public class RegistrationManagement {

	private final HashMap<RegistrationForm, String> registrations;

	RegistrationManagement() {

		registrations = new HashMap<>();

	}

	/**
	 * Adds a new Entry to the Registration-{@link HashMap} containing a new Eventmanager-Registration.
	 *
	 * @param form must not be {@literal null}.
	 */
	public void createEventmanager(RegistrationForm form) {

		Assert.notNull(form, "Registration form must not be null!");

		registrations.put(form, "ROLE_EVENTMANAGER");
	}

	/**
	 * Adds a new Entry to the Registration-{@link HashMap} containing a new Landlord-Registration.
	 *
	 * @param form must not be {@literal null}.
	 */
	public void createLandlord(RegistrationForm form) {

		Assert.notNull(form, "Registration form must not be null!");

		registrations.put(form, "ROLE_LANDLORD");
	}

	public HashMap<RegistrationForm, String> getRegistrations(){
		return registrations;
	}

	/**
	 * Removes an Entry from the Registration-{@link HashMap} based on a username.
	 *
	 * @param username must not be {@literal null}.
	 */
	public void removeRegistration(String username){
		RegistrationForm form = null;
		for (RegistrationForm f : registrations.keySet()) {
			if(f.getEmail().equals(username)){
				form = f;
				break;
			}
		}
		registrations.remove(form);
	}

	/**
	 * Determines wether or not an Username exists in the Registration-{@link HashMap}.
	 *
	 * @param username must not be {@literal null}.
	 */
	public boolean findUsername(String username){
		for (RegistrationForm f : registrations.keySet()) {
			if(f.getEmail().equals(username)){
				return true;
			}
		}
		return false;
	}

}