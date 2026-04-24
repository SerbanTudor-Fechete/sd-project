import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreatePartDto, Part, UpdatePartDto } from '../models/part.model';

const API_URL = 'http://localhost:8080/part'; 

@Injectable({ providedIn: 'root' })
export class PartService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<Part[]> {
    return this.http.get<Part[]>(API_URL);
  }

  create(dto: CreatePartDto): Observable<Part> {
    return this.http.post<Part>(API_URL, dto);
  }

  update(uuid: string, dto: UpdatePartDto): Observable<Part> {
    return this.http.put<Part>(`${API_URL}/${uuid}`, dto);
  }

  delete(uuid: string): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${uuid}`);
  }
}