import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';

import { LoginStore } from '../login/login.store';
import { CustomerDashboardStore } from './customer-dashboard.store';

export interface CustomerAppointment {
  id: number;
  appointmentDate: string; 
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  totalCost: number;
  motorcycle: {
    brand: string;
    model: string;
    licensePlate: string;
  };
}

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule, 
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatDividerModule
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './customer-dashboard.component.html',
  styleUrl: './customer-dashboard.component.scss' 
})

export class CustomerDashboardComponent implements OnInit {
  private readonly router = inject(Router);
  private readonly loginStore = inject(LoginStore);
  
  protected readonly dashboardStore = inject(CustomerDashboardStore); 

  ngOnInit(): void {
    this.dashboardStore.loadAppointments();
  }

  protected logout(): void {
    this.loginStore.logout(); 
    void this.router.navigate(['/login']);
  }
}