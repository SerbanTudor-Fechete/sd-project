package com.andrei.demo.service;

import com.andrei.demo.model.ServiceAppointment;
import com.andrei.demo.model.Part;
import com.andrei.demo.repository.ServiceAppointmentRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class PdfService {

    private final ServiceAppointmentRepository appointmentRepository;

    public PdfService(ServiceAppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generateInvoice(UUID appointmentId) {

        ServiceAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("MOTO-FIX GARAGE")
                .setFontSize(24)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("123 Mechanic Lane, Engine City, 90210 | (555) 123-4567")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        document.add(new Paragraph("INVOICE").setFontSize(18));
        document.add(new Paragraph("Invoice ID: " + appointment.getId()));
        document.add(new Paragraph("Generated On: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))));
        document.add(new Paragraph("Service Date: " + appointment.getScheduleDate()));
        document.add(new Paragraph("Status: " + appointment.getStatus().name()).setMarginBottom(20));

        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();

        String customerName = appointment.getMotorcycle().getOwner().getName();
        String bikeDetails = appointment.getMotorcycle().getManufactureYear() + " " +
                appointment.getMotorcycle().getBrand() + " " +
                appointment.getMotorcycle().getModel();
        String licensePlate = appointment.getMotorcycle().getLicensePlate();

        infoTable.addCell(createNoBorderCell("Billed To:\n" + customerName));
        infoTable.addCell(createNoBorderCell("Vehicle:\n" + bikeDetails + "\nPlate: " + licensePlate).setTextAlignment(TextAlignment.RIGHT));
        document.add(infoTable.setMarginBottom(20));

        Table itemTable = new Table(UnitValue.createPercentArray(new float[]{60, 20, 20})).useAllAvailableWidth();

        itemTable.addHeaderCell(createHeaderCell("Description"));
        itemTable.addHeaderCell(createHeaderCell("Qty").setTextAlignment(TextAlignment.CENTER));
        itemTable.addHeaderCell(createHeaderCell("Price").setTextAlignment(TextAlignment.RIGHT));

        double partsTotal = 0.0;
        if (appointment.getPartsUsed() != null) {
            for (Part part : appointment.getPartsUsed()) {
                partsTotal += part.getPrice();

                itemTable.addCell(new Cell().add(new Paragraph("Part: " + part.getName())));
                itemTable.addCell(new Cell().add(new Paragraph("1")).setTextAlignment(TextAlignment.CENTER));
                itemTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", part.getPrice()))).setTextAlignment(TextAlignment.RIGHT));
            }
        }

        double laborCost = appointment.getTotalCost() - partsTotal;

        itemTable.addCell(new Cell().add(new Paragraph("Labor: " + appointment.getDescription())));
        itemTable.addCell(new Cell().add(new Paragraph("1")).setTextAlignment(TextAlignment.CENTER));
        itemTable.addCell(new Cell().add(new Paragraph(String.format("$%.2f", laborCost))).setTextAlignment(TextAlignment.RIGHT));

        document.add(itemTable.setMarginBottom(20));

        Paragraph totalParagraph = new Paragraph("TOTAL DUE: " + String.format("$%.2f", appointment.getTotalCost()))
                .setFontSize(16)
                .setTextAlignment(TextAlignment.RIGHT);
        document.add(totalParagraph);

        document.add(new Paragraph("\nThank you for trusting Moto-Fix Garage with your ride!")
                .setTextAlignment(TextAlignment.CENTER));

        document.close();

        return outputStream.toByteArray();
    }

    private Cell createNoBorderCell(String text) {
        return new Cell().add(new Paragraph(text)).setBorder(Border.NO_BORDER);
    }

    private Cell createHeaderCell(String text) {
        return new Cell().add(new Paragraph(text))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBorder(new SolidBorder(ColorConstants.GRAY, 1));
    }
}