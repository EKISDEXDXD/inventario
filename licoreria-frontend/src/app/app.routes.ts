import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login.component';
import { HomeComponent } from './home/home.component';
import { AuthGuard } from './auth/auth.guard';
import { RegisterComponent } from './auth/register.component';
import { withoutUnsavedChangesGuard } from './common/without-unsaved-changes-guard.spec';
import { LogoutGuard } from './common/logout.guard.spec';
import { MyStoresComponent } from './stores/my-stores.component';
import { CreateStoreComponent } from './stores/create-store.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent, canDeactivate: [withoutUnsavedChangesGuard] },
  { path: '', component: HomeComponent, canActivate: [AuthGuard], canDeactivate: [LogoutGuard] },
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard], canDeactivate: [LogoutGuard] },
  { path: 'my-stores', component: MyStoresComponent, canActivate: [AuthGuard] },
  { path: 'create-store', component: CreateStoreComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '/login' }
];