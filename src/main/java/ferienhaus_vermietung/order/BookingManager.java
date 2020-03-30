package ferienhaus_vermietung.order;

import ferienhaus_vermietung.finances.invoice.InvoiceManagement;
import org.javamoney.moneta.Money;
import org.salespointframework.order.*;
import org.salespointframework.time.Interval;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.data.util.Streamable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

import static org.salespointframework.core.Currencies.EURO;

@Service
@Transactional
public class BookingManager {
	private final BookingRepository bookingRepository;
	private final InvoiceManagement invoiceManagement;
	private final double percentageDeposit = 0.1;

	/**
	 *
	 * @param bookingRepository
	 * @param invoiceManagement
	 */
	public BookingManager( BookingRepository bookingRepository, InvoiceManagement invoiceManagement) {

		Assert.notNull(bookingRepository, "bookingRepository must not be null!");
		Assert.notNull(invoiceManagement, "invoiceRepository must not be null!");

		this.bookingRepository = bookingRepository;
		this.invoiceManagement = invoiceManagement;
	}

	/**
	 *
	 * @param booking  must not be {@literal null}.
	 * @return
	 */
	public Booking save(Booking booking) {
		Assert.notNull(booking, "booking must be not null");
		return bookingRepository.save(booking);
	}


	/**
	 *
	 * @param orderIdentifier must not be {@literal null}.
	 * @return
	 */
	public Optional<Booking> get(OrderIdentifier orderIdentifier) {
		Assert.notNull(orderIdentifier, "orderIdentifier must not be null");
		return bookingRepository.findById(orderIdentifier);
	}

	/**
	 *
	 * @param orderIdentifier must not be {@literal null}.
	 * @return
	 */

	public boolean contains(OrderIdentifier orderIdentifier) {
		Assert.notNull(orderIdentifier, "OrderIdentifier must not be null");

		return bookingRepository.existsById(orderIdentifier);
	}

	/**
	 *
	 * @param status must not be {@literal null}.
	 * @return
	 */

	public Streamable<Booking> findBy(Status status) {
		Assert.notNull(status, "Status must not be null");

		return bookingRepository.findByStatus(status);
	}

	/**
	 *
	 * @param interval must not be {@literal null}.
	 * @return
	 */

	public Streamable<Booking> findBy(Interval interval) {
		return bookingRepository.findByDateCreatedBetween(interval.getStart(), interval.getEnd());
	}

	/**
	 *
	 * @param userAccount must not be {@literal null}.
	 * @return
	 */

	public Streamable<Booking> findBy(UserAccount userAccount) {
		Assert.notNull(userAccount, "UserAccount must not be null");

		return bookingRepository.findByUserAccount(userAccount);
	}

	/**
	 *
	 * @param userAccount must not be {@literal null}.
	 * @param interval must not be {@literal null}.
	 * @return
	 */

	public Streamable<Booking> findBy(UserAccount userAccount, Interval interval) {
		Assert.notNull(userAccount, "UserAccount must not be null");
		Assert.notNull(interval, "Interval must not be null!");

		return bookingRepository.findByUserAccountAndDateCreatedBetween(userAccount,interval.getStart(),
				interval.getEnd());
	}

	/**
	 * sets the booking status in reservation if the pre status is the correct one.
	 * @param booking  must not be {@literal null}.
	 * @return true when successful.
	 */

	public boolean setReserved(Booking booking) {

		Assert.notNull(booking, "booking must not be null");
		if(booking.getStatus().equals(Status.RESERVATION)) {
			booking.setPaid(Money.of(0, EURO));
			save(booking.markAsReservation());
			invoiceManagement.createInvoice(booking);
			return true;
		}
		return false;
	}

	/**
	 * sets the booking status in open if the pre status is the correct one.
	 * @param booking  must not be {@literal null}.
	 * @return true when successful.
	 */
	public boolean setOpen(Booking booking) {
		Assert.notNull(booking, "booking must not be null");
		if(booking.getStatus().equals(Status.OPEN) || booking.getStatus().equals(Status.RESERVATION)) {
			booking.setPaid(Money.of(0,EURO));
			save(booking.markAsOpen());
			invoiceManagement.createInvoice(booking);
			return true;
		}
		return false;
	}

	/**
	 * sets the booking status in booking if the pre status is the correct one.
	 * the paid amount of the booking gets adjusted.
	 * @param booking must not be {@literal null}.
	 * @return true when successful.
	 */

	public boolean payDeposit(Booking booking) {
		Assert.notNull(booking,"booking must not be null");
		if(booking.getPaid().isZero() && (booking.getStatus() == Status.RESERVATION ||
				booking.getStatus()==Status.OPEN)){
			booking.setPaid(booking.getTotalPrice().multiply(percentageDeposit));
			save(booking.markAsBooking());
			invoiceManagement.createInvoice(booking);
			return true;
		}
		return false;

	}

	/**
	 * sets the booking status in paid if the pre status is the correct one.
	 * the paid amount of the booking gets adjusted.
	 * @param booking must not be {@literal null}.
	 * @return true when successful.
	 */

	public boolean payBooking(Booking booking) {
		Assert.notNull(booking, "booking must not be null");

		if (booking.getStatus() == Status.RESERVATION || booking.getStatus() == Status.OPEN ||
				booking.getStatus() == Status.BOOKING ) {
			booking.setPaid(booking.getTotalPrice());
			save(booking.markAsPaid());
			invoiceManagement.createInvoice(booking);
			return true;
		}
		return false;
	}

	/**
	 * sets the booking status in completed if the pre status is the correct one.
	 * @param booking must not be {@literal null}.
	 * @return true when successful.
	 */

	public boolean completeBooking(Booking booking) {
		Assert.notNull(booking, "booking must not be null!");

		if (booking.getStatus() != Status.PAID) {
			return false;
		} else {
			save(booking.markAsCompleted());
			invoiceManagement.createInvoice(booking);
			return true;
		}
	}

	/**
	 * cancels the booking with no costs for the user.
	 * @param booking must not be {@literal null}.
	 * @return true when successful.
	 */
	public boolean cancelBooking(Booking booking) {
		Assert.notNull(booking, "booking must not be null");

		if (booking.getStatus() != Status.CANCELLED || booking.getStatus() != Status.CANCELLEDLATE ||
				booking.getStatus() != Status.COMPLETED) {
			booking.setPaid(Money.of(0,EURO));
			save(booking.markAsCancelled());
			invoiceManagement.createInvoice(booking);
			return true;
		}
		return false;
	}

	/**
	 * cancels the bookings with costs for the user.
	 * @param booking must not be {@literal null}.
	 */
	public boolean cancelLateBooking(Booking booking) {
		Assert.notNull(booking, "booking must not be null");

		if (booking.getStatus() != Status.CANCELLED || booking.getStatus() != Status.CANCELLEDLATE ||
				booking.getStatus() != Status.COMPLETED) {

			booking.setPaid(booking.getTotalPrice().multiply(percentageDeposit));
			save(booking.markAsCancelledLate());
			invoiceManagement.createInvoice(booking);
			return true;
		}
		return false;
	}

	/**
	 *  deletes the booking.
	 * @param booking must not be {@literal null}.
	 * @return
	 */
	public Booking delete(Booking booking) {

		Assert.notNull(booking, "booking must not be null!");

		bookingRepository.delete(booking);

		return booking;
	}
}
