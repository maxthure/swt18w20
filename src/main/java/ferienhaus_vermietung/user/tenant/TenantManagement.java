package ferienhaus_vermietung.user.tenant;

import ferienhaus_vermietung.order.Booking;
import ferienhaus_vermietung.order.BookingManager;
import ferienhaus_vermietung.order.OrderComponent;
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
public class TenantManagement {

	private final TenantRepository tenants;
	private final UserAccountManager userAccounts;
	private final BookingManager bookingManager;
	private final OrderComponent orderComponent;

	/**
	 * @param tenants must not be {@literal null}.
	 * @param userAccounts must not be {@literal null}.
	 * @param bookingManager must not be {@literal null}.
	 * @param orderComponent must not be {@literal null}.
	 */
	TenantManagement(TenantRepository tenants, UserAccountManager userAccounts, BookingManager bookingManager,
					 OrderComponent orderComponent) {

		Assert.notNull(tenants, "TenantRepository must not be null!");
		Assert.notNull(userAccounts, "UserAccountManager must not be null!");
		Assert.notNull(bookingManager, "BookingManager must not be null!");
		Assert.notNull(orderComponent, "OrderComponent must not be null!");

		this.tenants = tenants;
		this.userAccounts = userAccounts;
		this.bookingManager = bookingManager;
		this.orderComponent = orderComponent;
	}

	/**
	 * Creates a new {@link Tenant} using the information given in the {@link RegistrationForm}.
	 *
	 * @param form must not be {@literal null}.
	 * @return {@link Tenant}
	 */
	public Tenant createTenant(RegistrationForm form) {

		Assert.notNull(form, "Registration form must not be null!");

		UserAccount userAccount = userAccounts.create(form.getEmail(), form.getPassword() , Role.of("ROLE_TENANT"));
		userAccount.setFirstname(form.getFirstname());
		userAccount.setLastname(form.getLastname());
		userAccount.setEmail(form.getEmail());
		userAccounts.save(userAccount);

		String[] adress = {form.getStreet(), form.getPostcode(), form.getCity(), form.getCountry()};

		Tenant tenant = new Tenant(userAccount,adress);

		return tenants.save(tenant);
	}

	/**
	 * Deletes a {@link Tenant} using a username.
	 *
	 * @param tenant_username must not be {@literal null}.
	 */
	public void deleteTenant(String tenant_username){

		for (Tenant tenant : tenants.findAll()) {
			if (tenant.getUserAccount().getUsername().equals(tenant_username)){
				Streamable<Booking> bookings = bookingManager.findBy(tenant.getUserAccount());
				for (Booking b : bookings){
					orderComponent.deleteBooking(b);
				}
				userAccounts.disable(userAccounts.findByUsername(tenant_username)
						.orElseThrow(() -> new NullPointerException("UserAccount not present!")).getId());
				tenants.delete(tenant);
			}
		}
	}

	/**
	 * Returns all {@link Tenant}s currently available in the system.
	 *
	 * @return {@link Streamable<Tenant>}
	 */
	public Streamable<Tenant> findAll() {
		return Streamable.of(tenants.findAll());
	}
}