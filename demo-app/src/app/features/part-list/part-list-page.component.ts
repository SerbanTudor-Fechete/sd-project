import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { ConfirmDeleteDialogComponent } from '../../components/confirm-delete-dialog/confirm-delete-dialog.component';

import { PartFormDialogComponent, PartFormDialogData, PartFormDialogResult } from '../../components/part-form-dialog/part-form-dialog.component';
import { CreatePartDto, Part, UpdatePartDto } from '../../models/part.model';
import { PartListStore } from './part-list.store';

@Component({
  selector: 'app-part-list-page',
  standalone: true,
  imports: [MatTableModule, MatButtonModule, MatIconModule, MatDialogModule],
  templateUrl: './part-list-page.component.html',
  styleUrl: './part-list-page.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PartListPageComponent {
  private readonly dialog = inject(MatDialog);
  private readonly store = inject(PartListStore);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly parts = this.store.parts;
  protected readonly hasError = this.store.hasError;
  protected readonly isLoading = this.store.isLoading;
  protected readonly errorMessage = this.store.errorMessage;
  protected readonly displayedColumns = ['name', 'price', 'actions'];

  constructor() {
    this.store.load();
  }

  protected openCreateDialog(): void {
    if (this.isLoading()) {
      return;
    }

    this.dialog
      .open<PartFormDialogComponent, PartFormDialogData, PartFormDialogResult>(
        PartFormDialogComponent,
        { data: { title: 'Create Part', submitLabel: 'Create' } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((result) => {
        if (!result) return;
        this.store.create(result as CreatePartDto);
      });
  }

  protected openEditDialog(part: Part): void {
    if (this.isLoading()) {
      return;
    }

    this.dialog
      .open<PartFormDialogComponent, PartFormDialogData, PartFormDialogResult>(
        PartFormDialogComponent,
        { data: { title: 'Edit Part', submitLabel: 'Save', initialValue: part } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((result) => {
        if (!result) return;
        this.store.update(part.id, result as UpdatePartDto);
      });
  }

  protected openDeleteDialog(part: Part): void {
    if (this.isLoading()) {
      return;
    }

    this.dialog
      .open<ConfirmDeleteDialogComponent, { item: Part }, boolean>(
        ConfirmDeleteDialogComponent,
        { data: { item: part } },
      )
      .afterClosed()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((confirmed) => {
        if (!confirmed) return;
        this.store.remove(part.id);
      });
  }
}