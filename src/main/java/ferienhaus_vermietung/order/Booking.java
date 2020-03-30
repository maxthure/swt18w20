package ferienhaus_vermietung.order;

import ferienhaus_vermietung.holidayHome.House;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.order.*;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.useraccount.UserAccount;
import org.springframework.util.Assert;

import javax.money.MonetaryAmount;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.salespointframework.core.Currencies.EURO;

@Entity
public class Booking extends Order {
	private Status status;


	private MonetaryAmount paid;
	private LocalDate firstDate;
	private LocalDate lastDate;

	@ManyToOne
	private House house;

	private int range;

	/**
	 * @param status must not be {@literal null}.
	 * @param userAccount must not be {@literal null}.
	 * @param paymentMethod must not be {@literal null}.
	 * @param firstDate must not be {@literal null}.
	 * @param lastDate must not be {@literal null}.
	 */
	public Booking(Status status, UserAccount userAccount, PaymentMethod paymentMethod, LocalDate firstDate,
				   LocalDate lastDate, House house) {
		super(userAccount, paymentMethod);

		Assert.notNull(paymentMethod, "PaymentMethod must not be null");
		Assert.notNull(firstDate, "FirstDate must not be null");
		Assert.notNull(lastDate, "LastDate must not be null");
		Assert.notNull(house, "House must not be null");


		this.status = status;
		this.firstDate = firstDate;
		this.lastDate = lastDate;
		this.house = house;
		calcRange(firstDate,lastDate);
		paid = Money.of(0,EURO);
	}
	private Booking() {
	}

	/**
	 *
	 * @return the amount the User already paid.
	 */
	public MonetaryAmount getPaid() {
		return paid;
	}

	/**
	 *
	 * @param paid must not be {@literal null}.
	 */
	public void setPaid(MonetaryAmount paid) {
		this.paid = paid;
	}

	/**
	 *
	 * @return only the costs of the house.
	 */
	public MonetaryAmount getPaidHouse() {
		for (OrderLine ol : getOrderLines()) {
			if(ol.getProductIdentifier().equals(house.getId())){
				return ol.getPrice();
			}
		}
		return Money.of(0, EURO);
	}

	/**
	 *
	 * @return the status the booking is.
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 *
	 * @param status must not be {@literal null}.
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 *
	 * @return if the booking status is booked.
	 */
	public boolean isBooked() {
		return status == Status.BOOKING;
	}

	/**
	 *
	 * @return the first day of the booking.
	 */
	public LocalDate getFirstDate() {
		return firstDate;
	}

	/**
	 *
	 * @return
	 */
	public String getStringFirstDate(){
		return firstDate.getDayOfMonth()+"."+firstDate.getMonthValue()+"."+firstDate.getYear();
	}

	/**
	 *
	 * @return
	 */
	public String getStringDayStart(){
		return Integer.toString(firstDate.getDayOfMonth());
	}

	/**
	 *
	 * @return
	 */
	public String getStringMonthStart(){
		return Integer.toString(firstDate.getMonthValue());
	}
	/**
	 *
	 * @return
	 */
	public String getStringYearStart(){
		return Integer.toString(firstDate.getYear());
	}
	/**
	 *
	 * @return
	 */
	public String getStringDayEnd(){
		return Integer.toString(lastDate.getDayOfMonth());
	}
	/**
	 *
	 * @return
	 */
	public String getStringMonthEnd(){
		return Integer.toString(lastDate.getMonthValue());
	}
	/**
	 *
	 * @return
	 */
	public String getStringYearEnd(){
		return Integer.toString(lastDate.getYear());
	}

	/**
	 *
	 * @param firstDate must not be {@literal null}.
	 */
	public void setFirstDate(LocalDate firstDate) {
		this.firstDate = firstDate;
	}

	/**
	 *
	 * @return the last day of the booking.
	 */
	public LocalDate getLastDate() {
		return lastDate;
	}
	/**
	 *
	 * @return
	 */
	public String getStringLastDate(){
		return lastDate.getDayOfMonth()+"."+lastDate.getMonthValue()+"."+lastDate.getYear();
	}

	/**
	 *
	 * @param lastDate must not be {@literal null}.
	 */
	public void setLastDate(LocalDate lastDate) {
		this.lastDate = lastDate;
	}

	/**
	 *
	 * @return the house linked to the booking.
	 */
	public House getHouse() {
		return house;
	}

	/**
	 *
	 * @param house must not be {@literal null}.
	 */

 	public void setHouse(House house) {
		this.house = house;
	}

	/**
	 *
	 * @return the length of the stay in days.
	 */

	public int getRange() {
		return range;
	}

	/**
	 *
	 * @param range must not be {@literal null}.
	 */
	public void setRange(int range) {
		this.range = range;
	}

	/**
	 * calculates the length of the stay
	 * @param fD  must not be {@literal null}.
	 * @param lD  must not be {@literal null}.
	 */

	private void calcRange(LocalDate fD, LocalDate lD) {
		int calc = 0;

		if(!fD.isAfter(lD) || !lD.isBefore(fD)) {
			calc = (int) fD.until(lD, DAYS);
		} else {
			System.err.print("Datum ungültig");
		}
		range = calc;
	}

	/**
	 *
	 * @return
	 */
	public List<ProductIdentifier> getProductIdentifiers() {
		List<ProductIdentifier> productIdentifiersList = new ArrayList<>();
		for (OrderLine oL: this.getOrderLines()) {
			productIdentifiersList.add(oL.getProductIdentifier());
		}
		return productIdentifiersList;
	}

	/**
	 *
	 * @return itself for the BookingManager.
	 */

	public Booking markAsReservation() {
		this.status = Status.RESERVATION;
		return this;
	}

	/**
	 *
	 * @return itself for the BookingManager.
	 */

	public Booking markAsOpen() {
		this.status = Status.OPEN;
		return this;
	}

	/**
	 *
	 * @return itself for the BookingManager.
	 */

	public Booking markAsBooking() {
		this.status = Status.BOOKING;
		return this;
	}

	/**
	 *
	 * @return itself for the BookingManager.
	 */

	public Booking markAsPaid() {
		this.status = Status.PAID;
		return this;
	}
	/**
	 *
	 * @return itself for the BookingManager.
	 */

	public Booking markAsCompleted() {

		Assert.isTrue(this.status == Status.PAID, "An order must be paid to be completed!");

		this.status = Status.COMPLETED;

		return this;
	}
	/**
	 *
	 * @return itself for the BookingManager.
	 */
	public Booking markAsCancelled() {
		this.status = Status.CANCELLED;
		return this;
	}
	/**
	 *
	 * @return itself for the BookingManager.
	 */
	public Booking markAsCancelledLate() {
		this.status = Status.CANCELLEDLATE;
		return this;
	}

}