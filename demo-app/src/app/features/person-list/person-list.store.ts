import { computed, inject, Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { CreatePersonDto, Person, UpdatePersonDto } from '../../models/person.model';
import { PersonService } from '../../services/person.service';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class PersonListStore {
  private readonly personService = inject(PersonService);
  private readonly pendingRequests = signal(0);

  readonly persons = signal<Person[]>([]);
  readonly hasError = signal(false);
  readonly errorMessage = signal('');
  readonly isLoading = computed(() => this.pendingRequests() > 0);

  private beginRequest(): void {
    this.pendingRequests.update((count) => count + 1);
  }

  private endRequest(): void {
    this.pendingRequests.update((count) => Math.max(0, count - 1));
  }

  private handleBackendError(error: HttpErrorResponse): void {
    this.hasError.set(true);
    
    if (error.status === 400 && error.error && typeof error.error === 'object') {
      const validationMessages = Object.values(error.error);
      if (validationMessages.length > 0) {
        this.errorMessage.set(validationMessages.join(' • ')); 
        return;
      }
    }
    
    this.errorMessage.set('An error occurred while saving.');
  }

  load(): void {
    this.hasError.set(false);
    this.errorMessage.set('');
    this.beginRequest();
    this.personService
      .getAll()
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (data) => this.persons.set(data),
        error: () => this.hasError.set(true),
      });
  }

  create(dto: CreatePersonDto): void {
    this.hasError.set(false);
    this.errorMessage.set('');
    this.beginRequest();
    this.personService
      .create(dto)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (created) => this.persons.update((list) => [...list, created]),
        error: (err: HttpErrorResponse) => this.handleBackendError(err),
      });
  }

  update(id: string, dto: UpdatePersonDto): void {
    const existing = this.persons().find((p) => p.id === id);
    if (!existing) return;

    const payload: CreatePersonDto = { 
        ...dto, 
        password: existing.password,
        email: existing.email,
        role: existing.role 
    };

    this.hasError.set(false);
    this.beginRequest();
    this.personService
      .update(id, payload)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (updated) =>
          this.persons.update((list) =>
            list.map((person) => (person.id === updated.id ? updated : person)),
          ),
        error: () => this.hasError.set(true),
      });
  }

  remove(id: string): void {
    this.hasError.set(false);
    this.beginRequest();
    this.personService
      .delete(id)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: () =>
          this.persons.update((list) => list.filter((person) => person.id !== id)),
        error: () => this.hasError.set(true),
      });
  }
}