package ferienhaus_vermietung.finances.invoice;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import ferienhaus_vermietung.event.EventCatalog;
import ferienhaus_vermietung.holidayHome.CatalogHouses;
import ferienhaus_vermietung.order.Booking;
import org.javamoney.moneta.Money;
import org.salespointframework.order.OrderLine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.money.MonetaryAmount;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.stream.Stream;

import static org.salespointframework.core.Currencies.EURO;

@Service
@Transactional
public class InvoiceManagement {

	private final EventCatalog eventCatalog;
	private final CatalogHouses catalogHouses;
	private final int numColumns = 4;

	InvoiceManagement(EventCatalog eventCatalog, CatalogHouses catalogHouses){
		this.eventCatalog = eventCatalog;
		this.catalogHouses = catalogHouses;
	}

	public Document createInvoice(Booking booking) {
		Document doc = new Document();
		try {
			PdfWriter.getInstance(doc, new FileOutputStream("./src/main/webapp/invoices/Invoice-"
					+ booking.getStatus() + "-" + booking.getId() + ".pdf"));
		} catch (DocumentException | FileNotFoundException e) {
			e.printStackTrace();
		}

		doc.open();
		addMetaData(doc, booking);
		PdfPTable table = new PdfPTable(numColumns);
		addTableHeader(table);
		addRows(table, booking);

		try {
			doc.add(table);
		} catch (DocumentException e){
			e.printStackTrace();
		}
		doc.close();

		return doc; //invoiceRepository.save(doc);
	}

	private void addMetaData(Document doc, Booking booking){
		doc.addTitle("Rechnung");
		doc.addCreationDate();
		doc.addSubject("Status: " + booking.getStatus());
		doc.addSubject("Von: " + booking.getStringFirstDate() + "Bis:" + booking.getStringLastDate());
	}

	private void addTableHeader(PdfPTable table) {
		Stream.of("Product", "Price", "Quantity", "Total")
				.forEach(columnTitle -> {
					PdfPCell header = new PdfPCell();
					header.setBackgroundColor(BaseColor.LIGHT_GRAY);
					header.setBorderWidth(1);
					header.setPhrase(new Phrase(columnTitle));
					table.addCell(header);
				});
	}

	private void addRows(PdfPTable table, Booking booking) {
		MonetaryAmount totalTotal = Money.of(0, EURO);
		for(OrderLine ol: booking.getOrderLines()) {
			if (eventCatalog.findById(ol.getProductIdentifier()).isPresent()) {
				table.addCell(eventCatalog.findById(ol.getProductIdentifier()).get().getName());
				table.addCell(eventCatalog.findById(ol.getProductIdentifier()).get().getPrice().toString());
				totalTotal.add(eventCatalog.findById(ol.getProductIdentifier()).get().getPrice());
			}
			if (catalogHouses.findById(ol.getProductIdentifier()).isPresent()) {
				table.addCell(catalogHouses.findById(ol.getProductIdentifier()).get().getName());
				table.addCell(catalogHouses.findById(ol.getProductIdentifier()).get().getPrice().toString());
				totalTotal.add(catalogHouses.findById(ol.getProductIdentifier()).get().getPrice());
			}
			table.addCell(ol.getQuantity().toString());
			table.addCell(ol.getPrice().toString());
		}
		table.addCell("");
		table.addCell("");
		table.addCell("Gesamt:");
		table.addCell(totalTotal.toString());
	}
}
