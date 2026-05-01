import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login.component';
import { HomeComponent } from './home/home.component';
import { AuthGuard } from './auth/auth.guard';
import { RegisterComponent } from './auth/register.component';
import { withoutUnsavedChangesGuard } from './common/without-unsaved-changes-guard.spec';
import { LogoutGuard } from './common/logout.guard.spec';
import { MyStoresComponent } from './stores/my-stores.component';
import { CreateStoreComponent } from './stores/create-store.component';
import { EditStoreComponent } from './stores/edit-store.component';
import { DashboardTiendaComponent } from './stores/dashboard-tienda.component';
import { DashboardInfoComponent } from './stores/dashboard-info.component';
import { InventarioComponent } from './stores/inventario/inventario';
import { MovimientosComponent } from './stores/movimientos/movimientos';
import { MainLayoutComponent } from './main-layout.component';
import { SettingsComponent } from './settings/settings.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent, canDeactivate: [withoutUnsavedChangesGuard] },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [AuthGuard],
    canDeactivate: [LogoutGuard],
    children: [
      { path: '', component: HomeComponent },
      { path: 'home', component: HomeComponent },
      { path: 'my-stores', component: MyStoresComponent },
      { path: 'create-store', component: CreateStoreComponent },
      { path: 'edit-store/:id', component: EditStoreComponent },
      { path: 'settings', component: SettingsComponent },
      {
        path: 'tienda/:id',
        children: [
          { path: '', component: DashboardTiendaComponent },
          { path: 'dashboard-info', component: DashboardInfoComponent },
          { path: 'inventario', component: InventarioComponent },
          { path: 'movimientos', component: MovimientosComponent }
        ]
      }
    ]
  },
  { path: '**', redirectTo: '/login' }
];