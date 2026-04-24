import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Motorcycle } from '../../models/motorcycle.model';
import { Part } from '../../models/part.model';
import { AppointmentStatus } from '../../models/appointment.model';

export interface AppointmentFormDialogData {
  title: string;
  submitLabel?: string;
  motorcycles: Motorcycle[];
  parts: Part[];
  initialValue?: AppointmentFormInitialValue | null;
}

export interface AppointmentFormValue {
  scheduleDate: string;
  description: string;
  totalCost: number;
  status: AppointmentStatus;
  motorcycleId: string;
  partIds: string[];
}

export interface AppointmentFormInitialValue {
  scheduleDate: string;
  description: string;
  totalCost: number;
  status: AppointmentStatus;
  motorcycleId?: string;
  partIds?: string[];
}

export type AppointmentFormDialogResult = AppointmentFormValue | undefined;

@Component({
  selector: 'app-appointment-form-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule, MatDialogModule, MatFormFieldModule, 
    MatInputModule, MatButtonModule, MatSelectModule
  ],
  templateUrl: './appointment-form-dialog.component.html',
  styleUrl: './appointment-form-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppointmentFormDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<AppointmentFormDialogComponent>);
  protected readonly data = inject<AppointmentFormDialogData>(MAT_DIALOG_DATA);

  protected readonly form = this.fb.nonNullable.group({
    scheduleDate: ['', [Validators.required]],
    description: ['', [Validators.required]],
    totalCost: [0, [Validators.required, Validators.min(0)]],
    status: ['PENDING' as AppointmentStatus, [Validators.required]],
    motorcycleId: ['', [Validators.required]],
    partIds: [[] as string[]] // Optional multiple selection
  });

  ngOnInit(): void {
    if (this.data.initialValue) {
      this.form.patchValue({
        ...this.data.initialValue,
        partIds: this.data.initialValue.partIds || []
      });
    }
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.dialogRef.close(this.form.getRawValue());
  }

  protected cancel(): void {
    this.dialogRef.close(undefined);
  }
}