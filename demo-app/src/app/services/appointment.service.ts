import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateAppointmentDto, ServiceAppointment, UpdateAppointmentDto } from '../models/appointment.model';

const API_URL = 'http://localhost:8080/appointment'; 

@Injectable({ providedIn: 'root' })
export class AppointmentService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<ServiceAppointment[]> {
    return this.http.get<ServiceAppointment[]>(API_URL);
  }

  create(dto: CreateAppointmentDto): Observable<ServiceAppointment> {
    return this.http.post<ServiceAppointment>(API_URL, dto);
  }

  update(id: string, dto: UpdateAppointmentDto): Observable<ServiceAppointment> {
    return this.http.put<ServiceAppointment>(`${API_URL}/${id}`, dto);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}