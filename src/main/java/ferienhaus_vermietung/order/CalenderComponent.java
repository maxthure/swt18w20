package ferienhaus_vermietung.order;

import ferienhaus_vermietung.event.Event;
import ferienhaus_vermietung.event.EventCatalog;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Component
public class CalenderComponent {

	private final EventCatalog eventCatalog;

	private static final String NL = "\r\n";
	private String version =    "VERSION:2.0"+NL;
	private String prodid =     "PRODID://swt18w20//"+NL;
	private String calBegin =   "BEGIN:VCALENDAR"+NL;
	private String calEnd =     "END:VCALENDAR"+NL;
	private String eventBegin = "BEGIN:VEVENT"+NL;
	private String eventEnd =   "END:VEVENT"+NL;

	public CalenderComponent(EventCatalog eventCatalog){
		this.eventCatalog = eventCatalog;
	}

	public void write( Booking booking ){

		Random random = new Random();

		StringBuilder builder = new StringBuilder();
		builder.append("./src/main/webapp/calendarEntries/");
		builder.append(booking.getId());
		builder.append(".ics");


		String uID = "UID:"
				+booking.getId()+"-"
				+booking.getStatus().toString()+"-"
				+random.nextInt()+random.nextInt()+random.nextInt()+random.nextInt()+random.nextInt()
				+"@swt18w20.de";

		String location = "LOCATION:"
				+booking.getHouse().getAddress()[0]+"\\, "
				+booking.getHouse().getAddress()[1]+"\\, "
				+booking.getHouse().getAddress()[booking.getHouse().getAddress().length-1];

		String summary = "SUMMARY:"
				+booking.getHouse().getName();

		String dtStart = "DTSTART;VALUE=DATE:"
				+booking.getFirstDate().toString().replaceAll("-","");

		String dtEnd = "DTEND;VALUE=DATE:"
				+booking.getLastDate().toString().replaceAll("-","");

		String dtStamp = "DTSTAMP:"
				+ LocalDateTime.now().toString().replaceAll("-","").
				replaceAll(":","").replaceAll("[.].*","Z");

		String complete = uID+NL
				+location+NL
				+summary+NL
				+dtStart+NL
				+dtEnd+NL
				+dtStamp+NL;

		try {

			File file = new File(builder.toString());

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(calBegin);
			bw.write(version);
			bw.write(prodid);
			bw.write(eventBegin);
			bw.write(complete);
			bw.write(eventEnd);

			for (Event event : booking.getOrderLines().filter(orderLine ->
					eventCatalog.findById(orderLine.getProductIdentifier()).isPresent()).filter(orderLine ->
					eventCatalog.findById(orderLine.getProductIdentifier()).orElseThrow(()
							-> new NullPointerException("Event not found")).getType().equals(Event.EventType.UNIQUE))
					.map(orderLine -> {return eventCatalog.findById(orderLine.getProductIdentifier())
							.orElseThrow(() -> new NullPointerException("Event not found"));
					})) {
				String eUID = "UID:"
						+booking.getId()+"-"
						+booking.getStatus().toString()+"-"
						+random.nextInt()+random.nextInt()+random.nextInt()+random.nextInt()+random.nextInt()
						+"@swt18w20.de";

				String eLocation = "LOCATION:"
						+event.getPlace();

				String eSummary = "SUMMARY:"
						+event.getName();

				String eDtStart = "DTSTART:"
						+event.getDate().atTime(event.getTime()).toString().replaceAll("-","")
						.replaceAll(":","")+"00Z";

				String eDtEnd = "DTEND:"
						+event.getDate().atTime(event.getTime().plus(1, ChronoUnit.HOURS)).toString()
						.replaceAll("-","").replaceAll(":","")+"00Z";

				String eDtStamp = "DTSTAMP:"
						+ LocalDateTime.now().toString().replaceAll("-","")
						.replaceAll(":","").replaceAll("[.].*","Z");

				String extra = eUID+NL
						+eLocation+NL
						+eSummary+NL
						+eDtStart+NL
						+eDtEnd+NL
						+eDtStamp+NL;

				bw.write(eventBegin);
				bw.write(extra);
				bw.write(eventEnd);

			}

			bw.write(calEnd);
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}