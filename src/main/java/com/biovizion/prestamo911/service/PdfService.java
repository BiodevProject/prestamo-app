package com.biovizion.prestamo911.service;


import com.biovizion.prestamo911.entities.CreditoCuotaEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public void generarFacturaPDF(CreditoCuotaEntity cuota, HttpServletResponse response) {
        try {
            Document document = new Document();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=factura-cuota-" + cuota.getId() + ".pdf");

            OutputStream out = response.getOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Font fontContenido = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            document.add(new Paragraph("Factura de Pago de Cuota", fontTitulo));
            document.add(new Paragraph("Código de Cuota: " + cuota.getCodigo(), fontContenido));
            document.add(new Paragraph("Fecha de Vencimiento: " + cuota.getFechaVencimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), fontContenido));
            document.add(new Paragraph("Fecha de Pago: " + cuota.getFechaPago().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), fontContenido));
            document.add(new Paragraph("Monto Pagado: $" + cuota.getMonto(), fontContenido));
            document.add(new Paragraph("Mora Pagada: $" + (cuota.getPagoMora() != null ? cuota.getPagoMora() : "0.00"), fontContenido));
            document.add(new Paragraph("Estado: " + cuota.getEstado(), fontContenido));

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage());
        }
    }
}