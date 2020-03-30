package ferienhaus_vermietung.event;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

interface UniqueEventForm {

	@NotEmpty(message = "{EventForm.name.NotEmpty}") //
	String getName();

	@NotNull(message = "{uniqueEventForm.price.NotEmpty}") //
	Double getPrice();

	@NotEmpty(message = "{Place.NotEmpty}") //
	String getPlace();

	@NotEmpty(message = "{Description.NotEmpty}") //
	String getDescription();

	@NotNull(message = "{Year.NotEmpty}") //
	Integer getYear();

	@NotNull(message = "{Month.NotEmpty}") //
	Integer getMonth();

	@NotNull(message = "{Day.NotEmpty}") //
	Integer getDay();

	@NotNull(message = "{Hours.NotEmpty}") //
	Integer getHours();

	@NotNull(message = "{Minutes.NotEmpty}") //
	Integer getMinutes();
}
