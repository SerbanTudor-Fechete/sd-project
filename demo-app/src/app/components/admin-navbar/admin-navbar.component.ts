import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterModule } from '@angular/router';
import { LoginStore } from '../../features/login/login.store';

@Component({
  selector: 'app-admin-navbar',
  standalone: true,
  imports: [MatToolbarModule, MatButtonModule, RouterModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './admin-navbar.component.html',
  styleUrl: './admin-navbar.component.scss'
})
export class AdminNavbarComponent {
  private readonly loginStore = inject(LoginStore);
  private readonly router = inject(Router);

  protected logout(): void {
    this.loginStore.logout();
    void this.router.navigate(['/login']);
  }
}