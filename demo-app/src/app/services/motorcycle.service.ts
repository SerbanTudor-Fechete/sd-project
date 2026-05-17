import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreateMotorcycleDto, Motorcycle, UpdateMotorcycleDto } from '../models/motorcycle.model';

const API_URL = 'https://localhost:8443/motorcycle';

@Injectable({ providedIn: 'root' })
export class MotorcycleService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<Motorcycle[]> {
    return this.http.get<Motorcycle[]>(API_URL);
  }

  create(dto: CreateMotorcycleDto): Observable<Motorcycle> {
    return this.http.post<Motorcycle>(API_URL, dto);
  }

  update(id: string, dto: UpdateMotorcycleDto): Observable<Motorcycle> {
    return this.http.put<Motorcycle>(`${API_URL}/${id}`, dto);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}