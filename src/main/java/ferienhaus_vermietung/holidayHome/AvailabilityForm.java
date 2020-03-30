package ferienhaus_vermietung.holidayHome;

import javax.validation.constraints.NotNull;

interface AvailabilityForm {
	@NotNull(message = "{Year.NotEmpty}") //
	Integer getYearStart();

	@NotNull(message = "{Month.NotEmpty}") //
	Integer getMonthStart();

	@NotNull(message = "{Day.NotEmpty}") //
	Integer getDayStart();

	@NotNull(message = "{Year.NotEmpty}") //
	Integer getYearEnd();

	@NotNull(message = "{Month.NotEmpty}") //
	Integer getMonthEnd();

	@NotNull(message = "{Day.NotEmpty}") //
	Integer getDayEnd();

	Integer getGuests();

	boolean getKitchenSearch();

	boolean getAcSearch();

	boolean getWashingMachineSearch();

	boolean getTumbleDryerSearch();

	boolean getWifiSearch();

	boolean getBabyCribSearch();

	boolean getTelevisionSearch();
}