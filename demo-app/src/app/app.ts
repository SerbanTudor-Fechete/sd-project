import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterOutlet } from '@angular/router';
import { AdminNavbarComponent } from './components/admin-navbar/admin-navbar.component';
import { LoginStore } from './features/login/login.store';

@Component({
  selector: 'app-root',
  imports: [MatToolbarModule, RouterOutlet, AdminNavbarComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class App {
  protected readonly loginStore = inject(LoginStore);
}
