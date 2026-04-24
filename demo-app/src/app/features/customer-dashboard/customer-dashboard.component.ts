import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { Router } from '@angular/router';
import { LoginStore } from '../login/login.store'; 

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [MatCardModule, MatButtonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './customer-dashboard.component.html',
  styleUrl: './customer-dashboard.component.scss' 
})
export class CustomerDashboardComponent {
  private readonly router = inject(Router);
  private readonly loginStore = inject(LoginStore); 

  protected logout(): void {
    this.loginStore.logout(); 
    void this.router.navigate(['/login']);
  }
}