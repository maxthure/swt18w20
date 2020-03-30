package ferienhaus_vermietung.order;

import org.salespointframework.order.OrderIdentifier;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.time.LocalDateTime;

public interface BookingRepository extends CrudRepository<Booking, OrderIdentifier> {
	Streamable<Booking> findByDateCreatedBetween(LocalDateTime from, LocalDateTime to);

	/**
	 * @param status
	 * @return {@link Streamable<Booking>}
	 */
	Streamable<Booking> findByStatus(Status status);

	/**
	 * @param userAccount
	 * @return {@link Streamable<Booking>}
	 */
	Streamable<Booking> findByUserAccount(UserAccount userAccount);

	/**
	 * @param userAccount
	 * @param from
	 * @param to
	 * @return {@link Streamable<Booking>}
	 */
	Streamable<Booking> findByUserAccountAndDateCreatedBetween(UserAccount userAccount, LocalDateTime from,
															   LocalDateTime to);
}


