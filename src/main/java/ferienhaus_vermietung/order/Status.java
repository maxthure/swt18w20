package ferienhaus_vermietung.order;

public enum Status {
	RESERVATION("reservieren"),
	OPEN("offen"),
	BOOKING("buchen"),
	PAID("bezahlt"),
	COMPLETED("abgeschlossen"),
	CANCELLED("storniert"),
	CANCELLEDLATE("spät storniert"),
	WISHLIST("auf der Wunschliste"),
	WISHLISTPOSSIBLE("Buchung jetzt möglich");



	private final String type;

	Status(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
