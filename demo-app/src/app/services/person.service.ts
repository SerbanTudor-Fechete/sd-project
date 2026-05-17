import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CreatePersonDto, Person, UpdatePersonDto } from '../models/person.model';

const API_URL = 'https://localhost:8443/person';

@Injectable({ providedIn: 'root' })
export class PersonService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<Person[]> {
    return this.http.get<Person[]>(API_URL);
  }

  getCustomers(): Observable<Person[]> {
    return this.http.get<Person[]>(`${API_URL}/customers`);
  }

  create(dto: CreatePersonDto): Observable<Person> {
    return this.http.post<Person>(API_URL, dto);
  }

  update(id: string, dto: UpdatePersonDto): Observable<Person> {
    return this.http.put<Person>(`${API_URL}/${id}`, dto);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }
}