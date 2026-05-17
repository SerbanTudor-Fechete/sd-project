import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CustomerAppointment } from '../models/customer-appointment.model';

@Injectable({ providedIn: 'root' })
export class CustomerDashboardService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'https://localhost:8443/appointment'; 

  getMyAppointments(): Observable<CustomerAppointment[]> {
    return this.http.get<CustomerAppointment[]>(`${this.apiUrl}/my-appointments`);
  }

  downloadInvoice(appointmentId: number | string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${appointmentId}/invoice`, {
      responseType: 'blob' 
    });
  }

}