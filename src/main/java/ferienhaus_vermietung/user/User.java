package ferienhaus_vermietung.user;

import org.salespointframework.useraccount.UserAccount;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class User {

	private @Id @GeneratedValue long id;

	private String[] adress;
	private String birthday;

	@OneToOne
	private UserAccount userAccount;

	/**
	 * @param userAccount must not be {@literal null}.
	 * @param adress must not be {@literal null}.
	 */
	protected User(UserAccount userAccount, String[] adress) {

		Assert.notNull(userAccount, "UserAccount must not be null!");
		Assert.notNull(adress, "Adress must not be null!");

		this.userAccount = userAccount;
		this.adress = adress;

	}

	protected User() {
	}

	/**
	 * @return the {@link UserAccount} that is mapped to the {@link User}.
	 */
	public UserAccount getUserAccount() {
		return userAccount;
	}

	/**
	 * @return the generated Id of the {@link User}.
	 */
	protected long getId() {
		return id;
	}

	/**
	 * @return the Adress of the {@link User}.
	 */
	public String[] getAdress() {
		return adress;
	}

	/**
	 * @return the {@link org.salespointframework.useraccount.Role} of the {@link User} as a {@link String}.
	 */
	public String getRole(){
		return getUserAccount().getRoles().get().findFirst()
				.orElseThrow(() -> new NullPointerException("UserAccount not present!")).toString();
	}

}