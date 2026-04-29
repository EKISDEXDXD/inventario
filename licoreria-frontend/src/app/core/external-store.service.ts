import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ExternalStoreService {
  private openExternalModalSubject = new Subject<void>();
  public openExternalModal$ = this.openExternalModalSubject.asObservable();

  triggerOpenExternalModal() {
    this.openExternalModalSubject.next();
  }
}
