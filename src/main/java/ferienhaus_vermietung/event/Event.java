package ferienhaus_vermietung.event;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.springframework.util.Assert;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.salespointframework.core.Currencies.EURO;

@Entity
public class Event extends Product {

	public enum EventType {
		UNIQUE, WEEKLY
	}

	private String place, description;
	private LocalDate date;
	private LocalTime time;
	private String creatorName;
	private EventType type;
	private String day;
	private double actualprice;

	@SuppressWarnings("unused")
	private Event() {}

	//for unique events

	/**
	 * @param name must not be {@literal null}.
	 * @param price must not be {@literal null}.
	 * @param place must not be {@literal null}.
	 * @param description must not be {@literal null}.
	 * @param date must not be {@literal null}.
	 * @param time must not be {@literal null}.
	 * @param creatorName must not be {@literal null}.
	 */
	public Event(String name, double price, String place, String description, LocalDate date,
				 LocalTime time, String creatorName) {
		super(name, Money.of(price, EURO));

		Assert.notNull(name, "Name must not be null!");
		Assert.notNull(place, "Place must not be null!");
		Assert.notNull(description, "Description must not be null!");
		Assert.notNull(date, "Date must not be null!");
		Assert.notNull(time, "Time must not be null!");
		Assert.notNull(creatorName, "CreatorName must not be null");

		this.place = place;
		this.description = description;
		this.date = date;
		this.time = time;
		this.creatorName = creatorName;
		this.type = EventType.UNIQUE;
		this.actualprice = price;
	}

	//for weekly events

	/**
	 * @param name must not be {@literal null}.
	 * @param day must not be {@literal null}.
	 * @param place must not be {@literal null}.
	 * @param description must not be {@literal null}.
	 * @param time must not be {@literal null}.
	 * @param creatorName must not be {@literal null}.
	 */
	public Event(String name, String day, String place, String description, LocalTime time, String creatorName) {
		super(name, Money.of(0, EURO));

		Assert.notNull(name, "Name must not be null!");
		Assert.notNull(day, "Day must not be null!");
		Assert.notNull(place, "Place must not be null!");
		Assert.notNull(description, "Description must not be null!");
		Assert.notNull(time, "Time must not be null!");
		Assert.notNull(creatorName, "CreatorName must not be null");

		this.day = day;
		this.place = place;
		this.description = description;
		this.time = time;
		this.creatorName = creatorName;
		this.type = EventType.WEEKLY;
	}

	//Getter

	public String getPlace() {
		return place;
	}

	public String getDescription() {
		return description;
	}

	public LocalDate getDate() {
		return date;
	}

	public LocalTime getTime() {
		return time;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public EventType getType() {
		return type;
	}

	public String getDay() {
		return day;
	}

	public double getActualPrice() {
		return actualprice;
	}

	public String getActualDate() {
		return getActualDay()+"."+getActualMonth()+"."+getActualYear();
	}

	public int getActualDay(){
		return date.getDayOfMonth();
	}

	public int getActualMonth(){
		return date.getMonthValue();
	}

	public int getActualYear(){
		return date.getYear();
	}

	public int getActualHours(){
		return time.getHour();
	}

	public int getActualMinutes(){
		return time.getMinute();
	}

	//Setter

	public void setPlace(String place) {
		this.place = place;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public boolean overlaps(LocalDate startD, LocalDate endD) {
		return startD.isBefore(date) && endD.isAfter(date) || endD.equals(date);
	}
}
