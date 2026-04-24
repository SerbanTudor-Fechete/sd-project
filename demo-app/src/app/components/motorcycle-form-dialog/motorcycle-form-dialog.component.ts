import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Person } from '../../models/person.model';

export interface MotorcycleFormDialogData {
  title: string;
  submitLabel?: string;
  owners: Person[]; 
  initialValue?: MotorcycleFormInitialValue | null;
}

export interface MotorcycleFormValue {
  brand: string;
  model: string;
  manufactureYear: string;
  licensePlate: string;
  ownerId: string;
}

export interface MotorcycleFormInitialValue {
  brand: string;
  model: string;
  manufactureYear: string;
  licensePlate: string;
  ownerId?: string; 
}

export type MotorcycleFormDialogResult = MotorcycleFormValue | undefined;

@Component({
  selector: 'app-motorcycle-form-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule
  ],
  templateUrl: './motorcycle-form-dialog.component.html',
  styleUrl: './motorcycle-form-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MotorcycleFormDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<MotorcycleFormDialogComponent>);
  protected readonly data = inject<MotorcycleFormDialogData>(MAT_DIALOG_DATA);

  protected readonly form = this.fb.nonNullable.group({
    brand: ['', [Validators.required]],
    model: ['', [Validators.required]],
    manufactureYear: ['', [Validators.required, Validators.pattern('^[0-9]{4}$')]],
    licensePlate: ['', [Validators.required, Validators.minLength(7), Validators.maxLength(8)]],
    ownerId: ['', [Validators.required]]
  });

  ngOnInit(): void {
    if (this.data.initialValue) {
      this.form.patchValue(this.data.initialValue);
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