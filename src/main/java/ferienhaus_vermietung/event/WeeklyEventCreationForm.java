package ferienhaus_vermietung.event;

import ferienhaus_vermietung.holidayHome.House;

import javax.validation.constraints.NotNull;
import java.util.Set;

interface WeeklyEventCreationForm extends WeeklyEventForm{

	@NotNull(message = "{EventForm.houses.NotEmpty}") //
	Set<House> getHouses();
}