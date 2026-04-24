import { computed, inject, Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { CreatePartDto, Part, UpdatePartDto } from '../../models/part.model';
import { PartService } from '../../services/part.service';

@Injectable({ providedIn: 'root' })
export class PartListStore {
  private readonly partService = inject(PartService);
  private readonly pendingRequests = signal(0);

  readonly parts = signal<Part[]>([]);
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
    
    this.errorMessage.set('An error occurred while saving the part.');
  }

  load(): void {
    this.hasError.set(false);
    this.errorMessage.set('');
    this.beginRequest();
    this.partService
      .getAll()
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (data) => this.parts.set(data),
        error: () => this.hasError.set(true),
      });
  }

  create(dto: CreatePartDto): void {
    this.hasError.set(false);
    this.errorMessage.set('');
    this.beginRequest();
    this.partService
      .create(dto)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (created) => this.parts.update((list) => [...list, created]),
        error: (err: HttpErrorResponse) => this.handleBackendError(err),
      });
  }

  update(uuid: string, dto: UpdatePartDto): void {
    this.hasError.set(false);
    this.beginRequest();
    this.partService
      .update(uuid, dto)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (updated) =>
          this.parts.update((list) =>
            list.map((part) => (part.id === updated.id ? updated : part)),
          ),
        error: (err: HttpErrorResponse) => this.handleBackendError(err),
      });
  }

  remove(uuid: string): void {
    this.hasError.set(false);
    this.beginRequest();
    this.partService
      .delete(uuid)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: () =>
          this.parts.update((list) => list.filter((part) => part.id !== uuid)),
        error: () => this.hasError.set(true),
      });
  }
}