import { computed, inject, Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { Motorcycle, CreateMotorcycleDto, UpdateMotorcycleDto } from '../../models/motorcycle.model';
import { MotorcycleService } from '../../services/motorcycle.service';

@Injectable({ providedIn: 'root' })
export class MotorcycleListStore {
  private readonly service = inject(MotorcycleService);
  private readonly pendingRequests = signal(0);

  readonly motorcycles = signal<Motorcycle[]>([]);
  readonly hasError = signal(false);
  readonly errorMessage = signal('');
  readonly isLoading = computed(() => this.pendingRequests() > 0);

  readonly filterBrand = signal('');
  readonly filterYear = signal('');
  readonly filterModel = signal('');
  readonly sortBy = signal('brand');

  readonly filteredMotorcycles = computed(() => {
    let filtered = [...this.motorcycles()]; 
    
    const brandTerm = this.filterBrand().toLowerCase().trim();
    const yearTerm = this.filterYear().trim();
    const modelTerm = this.filterModel().toLowerCase().trim();

    if (brandTerm) {
      filtered = filtered.filter(m => m.brand.toLowerCase().includes(brandTerm));
    }
    if (modelTerm) {
      filtered = filtered.filter(m => m.model.toLowerCase() === modelTerm);
    }
    if (yearTerm) {
      filtered = filtered.filter(m => m.manufactureYear === yearTerm);
    }

    const sortProp = this.sortBy() as keyof Motorcycle;
    filtered.sort((a, b) => {
      const valA = String(a[sortProp]).toLowerCase();
      const valB = String(b[sortProp]).toLowerCase();
      return valA < valB ? -1 : valA > valB ? 1 : 0;
    });

    return filtered;
  });
  
  private beginRequest(): void {
    this.pendingRequests.update(count => count + 1);
  }

  private endRequest(): void {
    this.pendingRequests.update(count => Math.max(0, count - 1));
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
    this.errorMessage.set('An error occurred while saving the motorcycle.');
  }

  applyFilters(brand: string, model: string, year: string, sortBy: string): void {
    this.filterBrand.set(brand);
    this.filterModel.set(model); 
    this.filterYear.set(year);
    this.sortBy.set(sortBy);
  }

  load(): void {
    this.hasError.set(false);
    this.errorMessage.set('');
    this.beginRequest();
    this.service.getAll().pipe(finalize(() => this.endRequest())).subscribe({
      next: (data) => this.motorcycles.set(data),
      error: () => this.hasError.set(true)
    });
  }

  create(dto: CreateMotorcycleDto): void {
    this.hasError.set(false);
    this.errorMessage.set('');
    this.beginRequest();
    this.service.create(dto).pipe(finalize(() => this.endRequest())).subscribe({
      next: (created) => this.motorcycles.update(list => [...list, created]),
      error: (err: HttpErrorResponse) => this.handleBackendError(err)
    });
  }

  update(id: string, dto: UpdateMotorcycleDto): void {
    this.hasError.set(false);
    this.beginRequest();
    this.service.update(id, dto).pipe(finalize(() => this.endRequest())).subscribe({
      next: (updated) => this.motorcycles.update(list => list.map(m => m.id === updated.id ? updated : m)),
      error: (err: HttpErrorResponse) => this.handleBackendError(err)
    });
  }

  remove(id: string): void {
    this.hasError.set(false);
    this.beginRequest();
    this.service.delete(id).pipe(finalize(() => this.endRequest())).subscribe({
      next: () => this.motorcycles.update(list => list.filter(m => m.id !== id)),
      error: () => this.hasError.set(true)
    });
  }
}