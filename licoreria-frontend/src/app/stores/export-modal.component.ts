import { Component, Inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { formatDate } from '@angular/common';

interface ExportOption {
  type: 'COMPLETE' | 'SIMPLE';
  label: string;
  description: string;
  icon: string;
}

interface ExportHistory {
  id: string;
  fileName: string;
  dateGenerated: string;
  period: string;
  reportType: string;
  downloadUrl: string;
}

@Component({
  selector: 'app-export-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="export-modal">
      <div class="modal-header">
        <h2>📊 Exportar Reportes de Ventas</h2>
        <button class="close-btn" (click)="onCancel()">✕</button>
      </div>

      <!-- SECCIÓN 1: INFORMACIÓN -->
      <div class="info-section">
        <h3>ℹ️ Información de Reportes</h3>
        <p>
          <strong>Reporte Experto Completo:</strong> Incluye 4 hojas (Resumen, Movimientos Detallados, Análisis por Producto y Flujo de Caja). Ideal para análisis profundo.
        </p>
        <p>
          <strong>Reporte Resumido:</strong> Incluye 2 hojas (Resumen Ejecutivo y Movimientos Detallados). Perfecto para verificación rápida.
        </p>
      </div>

      <div class="divider"></div>

      <!-- SECCIÓN 2: OPCIONES DE EXPORTACIÓN -->
      <div class="options-section">
        <h3>📋 Opciones de Exportación</h3>

        <!-- OPCIÓN 1: REPORTE COMPLETO -->
        <div class="export-option-card">
          <div class="option-header">
            <h4>📋 Reporte Experto Completo</h4>
            <p class="option-desc">4 hojas completas con análisis detallado</p>
          </div>

          <div class="period-selector">
            <label>
              <input type="radio" name="complete-period" value="30days" [(ngModel)]="completeReportPeriod" (change)="onPeriodChange('complete')">
              Últimos 30 días
            </label>
            <label>
              <input type="radio" name="complete-period" value="custom" [(ngModel)]="completeReportPeriod" (change)="onPeriodChange('complete')">
              Personalizar período
            </label>
          </div>

          <div *ngIf="completeReportPeriod === 'custom'" class="date-picker-group">
            <div class="date-input">
              <label>Desde:</label>
              <input type="date" [(ngModel)]="completeFromDate">
            </div>
            <div class="date-input">
              <label>Hasta:</label>
              <input type="date" [(ngModel)]="completeToDate">
            </div>
          </div>

          <button 
            class="download-btn primary"
            (click)="downloadReport('COMPLETE')"
            [disabled]="isLoading">
            <span *ngIf="!isLoading">💾 Descargar Completo</span>
            <span *ngIf="isLoading">⏳ Generando...</span>
          </button>
        </div>

        <!-- OPCIÓN 2: REPORTE RESUMIDO -->
        <div class="export-option-card">
          <div class="option-header">
            <h4>📈 Reporte Resumido</h4>
            <p class="option-desc">2 hojas: Resumen y Movimientos</p>
          </div>

          <div class="period-selector">
            <label>
              <input type="radio" name="simple-period" value="30days" [(ngModel)]="simpleReportPeriod" (change)="onPeriodChange('simple')">
              Últimos 30 días
            </label>
            <label>
              <input type="radio" name="simple-period" value="custom" [(ngModel)]="simpleReportPeriod" (change)="onPeriodChange('simple')">
              Personalizar período
            </label>
          </div>

          <div *ngIf="simpleReportPeriod === 'custom'" class="date-picker-group">
            <div class="date-input">
              <label>Desde:</label>
              <input type="date" [(ngModel)]="simpleFromDate">
            </div>
            <div class="date-input">
              <label>Hasta:</label>
              <input type="date" [(ngModel)]="simpleToDate">
            </div>
          </div>

          <button 
            class="download-btn primary"
            (click)="downloadReport('SIMPLE')"
            [disabled]="isLoading">
            <span *ngIf="!isLoading">💾 Descargar Resumido</span>
            <span *ngIf="isLoading">⏳ Generando...</span>
          </button>
        </div>

        <!-- OPCIÓN 3: HISTORIAL -->
        <div class="export-option-card">
          <div class="option-header">
            <h4>📂 Historial de Exportaciones</h4>
            <p class="option-desc">Accede a reportes de meses anteriores</p>
          </div>

          <div *ngIf="exportHistory.length === 0" class="no-history">
            <p>No hay reportes generados aún</p>
          </div>

          <div *ngIf="exportHistory.length > 0" class="history-table">
            <table>
              <thead>
                <tr>
                  <th>Período</th>
                  <th>Fecha Generación</th>
                  <th>Tipo</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let item of exportHistory">
                  <td>{{ item.period }}</td>
                  <td>{{ item.dateGenerated | date: 'dd/MM/yyyy HH:mm' }}</td>
                  <td>
                    <span class="badge" [ngClass]="item.reportType === 'COMPLETE' ? 'complete' : 'simple'">
                      {{ item.reportType === 'COMPLETE' ? '📋 Completo' : '📈 Resumido' }}
                    </span>
                  </td>
                  <td class="actions">
                    <button class="action-btn download" (click)="redownloadReport(item)" title="Descargar">↓</button>
                    <button class="action-btn delete" (click)="deleteReport(item)" title="Eliminar">✕</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- Footer -->
      <div class="modal-footer">
        <button class="btn-secondary" (click)="onCancel()">Cerrar</button>
      </div>

      <!-- Error Message -->
      <div *ngIf="errorMessage" class="error-message">
        <p>❌ {{ errorMessage }}</p>
      </div>

      <!-- Success Message -->
      <div *ngIf="successMessage" class="success-message">
        <p>✅ {{ successMessage }}</p>
      </div>
    </div>
  `,
  styles: [`
    .export-modal {
      width: 100%;
      max-width: 900px;
      background: white;
      border-radius: 12px;
      overflow-y: auto;
      max-height: 90vh;
    }

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1.5rem;
      border-bottom: 2px solid #e0e0e0;
      background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
      color: white;
    }

    .modal-header h2 {
      margin: 0;
      font-size: 1.5rem;
    }

    .close-btn {
      background: none;
      border: none;
      font-size: 1.5rem;
      cursor: pointer;
      color: white;
      padding: 0;
      transition: transform 0.2s;
    }

    .close-btn:hover {
      transform: scale(1.2);
    }

    .info-section {
      padding: 1.5rem;
      background-color: #f5f5f5;
      border-bottom: 1px solid #e0e0e0;
    }

    .info-section h3 {
      margin-top: 0;
      color: #333;
    }

    .info-section p {
      margin: 0.5rem 0;
      color: #666;
      font-size: 0.95rem;
      line-height: 1.5;
    }

    .divider {
      height: 2px;
      background: linear-gradient(90deg, transparent, #ddd, transparent);
    }

    .options-section {
      padding: 1.5rem;
    }

    .options-section h3 {
      margin-top: 0;
      color: #333;
    }

    .export-option-card {
      background: white;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      padding: 1.5rem;
      margin-bottom: 1.5rem;
      transition: all 0.3s;
    }

    .export-option-card:hover {
      border-color: #6366f1;
      box-shadow: 0 4px 12px rgba(99, 102, 241, 0.1);
    }

    .option-header h4 {
      margin: 0 0 0.5rem 0;
      color: #333;
      font-size: 1.1rem;
    }

    .option-desc {
      margin: 0;
      color: #999;
      font-size: 0.9rem;
    }

    .period-selector {
      margin: 1rem 0;
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .period-selector label {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      cursor: pointer;
      color: #666;
    }

    .period-selector input[type="radio"] {
      cursor: pointer;
    }

    .date-picker-group {
      display: flex;
      gap: 1rem;
      margin: 1rem 0;
      padding: 1rem;
      background: #f9f9f9;
      border-radius: 6px;
    }

    .date-input {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 0.3rem;
    }

    .date-input label {
      font-size: 0.9rem;
      color: #666;
      font-weight: 500;
    }

    .date-input input {
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 0.9rem;
    }

    .download-btn {
      padding: 0.75rem 1.5rem;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      font-weight: 600;
      transition: all 0.3s;
      width: 100%;
    }

    .download-btn.primary {
      background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
      color: white;
    }

    .download-btn.primary:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 6px 12px rgba(99, 102, 241, 0.3);
    }

    .download-btn:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }

    .no-history {
      text-align: center;
      padding: 2rem;
      color: #999;
    }

    .history-table {
      overflow-x: auto;
    }

    table {
      width: 100%;
      border-collapse: collapse;
    }

    thead {
      background: #f5f5f5;
    }

    th {
      padding: 0.75rem;
      text-align: left;
      font-weight: 600;
      color: #333;
      border-bottom: 2px solid #e0e0e0;
    }

    td {
      padding: 0.75rem;
      border-bottom: 1px solid #e0e0e0;
      color: #666;
    }

    tr:hover {
      background: #f9f9f9;
    }

    .badge {
      display: inline-block;
      padding: 0.25rem 0.75rem;
      border-radius: 12px;
      font-size: 0.85rem;
      font-weight: 500;
    }

    .badge.complete {
      background: #dbeafe;
      color: #0369a1;
    }

    .badge.simple {
      background: #dcfce7;
      color: #15803d;
    }

    .actions {
      display: flex;
      gap: 0.5rem;
    }

    .action-btn {
      padding: 0.4rem 0.6rem;
      border: 1px solid #ddd;
      background: white;
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.2s;
      font-size: 0.9rem;
    }

    .action-btn.download:hover {
      background: #e8f4f8;
      border-color: #0369a1;
    }

    .action-btn.delete:hover {
      background: #fee2e2;
      border-color: #991b1b;
    }

    .modal-footer {
      padding: 1.5rem;
      border-top: 1px solid #e0e0e0;
      display: flex;
      gap: 1rem;
      justify-content: flex-end;
    }

    .btn-secondary {
      padding: 0.75rem 1.5rem;
      background: #f0f0f0;
      border: 1px solid #ddd;
      border-radius: 6px;
      cursor: pointer;
      font-weight: 600;
      transition: all 0.3s;
    }

    .btn-secondary:hover {
      background: #e0e0e0;
    }

    .error-message {
      margin: 1rem;
      padding: 1rem;
      background: #fee2e2;
      border-left: 4px solid #dc2626;
      border-radius: 4px;
      color: #991b1b;
    }

    .success-message {
      margin: 1rem;
      padding: 1rem;
      background: #dcfce7;
      border-left: 4px solid #16a34a;
      border-radius: 4px;
      color: #15803d;
    }
  `]
})
export class ExportModalComponent implements OnInit {
  storeId: number = 0;
  isLoading: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  // Complete Report
  completeReportPeriod: string = '30days';
  completeFromDate: string = '';
  completeToDate: string = '';

  // Simple Report
  simpleReportPeriod: string = '30days';
  simpleFromDate: string = '';
  simpleToDate: string = '';

  // History
  exportHistory: ExportHistory[] = [];

  constructor(
    public dialogRef: MatDialogRef<ExportModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { storeId: number },
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {
    this.storeId = data.storeId;
  }

  ngOnInit(): void {
    this.initializeDates();
    this.loadExportHistory();
  }

  initializeDates(): void {
    const today = new Date();
    const thirtyDaysAgo = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000);

    this.completeToDate = this.formatDateForInput(today);
    this.completeFromDate = this.formatDateForInput(thirtyDaysAgo);

    this.simpleToDate = this.formatDateForInput(today);
    this.simpleFromDate = this.formatDateForInput(thirtyDaysAgo);
  }

  formatDateForInput(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  onPeriodChange(reportType: string): void {
    if (reportType === 'complete' && this.completeReportPeriod === '30days') {
      this.initializeDates();
    } else if (reportType === 'simple' && this.simpleReportPeriod === '30days') {
      this.initializeDates();
    }
  }

  downloadReport(reportType: 'COMPLETE' | 'SIMPLE'): void {
    this.errorMessage = '';
    this.successMessage = '';

    const fromDate = reportType === 'COMPLETE' ? this.completeFromDate : this.simpleFromDate;
    const toDate = reportType === 'COMPLETE' ? this.completeToDate : this.simpleToDate;

    if (!this.validateDates(fromDate, toDate)) {
      this.errorMessage = 'Por favor, selecciona un rango de fechas válido';
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'Token no encontrado. Por favor, inicia sesión nuevamente.';
      console.error('Token no encontrado en localStorage');
      return;
    }

    this.isLoading = true;

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    const params = {
      storeId: this.storeId.toString(),
      dateFrom: fromDate,
      dateTo: toDate,
      reportType: reportType
    };

    console.log('Enviando solicitud de exportación:', { 
      url: 'http://localhost:8081/api/export/sales-report',
      params,
      hasToken: !!token
    });

    this.http.post('http://localhost:8081/api/export/sales-report', {}, {
      headers,
      params,
      responseType: 'blob'
    }).subscribe({
      next: (blob) => {
        this.handleDownload(blob, reportType, fromDate, toDate);
        this.isLoading = false;
        this.successMessage = '✅ Reporte descargado exitosamente';
        this.cdr.detectChanges();
        setTimeout(() => this.loadExportHistory(), 500);
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error descargando reporte:', err);
        
        if (err.status === 403) {
          this.errorMessage = 'Acceso denegado (403). Verifica tus permisos.';
        } else if (err.status === 401) {
          this.errorMessage = 'No autenticado. Por favor, inicia sesión nuevamente.';
        } else if (err.status === 400) {
          this.errorMessage = 'Parámetros inválidos. Verifica las fechas.';
        } else {
          this.errorMessage = `Error al generar el reporte (${err.status}). Intenta nuevamente.`;
        }
        this.cdr.detectChanges();
      }
    });
  }

  handleDownload(blob: Blob, reportType: string, dateFrom: string, dateTo: string): void {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `reporte-ventas-${reportType}-${new Date().toISOString().split('T')[0]}.xlsx`;
    link.click();
    window.URL.revokeObjectURL(url);
  }

  validateDates(fromDate: string, toDate: string): boolean {
    if (!fromDate || !toDate) return false;
    return new Date(fromDate) <= new Date(toDate);
  }

  loadExportHistory(): void {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get<ExportHistory[]>(`http://localhost:8081/api/export/history?storeId=${this.storeId}`, { headers })
      .subscribe({
        next: (history) => {
          this.exportHistory = history;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error cargando historial:', err);
        }
      });
  }

  redownloadReport(item: ExportHistory): void {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.get(`http://localhost:8081/api/export/download/${item.id}`, {
      headers,
      responseType: 'blob'
    }).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = item.fileName;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        this.errorMessage = 'Error al descargar el archivo';
        console.error('Error:', err);
      }
    });
  }

  deleteReport(item: ExportHistory): void {
    if (!confirm('¿Estás seguro de que deseas eliminar este reporte?')) return;

    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.delete(`http://localhost:8081/api/export/${item.id}`, { headers })
      .subscribe({
        next: () => {
          this.successMessage = 'Reporte eliminado';
          this.cdr.detectChanges();
          this.loadExportHistory();
        },
        error: (err) => {
          this.errorMessage = 'Error al eliminar el reporte';
          console.error('Error:', err);
          this.cdr.detectChanges();
        }
      });
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
