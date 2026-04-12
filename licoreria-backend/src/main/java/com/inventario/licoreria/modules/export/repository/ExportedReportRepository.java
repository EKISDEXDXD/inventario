package com.inventario.licoreria.modules.export.repository;

import com.inventario.licoreria.modules.export.model.ExportedReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExportedReportRepository extends JpaRepository<ExportedReport, String> {
    List<ExportedReport> findByStoreIdAndIsDeletedFalseOrderByDateGeneratedDesc(Long storeId);
}
