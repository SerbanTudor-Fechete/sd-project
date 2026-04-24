import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';

import { ConfirmDeleteDialogComponent } from '../../components/confirm-delete-dialog/confirm-delete-dialog.component';
import { AppointmentFormDialogComponent, AppointmentFormDialogData, AppointmentFormDialogResult } from '../../components/appointment-form-dialog/appointment-form-dialog.component';
import { CreateAppointmentDto, ServiceAppointment, UpdateAppointmentDto } from '../../models/appointment.model';
import { AppointmentListStore } from './appointment-list.store';

import { Motorcycle } from '../../models/motorcycle.model';
import { Part } from '../../models/part.model';
import { MotorcycleService } from '../../services/motorcycle.service';
import { PartService } from '../../services/part.service';

@Component({
  selector: 'app-appointment-list-page',
  standalone: true,
  imports: [MatTableModule, MatButtonModule, MatIconModule, MatDialogModule],
  templateUrl: './appointment-list-page.component.html',
  styleUrl: './appointment-list-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppointmentListPageComponent {
  private readonly dialog = inject(MatDialog);
  private readonly store = inject(AppointmentListStore);
  private readonly motoService = inject(MotorcycleService);
  private readonly partService = inject(PartService);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly appointments = this.store.appointments;
  protected readonly hasError = this.store.hasError;
  protected readonly isLoading = this.store.isLoading;
  protected readonly errorMessage = this.store.errorMessage;
  protected readonly displayedColumns = ['date', 'description', 'cost', 'status', 'actions'];

  protected readonly motorcycles = signal<Motorcycle[]>([]);
  protected readonly parts = signal<Part[]>([]);

  constructor() {
    this.store.load();
    this.motoService.getAll().pipe(takeUntilDestroyed()).subscribe(data => this.motorcycles.set(data));
    this.partService.getAll().pipe(takeUntilDestroyed()).subscribe(data => this.parts.set(data));
  }

  protected openCreateDialog(): void {
    if (this.isLoading()) return;

    this.dialog.open<AppointmentFormDialogComponent, AppointmentFormDialogData, AppointmentFormDialogResult>(
        AppointmentFormDialogComponent,
        { data: { title: 'Schedule Appointment', submitLabel: 'Schedule', motorcycles: this.motorcycles(), parts: this.parts() } }
      )
      .afterClosed().pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(result => {
        if (!result) return;
        this.store.create(result as CreateAppointmentDto);
      });
  }

  protected openEditDialog(appointment: ServiceAppointment): void {
    if (this.isLoading()) return;

    this.dialog.open<AppointmentFormDialogComponent, AppointmentFormDialogData, AppointmentFormDialogResult>(
        AppointmentFormDialogComponent,
        { data: { title: 'Edit Appointment', submitLabel: 'Save', motorcycles: this.motorcycles(), parts: this.parts(), initialValue: appointment } }
      )
      .afterClosed().pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(result => {
        if (!result) return;
        this.store.update(appointment.id, result as UpdateAppointmentDto);
      });
  }

  protected openDeleteDialog(appointment: ServiceAppointment): void {
    if (this.isLoading()) return;

    this.dialog.open<ConfirmDeleteDialogComponent, { item: ServiceAppointment }, boolean>(
        ConfirmDeleteDialogComponent,
        { data: { item: appointment } }
      )
      .afterClosed().pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(confirmed => {
        if (!confirmed) return;
        this.store.remove(appointment.id);
      });
  }
}