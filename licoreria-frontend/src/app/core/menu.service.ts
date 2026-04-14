import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private isMenuOpenSubject = new BehaviorSubject<boolean>(false);
  isMenuOpen$ = this.isMenuOpenSubject.asObservable();

  constructor() {}

  toggleMenu(): void {
    this.isMenuOpenSubject.next(!this.isMenuOpenSubject.value);
  }

  closeMenu(): void {
    this.isMenuOpenSubject.next(false);
  }

  openMenu(): void {
    this.isMenuOpenSubject.next(true);
  }

  isMenuOpen(): boolean {
    return this.isMenuOpenSubject.value;
  }
}
