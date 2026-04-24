import { computed, inject, Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { AppointmentService } from '../../services/appointment.service';
import { CreateAppointmentDto, ServiceAppointment, UpdateAppointmentDto } from '../../models/appointment.model';

@Injectable({ providedIn: 'root' })
export class AppointmentListStore {
  private readonly service = inject(AppointmentService);
  private readonly pendingRequests = signal(0);

  readonly appointments = signal<ServiceAppointment[]>([]);
  readonly hasError = signal(false);
  readonly errorMessage = signal('');
  readonly isLoading = computed(() => this.pendingRequests() > 0);

  private beginRequest(): void { this.pendingRequests.update(c => c + 1); }
  private endRequest(): void { this.pendingRequests.update(c => Math.max(0, c - 1)); }

  private handleBackendError(error: HttpErrorResponse): void {
    this.hasError.set(true);
    if (error.status === 400 && error.error && typeof error.error === 'object') {
      const validationMessages = Object.values(error.error);
      if (validationMessages.length > 0) {
        this.errorMessage.set(validationMessages.join(' • ')); 
        return;
      }
    }
    this.errorMessage.set('An error occurred while saving the appointment.');
  }

  load(): void {
    this.hasError.set(false);
    this.errorMessage.set('');
    this.beginRequest();
    this.service.getAll().pipe(finalize(() => this.endRequest())).subscribe({
      next: (data) => this.appointments.set(data),
      error: () => this.hasError.set(true)
    });
  }

  create(dto: CreateAppointmentDto): void {
    this.hasError.set(false);
    this.errorMessage.set('');
    this.beginRequest();
    this.service.create(dto).pipe(finalize(() => this.endRequest())).subscribe({
      next: (created) => this.appointments.update(list => [...list, created]),
      error: (err) => this.handleBackendError(err)
    });
  }

  update(id: string, dto: UpdateAppointmentDto): void {
    this.hasError.set(false);
    this.beginRequest();
    this.service.update(id, dto).pipe(finalize(() => this.endRequest())).subscribe({
      next: (updated) => this.appointments.update(list => list.map(a => a.id === updated.id ? updated : a)),
      error: (err) => this.handleBackendError(err)
    });
  }

  remove(id: string): void {
    this.hasError.set(false);
    this.beginRequest();
    this.service.delete(id).pipe(finalize(() => this.endRequest())).subscribe({
      next: () => this.appointments.update(list => list.filter(a => a.id !== id)),
      error: () => this.hasError.set(true)
    });
  }
}