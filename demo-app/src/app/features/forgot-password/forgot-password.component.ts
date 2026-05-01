import { Component, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent {
  private http = inject(HttpClient);
  private router = inject(Router);

  step = signal(1); 
  errorMessage = signal('');
  
  email = '';
  code = '';
  newPassword = '';
  confirmPassword = '';

  sendCode() {
    this.http.post('http://localhost:8080/person/forgot-password', { email: this.email })
      .subscribe({
        next: () => {
          this.step.set(2);
          this.errorMessage.set('');
        },
        error: () => this.errorMessage.set('User with this email not found.')
      });
  }

  resetPassword() {
  if (this.newPassword !== this.confirmPassword) {
    this.errorMessage.set('Passwords do not match!');
    return;
  }

  const payload = {
    email: this.email,
    code: this.code,
    newPassword: this.newPassword,
    confirmPassword: this.confirmPassword
  };

  this.http.post('http://localhost:8080/person/reset-password', payload, { responseType: 'text' })
    .subscribe({
      next: () => {
        alert('Success! Check your email for confirmation. You can now log in.');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        let msg = 'Invalid or expired code.';
        
        if (err.error && typeof err.error === 'string') {
          msg = err.error;
        } else if (err.error && err.error.message) {
          msg = err.error.message;
        }
        
        this.errorMessage.set(msg);
      }
    });
}
}