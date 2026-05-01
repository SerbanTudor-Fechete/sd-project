import { inject, Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { CustomerDashboardService } from '../../services/customer-dashboard.service';
import { CustomerAppointment } from '../../models/customer-appointment.model';

@Injectable({ providedIn: 'root' })
export class CustomerDashboardStore {
  private readonly service = inject(CustomerDashboardService);

  readonly appointments = signal<CustomerAppointment[]>([]);
  readonly isLoading = signal<boolean>(false);
  readonly errorMessage = signal<string | null>(null);

  loadAppointments(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.service.getMyAppointments()
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe({
        next: (data) => this.appointments.set(data),
        error: (err) => {
          console.error('Failed to fetch appointments:', err);
          this.errorMessage.set('Failed to load your appointments. Please try again later.');
        }
      });
  }
}