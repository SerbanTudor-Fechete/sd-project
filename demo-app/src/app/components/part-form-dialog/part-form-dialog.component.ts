import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

export interface PartFormDialogData {
  title: string;
  submitLabel?: string;
  initialValue?: PartFormInitialValue | null;
}

export interface PartFormValue {
  name: string;
  price: number;
}

export interface PartFormInitialValue {
  name: string;
  price: number;
}

export type PartFormDialogResult = PartFormValue | undefined;

@Component({
  selector: 'app-part-form-dialog',
  standalone: true, // Optional if you are using 'imports' array, but good practice
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './part-form-dialog.component.html',
  styleUrl: './part-form-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PartFormDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<PartFormDialogComponent>);
  protected readonly data = inject<PartFormDialogData>(MAT_DIALOG_DATA);

  protected readonly form = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    price: [0, [Validators.required, Validators.min(0.01)]],
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

    const { name, price } = this.form.getRawValue();
    const result: PartFormValue = { name, price };

    this.dialogRef.close(result);
  }

  protected cancel(): void {
    this.dialogRef.close(undefined);
  }
}