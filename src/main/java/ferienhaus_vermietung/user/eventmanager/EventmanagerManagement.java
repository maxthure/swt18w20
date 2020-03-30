package ferienhaus_vermietung.user.eventmanager;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.event.EventCatalog;
import ferienhaus_vermietung.event.EventManagement;
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
public class EventmanagerManagement {

	private final EventmanagerRepository eventmanagers;
	private final UserAccountManager userAccounts;
	private final EventManagement eventManagement;
	private final EventCatalog eventCatalog;

	/**
	 * @param eventmanagers must not be {@literal null}.
	 * @param userAccounts must not be {@literal null}.
	 * @param eventManagement must not be {@literal null}.
	 * @param eventCatalog must not be {@literal null}.
	 */
	EventmanagerManagement(EventmanagerRepository eventmanagers, UserAccountManager userAccounts,
						   EventManagement eventManagement, EventCatalog eventCatalog) {

		Assert.notNull(eventmanagers, "EventmanagerRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManager must not be null!");
		Assert.notNull(eventManagement, "EventManagement must not be null!");
		Assert.notNull(eventCatalog, "EventCatalog must not be null!");

		this.eventmanagers = eventmanagers;
		this.userAccounts = userAccounts;
		this.eventManagement = eventManagement;
		this.eventCatalog = eventCatalog;
	}

	/**
	 * Creates a new {@link Eventmanager} using the information given in the {@link RegistrationForm}.
	 *
	 * @param form must not be {@literal null}.
	 * @return {@link Eventmanager}
	 */
	public Eventmanager createEventmanager(RegistrationForm form) {

		Assert.notNull(form, "Registration form must not be null!");

		UserAccount userAccount = userAccounts.create(form.getEmail(), form.getPassword() , Role.of("ROLE_EVENTMANAGER"));
		userAccount.setFirstname(form.getFirstname());
		userAccount.setLastname(form.getLastname());
		userAccount.setEmail(form.getEmail());
		userAccounts.save(userAccount);

		String[] adress = {form.getStreet(), form.getPostcode(), form.getCity(), form.getCountry()};
		String company = form.getCompany();

		return eventmanagers.save(new Eventmanager(userAccount,adress,company));
	}

	/**
	 * Deletes a {@link Eventmanager} using a username.
	 *
	 * @param eventmanager_username must not be {@literal null}.
	 */
	public void deleteEventmanager(String eventmanager_username){
		for (Eventmanager eventmanager : eventmanagers.findAll()) {
			if (eventmanager.getUserAccount().getUsername().equals(eventmanager_username)){
				for(Event e : eventCatalog.findByCreatorName(eventmanager.getUserAccount().getUsername())){
					eventManagement.deleteEvent(e);
				}
				userAccounts.disable(userAccounts.findByUsername(eventmanager_username)
						.orElseThrow(() -> new NullPointerException("Eventmanager not present!")).getId());
				eventmanagers.delete(eventmanager);
			}
		}
	}

	/**
	 * Returns all {@link Eventmanager}s currently available in the system.
	 *
	 * @return {@link Streamable<Eventmanager>}
	 */
	public Streamable<Eventmanager> findAll() {
		return Streamable.of(eventmanagers.findAll());
	}
}