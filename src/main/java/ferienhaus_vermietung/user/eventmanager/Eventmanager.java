package ferienhaus_vermietung.user.eventmanager;

import ferienhaus_vermietung.user.User;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Eventmanager extends User {

	private @Id long id;

	private String company;

	@SuppressWarnings("unused")
	private Eventmanager() {}

	/**
	 * @param company must not be {@literal null}.
	 */
	public Eventmanager(UserAccount userAccount , String[] adress, String company) {

		super(userAccount, adress);

		Assert.notNull(company, "Company must not be null!");

		id = getId();
		this.company = company;
	}

	/**
	 * @return the {@link Eventmanager}s company
	 */
	public String getCompany() {
		return company;
	}
}