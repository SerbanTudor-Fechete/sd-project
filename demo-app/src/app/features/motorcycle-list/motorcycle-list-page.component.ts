import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';

import { ConfirmDeleteDialogComponent } from '../../components/confirm-delete-dialog/confirm-delete-dialog.component';
import { MotorcycleFormDialogComponent, MotorcycleFormDialogData, MotorcycleFormDialogResult } from '../../components/motorcycle-form-dialog/motorcycle-form-dialog.component';
import { CreateMotorcycleDto, Motorcycle, UpdateMotorcycleDto } from '../../models/motorcycle.model';
import { Person } from '../../models/person.model';
import { PersonService } from '../../services/person.service';
import { MotorcycleListStore } from './motorcycle-list.store';

@Component({
  selector: 'app-motorcycle-list-page',
  standalone: true,
  imports: [
    MatTableModule, MatButtonModule, MatIconModule, MatDialogModule,
    MatCardModule, MatFormFieldModule, MatInputModule, MatSelectModule, ReactiveFormsModule
  ],
  templateUrl: './motorcycle-list-page.component.html',
  styleUrl: './motorcycle-list-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MotorcycleListPageComponent {
  private readonly dialog = inject(MatDialog);
  private readonly store = inject(MotorcycleListStore);
  private readonly personService = inject(PersonService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly fb = inject(FormBuilder);

  protected readonly filteredMotorcycles = this.store.filteredMotorcycles; 
  protected readonly hasError = this.store.hasError;
  protected readonly isLoading = this.store.isLoading;
  protected readonly errorMessage = this.store.errorMessage;
  protected readonly displayedColumns = ['brand', 'model', 'manufactureYear', 'licensePlate', 'actions'];

  protected readonly owners = signal<Person[]>([]);

  // 1.0p FRONTEND FILTER FORM
  protected readonly filterForm = this.fb.nonNullable.group({
    brand: [''],
    year: [''],
    model: [''],
    sortBy: ['brand']
  });

  constructor() {
    this.store.load();
    this.personService.getCustomers().pipe(takeUntilDestroyed()).subscribe(data => this.owners.set(data));
  }

  protected applyFilters(): void {
    const { brand, model, year, sortBy } = this.filterForm.getRawValue();
    this.store.applyFilters(brand, model, year, sortBy);
  }

  protected clearFilters(): void {
  this.filterForm.reset({ brand: '', model: '', year: '', sortBy: 'brand' });
  this.applyFilters();
  }

  protected openCreateDialog(): void {
    if (this.isLoading()) return;

    this.dialog.open<MotorcycleFormDialogComponent, MotorcycleFormDialogData, MotorcycleFormDialogResult>(
        MotorcycleFormDialogComponent,
        { data: { title: 'Create Motorcycle', submitLabel: 'Create', owners: this.owners() } }
      )
      .afterClosed().pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(result => {
        if (!result) return;
        this.store.create(result as CreateMotorcycleDto);
      });
  }

  protected openEditDialog(motorcycle: Motorcycle): void {
    if (this.isLoading()) return;

    this.dialog.open<MotorcycleFormDialogComponent, MotorcycleFormDialogData, MotorcycleFormDialogResult>(
        MotorcycleFormDialogComponent,
        { data: { title: 'Edit Motorcycle', submitLabel: 'Save', owners: this.owners(), initialValue: motorcycle } }
      )
      .afterClosed().pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(result => {
        if (!result) return;
        this.store.update(motorcycle.id, result as UpdateMotorcycleDto);
      });
  }

  protected openDeleteDialog(motorcycle: Motorcycle): void {
    if (this.isLoading()) return;

    this.dialog.open<ConfirmDeleteDialogComponent, { item: Motorcycle }, boolean>(
        ConfirmDeleteDialogComponent,
        { data: { item: motorcycle } }
      )
      .afterClosed().pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(confirmed => {
        if (!confirmed) return;
        this.store.remove(motorcycle.id);
      });
  }
}