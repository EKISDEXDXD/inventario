package com.inventario.licoreria.modules.export.service;

import com.inventario.licoreria.modules.administrative_costs.model.AdministrativeCostMovement;
import com.inventario.licoreria.modules.administrative_costs.service.AdministrativeCostMovementService;
import com.inventario.licoreria.modules.inventory.model.Transaction;
import com.inventario.licoreria.modules.inventory.service.TransactionService;
import com.inventario.licoreria.modules.products.model.Product;
import com.inventario.licoreria.modules.products.service.ProductService;
import com.inventario.licoreria.modules.users.model.User;
import com.inventario.licoreria.modules.users.service.UserService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalesReportService {

    private final TransactionService transactionService;
    private final ProductService productService;
    private final UserService userService;
    private final AdministrativeCostMovementService administrativeCostMovementService;

    public SalesReportService(TransactionService transactionService, 
                             ProductService productService, 
                             UserService userService,
                             AdministrativeCostMovementService administrativeCostMovementService) {
        this.transactionService = transactionService;
        this.productService = productService;
        this.userService = userService;
        this.administrativeCostMovementService = administrativeCostMovementService;
    }

    public byte[] generateSalesReport(Long storeId, LocalDate dateFrom, LocalDate dateTo, String reportType) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // Obtener todas las transacciones
        List<Transaction> allTransactions = transactionService.findAll();
        
        // Filtrar por tienda y rango de fechas
        List<Transaction> transactions = allTransactions.stream()
            .filter(t -> {
                try {
                    // Usar la relación Product desde Transaction
                    Product product = t.getProduct();
                    if (product == null) {
                        return false;
                    }
                    return product.getStore() != null && product.getStore().getId().equals(storeId) 
                        && t.getDateTime().toLocalDate().isAfter(dateFrom.minusDays(1))
                        && t.getDateTime().toLocalDate().isBefore(dateTo.plusDays(1));
                } catch (Exception e) {
                    return false;
                }
            })
            .collect(Collectors.toList());

        // Crear hojas según tipo de reporte
        createExecutiveSummarySheet(workbook, transactions);
        createDetailedMovementsSheet(workbook, transactions);
        createAdministrativeCostsSheet(workbook, storeId, dateFrom, dateTo);
        
        // Si es COMPLETE, agregar hojas adicionales
        if ("COMPLETE".equalsIgnoreCase(reportType)) {
            createProductAnalysisSheet(workbook, transactions, storeId);
            createDailyCashFlowSheet(workbook, transactions);
        }
        // Para DAILY, no hay hojas adicionales (similar a SIMPLE)

        // Escribir a bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private void createExecutiveSummarySheet(Workbook workbook, List<Transaction> transactions) {
        Sheet sheet = workbook.createSheet("Resumen Ejecutivo");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle totalStyle = createTotalStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle currencyWithSymbolStyle = createCurrencyWithSymbolStyle(workbook);
        CellStyle labelStyle = createLabelStyle(workbook);
        CellStyle altRowStyle = createAlternateRowStyle(workbook);

        int[] rowNum = {0};

        // Título
        Row titleRow = sheet.createRow(rowNum[0]++);
        titleRow.setHeightInPoints(25);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("📊 REPORTE DE VENTAS");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));

        rowNum[0]++; // Espacio

        // Período
        Row periodRow = sheet.createRow(rowNum[0]++);
        Cell periodCell = periodRow.createCell(0);
        periodCell.setCellValue("📅 Período:");
        periodCell.setCellStyle(labelStyle);
        if (!transactions.isEmpty()) {
            LocalDate minDate = transactions.stream()
                .map(t -> t.getDateTime().toLocalDate())
                .min(LocalDate::compareTo).orElse(LocalDate.now());
            LocalDate maxDate = transactions.stream()
                .map(t -> t.getDateTime().toLocalDate())
                .max(LocalDate::compareTo).orElse(LocalDate.now());
            Cell periodValueCell = periodRow.createCell(1);
            periodValueCell.setCellValue(minDate + " a " + maxDate);
            addBorders(periodValueCell);
        }

        rowNum[0]++; // Espacio

        // Cálculos
        BigDecimal totalEntradas = BigDecimal.ZERO;
        BigDecimal totalSalidas = BigDecimal.ZERO;
        BigDecimal totalCostSold = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            Product product = t.getProduct();
            if (product == null) continue;

            if ("ENTRADA".equalsIgnoreCase(t.getType())) {
                BigDecimal cost = product.getCost() != null ? product.getCost() : BigDecimal.ZERO;
                totalEntradas = totalEntradas.add(cost.multiply(new BigDecimal(t.getQuantity())));
            } else if ("SALIDA".equalsIgnoreCase(t.getType())) {
                BigDecimal price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
                BigDecimal cost = product.getCost() != null ? product.getCost() : BigDecimal.ZERO;
                totalSalidas = totalSalidas.add(price.multiply(new BigDecimal(t.getQuantity())));
                totalCostSold = totalCostSold.add(cost.multiply(new BigDecimal(t.getQuantity())));
            }
        }

        BigDecimal gananciaTotal = totalSalidas.subtract(totalCostSold);
        BigDecimal margenPromedio = totalSalidas.compareTo(BigDecimal.ZERO) > 0 
            ? gananciaTotal.divide(totalSalidas, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal(100))
            : BigDecimal.ZERO;

        // Mostrar estadísticas con mejor formato
        sheet.setColumnWidth(0, 28);
        sheet.setColumnWidth(1, 22);

        int statsStartRow = rowNum[0];
        
        Row row = sheet.createRow(rowNum[0]++);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue("💰 Total Invertido:");
        labelCell.setCellStyle(labelStyle);
        Cell cell = row.createCell(1);
        cell.setCellValue(totalEntradas.doubleValue());
        cell.setCellStyle(currencyWithSymbolStyle);

        row = sheet.createRow(rowNum[0]++);
        labelCell = row.createCell(0);
        labelCell.setCellValue("📈 Total Ingresos:");
        labelCell.setCellStyle(labelStyle);
        cell = row.createCell(1);
        cell.setCellValue(totalSalidas.doubleValue());
        cell.setCellStyle(currencyWithSymbolStyle);

        row = sheet.createRow(rowNum[0]++);
        labelCell = row.createCell(0);
        labelCell.setCellValue("📉 Costo de Venta:");
        labelCell.setCellStyle(labelStyle);
        cell = row.createCell(1);
        cell.setCellValue(totalCostSold.doubleValue());
        cell.setCellStyle(currencyWithSymbolStyle);

        row = sheet.createRow(rowNum[0]++);
        labelCell = row.createCell(0);
        labelCell.setCellValue("💵 Ganancia Bruta:");
        labelCell.setCellStyle(labelStyle);
        cell = row.createCell(1);
        cell.setCellValue(gananciaTotal.doubleValue());
        cell.setCellStyle(currencyWithSymbolStyle);

        row = sheet.createRow(rowNum[0]++);
        labelCell = row.createCell(0);
        labelCell.setCellValue("% Margen de Ganancia:");
        labelCell.setCellStyle(totalStyle);
        cell = row.createCell(1);
        cell.setCellValue(margenPromedio.doubleValue() / 100.0);
        CellStyle percentStyle = createPercentageStyle(workbook);
        cell.setCellStyle(percentStyle);

        rowNum[0]++; // Espacio

        // Top 5 productos
        row = sheet.createRow(rowNum[0]++);
        row.setHeightInPoints(18);
        Cell topCell = row.createCell(0);
        topCell.setCellValue("🏆 TOP 5 PRODUCTOS");
        CellStyle sectionStyle = createSectionHeaderStyle(workbook);
        topCell.setCellStyle(sectionStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum[0]-1, rowNum[0]-1, 0, 2));

        row = sheet.createRow(rowNum[0]++);
        row.setHeightInPoints(16);
        String[] topHeaders = {"Producto", "Cant. Vendida", "Ingresos"};
        for (int i = 0; i < topHeaders.length; i++) {
            Cell hCell = row.createCell(i);
            hCell.setCellValue(topHeaders[i]);
            hCell.setCellStyle(headerStyle);
        }

        Map<String, Integer> productQuantity = new HashMap<>();
        Map<String, BigDecimal> productRevenue = new HashMap<>();

        for (Transaction t : transactions) {
            if ("SALIDA".equalsIgnoreCase(t.getType())) {
                try {
                    Product product = t.getProduct();
                    if (product != null) {
                        String productKey = product.getName();
                        productQuantity.put(productKey, productQuantity.getOrDefault(productKey, 0) + t.getQuantity());
                        BigDecimal price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
                        BigDecimal revenue = price.multiply(new BigDecimal(t.getQuantity()));
                        productRevenue.put(productKey, productRevenue.getOrDefault(productKey, BigDecimal.ZERO).add(revenue));
                    }
                } catch (Exception e) {
                    // Skip product
                }
            }
        }

        int topIndex = 0;
        for (Map.Entry<String, Integer> entry : productQuantity.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .limit(5)
            .collect(Collectors.toList())) {
            Row dataRow = sheet.createRow(rowNum[0]++);
            dataRow.setHeightInPoints(14);
            
            Cell nameCell = dataRow.createCell(0);
            nameCell.setCellValue(entry.getKey());
            addBorders(nameCell);
            
            Cell qtyCell = dataRow.createCell(1);
            qtyCell.setCellValue(entry.getValue());
            addBorders(qtyCell);
            
            Cell revCell = dataRow.createCell(2);
            revCell.setCellValue(productRevenue.getOrDefault(entry.getKey(), BigDecimal.ZERO).doubleValue());
            revCell.setCellStyle(currencyStyle);
            addBorders(revCell);
            topIndex++;
        }

        // Auto-ajustar columnas
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }

    private void createDetailedMovementsSheet(Workbook workbook, List<Transaction> transactions) {
        Sheet sheet = workbook.createSheet("Movimientos Detallados");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);

        int[] rowNum = {0};

        // Encabezados
        Row headerRow = sheet.createRow(rowNum[0]++);
        headerRow.setHeightInPoints(18);
        String[] headers = {"Fecha", "Tipo", "Producto", "Cantidad", "Costo Unit", "Costo Total", 
                          "Precio Unit", "Precio Total", "Ganancia Unit", "Ganancia Total", "Usuario"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Organizar por meses
        Map<YearMonth, List<Transaction>> transactionsByMonth = transactions.stream()
            .collect(Collectors.groupingBy(
                t -> YearMonth.from(t.getDateTime()),
                Collectors.toList()
            ));

        for (YearMonth month : transactionsByMonth.keySet().stream().sorted().collect(Collectors.toList())) {
            List<Transaction> monthTransactions = transactionsByMonth.get(month);
            
            // Separador de mes
            Row monthRow = sheet.createRow(rowNum[0]++);
            monthRow.setHeightInPoints(16);
            monthRow.createCell(0).setCellValue("📅 " + month);
            CellStyle monthStyle = createSectionHeaderStyle(workbook);
            monthRow.getCell(0).setCellStyle(monthStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum[0]-1, rowNum[0]-1, 0, 10));

            for (Transaction t : monthTransactions.stream()
                    .sorted(Comparator.comparing(Transaction::getDateTime))
                    .collect(Collectors.toList())) {
                Product product;
                try {
                    product = productService.findById(t.getProductId());
                } catch (Exception e) {
                    continue;
                }
                if (product == null) continue;

                User user = null;
                try {
                    user = userService.findById(t.getUserId());
                } catch (Exception e) {
                    user = null;
                }

                Row row = sheet.createRow(rowNum[0]++);
                row.setHeightInPoints(14);

                row.createCell(0).setCellValue(t.getDateTime().toLocalDate().toString());
                addBorders(row.getCell(0));

                row.createCell(1).setCellValue(t.getType());
                addBorders(row.getCell(1));

                row.createCell(2).setCellValue(product.getName());
                addBorders(row.getCell(2));

                row.createCell(3).setCellValue(t.getQuantity());
                addBorders(row.getCell(3));

                // Handle null prices/costs
                BigDecimal costUnit = product.getCost() != null ? product.getCost() : BigDecimal.ZERO;
                BigDecimal priceUnit = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
                BigDecimal costTotal = costUnit.multiply(new BigDecimal(t.getQuantity()));
                BigDecimal priceTotal = priceUnit.multiply(new BigDecimal(t.getQuantity()));
                BigDecimal gainUnit = priceUnit.subtract(costUnit);
                BigDecimal gainTotal = "SALIDA".equalsIgnoreCase(t.getType()) 
                    ? gainUnit.multiply(new BigDecimal(t.getQuantity())) 
                    : BigDecimal.ZERO;

                row.createCell(4).setCellValue(costUnit.doubleValue());
                row.getCell(4).setCellStyle(currencyStyle);
                addBorders(row.getCell(4));
                
                row.createCell(5).setCellValue(costTotal.doubleValue());
                row.getCell(5).setCellStyle(currencyStyle);
                addBorders(row.getCell(5));
                
                row.createCell(6).setCellValue(priceUnit.doubleValue());
                row.getCell(6).setCellStyle(currencyStyle);
                addBorders(row.getCell(6));
                
                row.createCell(7).setCellValue(priceTotal.doubleValue());
                row.getCell(7).setCellStyle(currencyStyle);
                addBorders(row.getCell(7));
                
                row.createCell(8).setCellValue(gainUnit.doubleValue());
                row.getCell(8).setCellStyle(currencyStyle);
                addBorders(row.getCell(8));
                
                row.createCell(9).setCellValue(gainTotal.doubleValue());
                row.getCell(9).setCellStyle(currencyStyle);
                addBorders(row.getCell(9));

                row.createCell(10).setCellValue(user != null ? user.getUsername() : "N/A");
                addBorders(row.getCell(10));
            }

            rowNum[0]++; // Espacio entre meses
        }

        // Auto-ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createProductAnalysisSheet(Workbook workbook, List<Transaction> transactions, Long storeId) {
        Sheet sheet = workbook.createSheet("Análisis por Producto");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);

        int[] rowNum = {0};

        // Encabezados
        Row headerRow = sheet.createRow(rowNum[0]++);
        headerRow.setHeightInPoints(18);
        String[] headers = {"Producto", "Cantidad Entrada", "Cantidad Salida", "Stock Actual", 
                          "Costo Invertido", "Ingreso Total", "Ganancia Total", "Ganancia %"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Agrupar por producto
        Map<Long, List<Transaction>> transactionsByProductId = transactions.stream()
            .collect(Collectors.groupingBy(
                t -> t.getProduct().getId(),
                Collectors.toList()
            ));

        boolean altRow = false;
        for (Map.Entry<Long, List<Transaction>> entry : transactionsByProductId.entrySet()) {
            Long productId = entry.getKey();
            List<Transaction> productTransactions = entry.getValue();
            
            if (productTransactions.isEmpty()) continue;
            Product product = productTransactions.get(0).getProduct();
            if (product == null || !product.getStore().getId().equals(storeId)) continue;

            int cantEntrada = productTransactions.stream()
                .filter(t -> "ENTRADA".equalsIgnoreCase(t.getType()))
                .mapToInt(Transaction::getQuantity)
                .sum();

            int cantSalida = productTransactions.stream()
                .filter(t -> "SALIDA".equalsIgnoreCase(t.getType()))
                .mapToInt(Transaction::getQuantity)
                .sum();

            BigDecimal costInvested = productTransactions.stream()
                .filter(t -> "ENTRADA".equalsIgnoreCase(t.getType()))
                .map(t -> {
                    BigDecimal cost = product.getCost() != null ? product.getCost() : BigDecimal.ZERO;
                    return cost.multiply(new BigDecimal(t.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal ingresoTotal = productTransactions.stream()
                .filter(t -> "SALIDA".equalsIgnoreCase(t.getType()))
                .map(t -> {
                    BigDecimal price = product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
                    return price.multiply(new BigDecimal(t.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal costSold = productTransactions.stream()
                .filter(t -> "SALIDA".equalsIgnoreCase(t.getType()))
                .map(t -> {
                    BigDecimal cost = product.getCost() != null ? product.getCost() : BigDecimal.ZERO;
                    return cost.multiply(new BigDecimal(t.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal gananciaTotal = ingresoTotal.subtract(costSold);
            BigDecimal gananciaPercent = ingresoTotal.compareTo(BigDecimal.ZERO) > 0 
                ? gananciaTotal.divide(ingresoTotal, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal(100))
                : BigDecimal.ZERO;

            Row row = sheet.createRow(rowNum[0]++);
            row.setHeightInPoints(14);

            row.createCell(0).setCellValue(product.getName());
            addBorders(row.getCell(0));

            row.createCell(1).setCellValue(cantEntrada);
            addBorders(row.getCell(1));

            row.createCell(2).setCellValue(cantSalida);
            addBorders(row.getCell(2));

            row.createCell(3).setCellValue(product.getStock());
            addBorders(row.getCell(3));
            
            row.createCell(4).setCellValue(costInvested.doubleValue());
            row.getCell(4).setCellStyle(currencyStyle);
            addBorders(row.getCell(4));

            row.createCell(5).setCellValue(ingresoTotal.doubleValue());
            row.getCell(5).setCellStyle(currencyStyle);
            addBorders(row.getCell(5));

            row.createCell(6).setCellValue(gananciaTotal.doubleValue());
            row.getCell(6).setCellStyle(currencyStyle);
            addBorders(row.getCell(6));

            row.createCell(7).setCellValue(gananciaPercent.doubleValue() / 100.0);
            CellStyle pctStyle = workbook.createCellStyle();
            pctStyle.cloneStyleFrom(currencyStyle);
            pctStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
            row.getCell(7).setCellStyle(pctStyle);
            addBorders(row.getCell(7));
        }

        // Auto-ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDailyCashFlowSheet(Workbook workbook, List<Transaction> transactions) {
        Sheet sheet = workbook.createSheet("Flujo Caja Diario");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);

        int rowNum = 0;

        // Encabezados
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.setHeightInPoints(18);
        String[] headers = {"Día", "Entradas", "Salidas", "Ganancia Neta"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Agrupar por día
        Map<LocalDate, List<Transaction>> transactionsByDay = transactions.stream()
            .collect(Collectors.groupingBy(
                t -> t.getDateTime().toLocalDate(),
                TreeMap::new,
                Collectors.toList()
            ));

        for (Map.Entry<LocalDate, List<Transaction>> entry : transactionsByDay.entrySet()) {
            LocalDate day = entry.getKey();
            List<Transaction> dayTransactions = entry.getValue();

            BigDecimal entradas = BigDecimal.ZERO;
            BigDecimal salidas = BigDecimal.ZERO;

            for (Transaction t : dayTransactions) {
                Product product;
                try {
                    product = productService.findById(t.getProductId());
                } catch (Exception e) {
                    continue;
                }
                if (product == null) continue;

                if ("ENTRADA".equalsIgnoreCase(t.getType())) {
                    entradas = entradas.add(product.getCost().multiply(new BigDecimal(t.getQuantity())));
                } else if ("SALIDA".equalsIgnoreCase(t.getType())) {
                    salidas = salidas.add(product.getPrice().multiply(new BigDecimal(t.getQuantity())));
                }
            }

            BigDecimal ganancia = salidas.subtract(entradas);

            Row row = sheet.createRow(rowNum++);
            row.setHeightInPoints(14);

            row.createCell(0).setCellValue(day.toString());
            addBorders(row.getCell(0));
            
            row.createCell(1).setCellValue(entradas.doubleValue());
            row.getCell(1).setCellStyle(currencyStyle);
            addBorders(row.getCell(1));

            row.createCell(2).setCellValue(salidas.doubleValue());
            row.getCell(2).setCellStyle(currencyStyle);
            addBorders(row.getCell(2));

            row.createCell(3).setCellValue(ganancia.doubleValue());
            row.getCell(3).setCellStyle(currencyStyle);
            addBorders(row.getCell(3));
        }

        // Auto-ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createAdministrativeCostsSheet(Workbook workbook, Long storeId, LocalDate dateFrom, LocalDate dateTo) {
        Sheet sheet = workbook.createSheet("Costos Administrativos");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        CellStyle altRowStyle = createAlternateRowStyle(workbook);

        int rowNum = 0;

        // Título
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.setHeightInPoints(25);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("💰 MOVIMIENTOS DE COSTOS ADMINISTRATIVOS");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 4));

        // Período
        Row periodRow = sheet.createRow(rowNum++);
        periodRow.setHeightInPoints(16);
        Cell periodCell = periodRow.createCell(0);
        periodCell.setCellValue(String.format("Período: %s al %s", dateFrom, dateTo));
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 4));
        rowNum++;

        // Encabezados
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.setHeightInPoints(18);
        String[] headers = {"Fecha", "Costo", "Tipo", "Monto Pagado", "Usuario"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Obtener costos administrativos filtrados
        try {
            List<AdministrativeCostMovement> movements = administrativeCostMovementService.findByStoreId(storeId).stream()
                .filter(m -> {
                    try {
                        return m != null && 
                               m.getDateTime() != null &&
                               m.getDateTime().toLocalDate().isAfter(dateFrom.minusDays(1)) &&
                               m.getDateTime().toLocalDate().isBefore(dateTo.plusDays(1));
                    } catch (Exception e) {
                        System.err.println("Error filtrando movimiento: " + e.getMessage());
                        return false;
                    }
                })
                .collect(Collectors.toList());

            // Llenar datos
            BigDecimal totalAmount = BigDecimal.ZERO;
            boolean alternate = false;
            
            for (AdministrativeCostMovement movement : movements) {
                Row dataRow = sheet.createRow(rowNum++);
                dataRow.setHeightInPoints(14);
                CellStyle rowStyle = alternate ? altRowStyle : null;
                
                // Fecha
                Cell dateCell = dataRow.createCell(0);
                dateCell.setCellValue(movement.getDateTime().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                if (rowStyle != null) dateCell.setCellStyle(rowStyle);
                
                // Nombre del costo
                Cell costNameCell = dataRow.createCell(1);
                costNameCell.setCellValue(movement.getAdministrativeCost().getName() != null ? 
                    movement.getAdministrativeCost().getName() : "N/A");
                if (rowStyle != null) costNameCell.setCellStyle(rowStyle);
                
                // Tipo
                Cell typeCell = dataRow.createCell(2);
                typeCell.setCellValue(movement.getType() != null ? movement.getType() : "N/A");
                if (rowStyle != null) typeCell.setCellStyle(rowStyle);
                
                // Monto pagado
                Cell amountCell = dataRow.createCell(3);
                if (movement.getAmountPaid() != null) {
                    amountCell.setCellValue(movement.getAmountPaid().doubleValue());
                    totalAmount = totalAmount.add(movement.getAmountPaid());
                } else {
                    amountCell.setCellValue(0.0);
                }
                amountCell.setCellStyle(currencyStyle);
                
                // Usuario
                Cell userCell = dataRow.createCell(4);
                userCell.setCellValue(movement.getUser() != null && movement.getUser().getUsername() != null ? 
                    movement.getUser().getUsername() : "N/A");
                if (rowStyle != null) userCell.setCellStyle(rowStyle);
                
                alternate = !alternate;
            }

            // Total
            if (!movements.isEmpty()) {
                rowNum++;
                Row totalRow = sheet.createRow(rowNum);
                totalRow.setHeightInPoints(16);
                Cell totalLabelCell = totalRow.createCell(2);
                totalLabelCell.setCellValue("TOTAL:");
                totalLabelCell.setCellStyle(headerStyle);
                
                Cell totalValueCell = totalRow.createCell(3);
                totalValueCell.setCellValue(totalAmount.doubleValue());
                totalValueCell.setCellStyle(currencyStyle);
            } else {
                // Si no hay datos
                Row noDataRow = sheet.createRow(rowNum);
                Cell noDataCell = noDataRow.createCell(0);
                noDataCell.setCellValue("No hay movimientos de costos para este período");
            }

        } catch (Exception e) {
            System.err.println("Error al crear hoja de costos administrativos: " + e.getMessage());
            e.printStackTrace();
            // En caso de error, mostrar mensaje en la hoja
            Row errorRow = sheet.createRow(rowNum);
            Cell errorCell = errorRow.createCell(0);
            errorCell.setCellValue("Error al cargar datos de costos administrativos: " + e.getMessage());
        }

        // Auto-ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // Estilos - LIMPIOS Y CLAROS SIN COLORES
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorders(style);
        return style;
    }

    private CellStyle createTotalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        addBorders(style);
        return style;
    }

    private CellStyle createLabelStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        addBorders(style);
        return style;
    }

    private CellStyle createAlternateRowStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorders(style);
        return style;
    }

    private CellStyle createTransactionStyle(Workbook workbook, String type) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        addBorders(style);
        return style;
    }

    private CellStyle createSectionHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        addBorders(style);
        return style;
    }

    private CellStyle createPercentageStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        addBorders(style);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.##"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        addBorders(style);
        return style;
    }

    private CellStyle createCurrencyWithSymbolStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("\"$\"#,##0.##"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        addBorders(style);
        return style;
    }

    private Font createBoldFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        return font;
    }

    private void addBorders(Cell cell) {
        CellStyle style = cell.getCellStyle();
        if (style == null) {
            style = cell.getSheet().getWorkbook().createCellStyle();
        }
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        cell.setCellStyle(style);
    }

    private void addBorders(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
}
