package com.inventario.licoreria.modules.export;

import com.inventario.licoreria.modules.products.service.ProductService;
import com.inventario.licoreria.modules.products.model.Product;
import com.inventario.licoreria.modules.users.service.UserService;
import com.inventario.licoreria.modules.users.model.User;
import com.inventario.licoreria.modules.inventory.service.TransactionService;
import com.inventario.licoreria.modules.inventory.model.Transaction;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final UserService userService;
    private final ProductService productService;
    private final TransactionService transactionService;

    public ExportController(UserService userService, ProductService productService, TransactionService transactionService) {
        this.userService = userService;
        this.productService = productService;
        this.transactionService = transactionService;
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportToExcel() throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // Hoja de Usuarios
        Sheet userSheet = workbook.createSheet("Usuarios");
        createUserSheet(userSheet, userService.findAllModels());

        // Hoja de Productos
        Sheet productSheet = workbook.createSheet("Productos");
        createProductSheet(productSheet, productService.findAll());

        // Hoja de Transacciones
        Sheet transactionSheet = workbook.createSheet("Transacciones");
        createTransactionSheet(transactionSheet, transactionService.findAll());

        // Escribir a bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "inventario-licoreria.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
    }

    private void createUserSheet(Sheet sheet, List<User> users) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Usuario");
        headerRow.createCell(2).setCellValue("Rol");

        int rowNum = 1;
        for (User user : users) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getUsername());
            row.createCell(2).setCellValue(user.getRole().name());
        }
    }

    private void createProductSheet(Sheet sheet, List<Product> products) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Nombre");
        headerRow.createCell(2).setCellValue("Descripción");
        headerRow.createCell(3).setCellValue("Costo");
        headerRow.createCell(4).setCellValue("Precio");
        headerRow.createCell(5).setCellValue("Stock");

        int rowNum = 1;
        for (Product product : products) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getDescription());
            row.createCell(3).setCellValue(product.getCost().doubleValue());
            row.createCell(4).setCellValue(product.getPrice().doubleValue());
            row.createCell(5).setCellValue(product.getStock());
        }
    }

    private void createTransactionSheet(Sheet sheet, List<Transaction> transactions) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Tipo");
        headerRow.createCell(2).setCellValue("Cantidad");
        headerRow.createCell(3).setCellValue("Producto ID");
        headerRow.createCell(4).setCellValue("Usuario ID");
        headerRow.createCell(5).setCellValue("Fecha");

        int rowNum = 1;
        for (Transaction transaction : transactions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(transaction.getId());
            row.createCell(1).setCellValue(transaction.getType());
            row.createCell(2).setCellValue(transaction.getQuantity());
            row.createCell(3).setCellValue(transaction.getProductId());
            row.createCell(4).setCellValue(transaction.getUserId());
            row.createCell(5).setCellValue(transaction.getDateTime().toString());
        }
    }
}