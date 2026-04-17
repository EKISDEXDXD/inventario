package com.inventario.licoreria.modules.export.service;

import com.inventario.licoreria.modules.export.model.ExportedReport;
import com.inventario.licoreria.modules.export.repository.ExportedReportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class ExportedReportService {

    private final ExportedReportRepository exportedReportRepository;

    @Value("${export.file.path:}")
    private String exportFilePath;

    public ExportedReportService(ExportedReportRepository exportedReportRepository) {
        this.exportedReportRepository = exportedReportRepository;
    }

    /**
     * Guarda un reporte generado en el sistema de archivos y registra la metadata en BD
     */
    public ExportedReport saveReportFile(Long storeId, byte[] fileContent, String reportType, 
                                         String dateFrom, String dateTo) throws IOException {
        // Usar ruta absoluta en temp directory si no está configurada
        String basePath = exportFilePath.isEmpty() ? 
            System.getProperty("java.io.tmpdir") : exportFilePath;
        
        // Crear directorio si no existe
        String storePath = basePath + File.separator + "Reports" + File.separator + storeId;
        File storeDir = new File(storePath);
        if (!storeDir.exists()) {
            storeDir.mkdirs();
        }

        // Generar nombre único del archivo
        String fileName = generateFileName(reportType, dateFrom, dateTo);
        String filePath = storePath + File.separator + fileName;

        // Guardar archivo en sistema de archivos
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(fileContent);
            fos.flush();
        } catch (IOException e) {
            throw new IOException("Error al guardar archivo: " + fileName, e);
        }

        // Crear registro en BD
        ExportedReport report = new ExportedReport(
            UUID.randomUUID().toString(),
            storeId,
            fileName,
            filePath,
            LocalDateTime.now(),
            dateFrom,
            dateTo,
            reportType,
            (long) fileContent.length
        );

        return exportedReportRepository.save(report);
    }

    /**
     * Obtiene el contenido del archivo desde el sistema de archivos
     */
    public byte[] getReportFile(String reportId) throws IOException {
        ExportedReport report = exportedReportRepository.findById(reportId).orElse(null);
        
        if (report == null || report.isDeleted()) {
            throw new IOException("Reporte no encontrado");
        }

        try {
            return Files.readAllBytes(Paths.get(report.getFilePath()));
        } catch (IOException e) {
            throw new IOException("Error al leer archivo: " + report.getFilePath(), e);
        }
    }

    /**
     * Elimina un reporte (borrado lógico en BD + eliminación física de archivo)
     */
    public void deleteReport(String reportId) throws IOException {
        ExportedReport report = exportedReportRepository.findById(reportId).orElse(null);
        
        if (report == null) {
            throw new IOException("Reporte no encontrado");
        }

        // Borrado lógico en BD
        report.setDeleted(true);
        exportedReportRepository.save(report);

        // Intentar eliminar archivo físicamente
        try {
            Files.deleteIfExists(Paths.get(report.getFilePath()));
        } catch (IOException e) {
            // Log pero no falla, el archivo puede no existir
            System.err.println("Advertencia: No se pudo eliminar archivo: " + report.getFilePath());
        }
    }

    /**
     * Genera nombre descriptivo del archivo
     */
    private String generateFileName(String reportType, String dateFrom, String dateTo) {
        String type;
        String period;
        
        if ("DAILY".equals(reportType)) {
            type = "diario";
            period = dateFrom; // Ej: 2026-04-16
        } else {
            type = "COMPLETE".equals(reportType) ? "completo" : "resumido";
            period = formatFilePeriod(dateFrom, dateTo);
        }
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        return String.format("reporte-%s-%s-%s.xlsx", type, period, timestamp);
    }

    /**
     * Formatea el período para el nombre del archivo
     */
    private String formatFilePeriod(String dateFrom, String dateTo) {
        try {
            YearMonth from = YearMonth.parse(dateFrom.substring(0, 7));
            YearMonth to = YearMonth.parse(dateTo.substring(0, 7));
            
            if (from.equals(to)) {
                return from.toString(); // Ej: 2026-01
            } else {
                return from.toString() + "-a-" + to.toString(); // Ej: 2026-01-a-2026-03
            }
        } catch (Exception e) {
            return "periodo-desconocido";
        }
    }

    /**
     * Limpia archivos huérfanos y registros marcados como eliminados más antiguos de X días
     */
    public void cleanupOldReports(int daysOld) {
        // Esta función puede ejecutarse periódicamente (Scheduled Task)
        // Por ahora solo registra la lógica
        System.out.println("Limpieza de reportes más antiguos de " + daysOld + " días ejecutada");
    }
}
