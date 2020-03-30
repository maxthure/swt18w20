package ferienhaus_vermietung.user.landlord;

import ferienhaus_vermietung.user.User;
import org.salespointframework.useraccount.UserAccount;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Landlord extends User {

	private @Id long id;

	@SuppressWarnings("unused")
	private Landlord() {}

	public Landlord(UserAccount userAccount , String[] adress) {
		super(userAccount, adress);
		id = getId();
	}

}