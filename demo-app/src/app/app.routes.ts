import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { PersonListPageComponent } from './features/person-list/person-list-page.component';
import { CustomerDashboardComponent } from './features/customer-dashboard/customer-dashboard.component';
import { NotFoundPageComponent } from './features/not-found/not-found-page.component';

import { authGuard, adminGuard, guestGuard } from './guards/auth.guard'; 

import { MotorcycleListPageComponent } from './features/motorcycle-list/motorcycle-list-page.component';
import { PartListPageComponent } from './features/part-list/part-list-page.component';
import { AppointmentListPageComponent } from './features/appointment-list/appointment-list-page.component';
import { ForgotPasswordComponent } from './features/forgot-password/forgot-password.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'forgot-password', component: ForgotPasswordComponent, canActivate: [guestGuard] },
  
  { path: 'people', component: PersonListPageComponent, canActivate: [adminGuard] },
  { path: 'motorcycles', component: MotorcycleListPageComponent, canActivate: [adminGuard] },
  { path: 'parts', component: PartListPageComponent, canActivate: [adminGuard] },
  { path: 'appointments', component: AppointmentListPageComponent, canActivate: [adminGuard] },
  
  { path: 'customer-dashboard', component: CustomerDashboardComponent, canActivate: [authGuard] },
  
  { path: '**', component: NotFoundPageComponent }
];