package ferienhaus_vermietung.event;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

interface WeeklyEventForm {

	@NotEmpty(message = "{EventForm.name.NotEmpty}") //
	String getName();

	@NotEmpty(message = "{Place.NotEmpty}") //
	String getPlace();

	@NotEmpty(message = "{Description.NotEmpty}") //
	String getDescription();

	@NotEmpty(message = "{Day.NotEmpty}") //
	String getDay();

	@NotNull(message = "{Hours.NotEmpty}") //
	Integer getHours();

	@NotNull(message = "{Minutes.NotEmpty}") //
	Integer getMinutes();
}
