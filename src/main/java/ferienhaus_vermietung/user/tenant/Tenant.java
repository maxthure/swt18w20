package ferienhaus_vermietung.user.tenant;

import ferienhaus_vermietung.user.User;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Tenant extends User {

	private @Id long id;

	@SuppressWarnings("unused")
	private Tenant() {}

	public Tenant(UserAccount userAccount , String[] adress) {
		super(userAccount, adress);
		id = getId();
	}

}