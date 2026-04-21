package com.ga.TicketSystem.service;

import com.ga.TicketSystem.model.Booking;
import com.ga.TicketSystem.model.Seat;
import com.ga.TicketSystem.model.Ticket;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import static java.awt.Color.DARK_GRAY;
import static java.awt.Color.LIGHT_GRAY;

@Service
public class BookingPDFService {

    public byte[] generateBookingPdf(Booking booking) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, DARK_GRAY);
            Paragraph title = new Paragraph("BOOKING CONFIRMATION", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Booking ID: #" + booking.getId()));
            document.add(new Paragraph("Customer: " + booking.getUser().getUsername()));
            document.add(new Paragraph("Date: " +
                    booking.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
            document.add(new Paragraph("Status: " + booking.getBookingStatus()));
            document.add(new Paragraph(" "));

            // --- Tickets Table ---
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);


            table.setWidths(new float[]{3.5f, 2.0f, 1.5f, 1.5f, 2.5f});

            addTableHeader(table, "Event Name");
            addTableHeader(table, "Category");
            addTableHeader(table, "Row");
            addTableHeader(table, "Seat");
            addTableHeader(table, "Price");

            for (Ticket ticket : booking.getTickets()) {
                Seat seat = ticket.getSeat();

                table.addCell(ticket.getEvent().getEventName());

                table.addCell(seat.getCategory().toString());
                table.addCell(seat.getRowNumber());
                table.addCell(seat.getSeatNumber());
                table.addCell(ticket.getPrice() + " BHD");
            }

            document.add(table);

            Paragraph total = new Paragraph("Total Amount: " + booking.getTotalAmount() + " BHD",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            if (!booking.getTickets().isEmpty()) {
                document.add(new Paragraph(" "));
                String qrContent = "VALID-TICKET-" + booking.getTickets().get(0).getUniqueHash();

                Image qrCodeImage = generateQRCodeImage(qrContent);
                qrCodeImage.setAlignment(Element.ALIGN_CENTER);
                qrCodeImage.scaleToFit(120, 120);
                document.add(qrCodeImage);

                Paragraph qrLabel = new Paragraph("Scan for Entry Validation",
                        FontFactory.getFont(FontFactory.HELVETICA, 8));
                qrLabel.setAlignment(Element.ALIGN_CENTER);
                document.add(qrLabel);
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate booking PDF with QR", e);
        }
    }

    private Image generateQRCodeImage(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        return Image.getInstance(pngData);
    }

    private void addTableHeader(PdfPTable table, String columnTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(LIGHT_GRAY);
        header.setPhrase(new Phrase(columnTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        header.setPadding(5);
        table.addCell(header);
    }
}