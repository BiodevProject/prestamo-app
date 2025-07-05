package com.biovizion.prestamo911.service;

import com.biovizion.prestamo911.entities.CreditoCuotaEntity;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.OutputStream;

@Service
public class PdfService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void generarFacturaPDF(CreditoCuotaEntity cuota, HttpServletResponse response) {
        try {
            // Preparar contexto de Thymeleaf
            Context context = new Context();
            context.setVariable("cuota", cuota);

            // Renderizar HTML desde la plantilla
            String htmlContent = templateEngine.process("fragments/pdf_factura", context);

            // Configurar la respuesta
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=factura-cuota-" + cuota.getId() + ".pdf");

            try (OutputStream os = response.getOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(htmlContent, null);
                builder.toStream(os);
                builder.run();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF desde HTML: " + e.getMessage(), e);
        }
    }
}
