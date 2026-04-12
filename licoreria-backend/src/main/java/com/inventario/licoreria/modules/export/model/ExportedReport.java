package com.inventario.licoreria.modules.export.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class ExportedReport {

    @Id
    private String id;
    private Long storeId;
    private String fileName;
    private String filePath;
    private LocalDateTime dateGenerated;
    private String dateFrom;
    private String dateTo;
    private String reportType;
    private Long fileSize;
    private boolean isDeleted = false;

    public ExportedReport() {
    }

    public ExportedReport(String id, Long storeId, String fileName, String filePath, 
                         LocalDateTime dateGenerated, String dateFrom, String dateTo, 
                         String reportType, Long fileSize) {
        this.id = id;
        this.storeId = storeId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.dateGenerated = dateGenerated;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.reportType = reportType;
        this.fileSize = fileSize;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getDateGenerated() {
        return dateGenerated;
    }

    public void setDateGenerated(LocalDateTime dateGenerated) {
        this.dateGenerated = dateGenerated;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
