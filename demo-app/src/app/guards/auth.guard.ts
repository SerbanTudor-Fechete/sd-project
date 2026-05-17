import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { LoginStore } from '../features/login/login.store';

export const authGuard: CanActivateFn = () => {
  const loginStore = inject(LoginStore);
  const router = inject(Router);

  if (!loginStore.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }

  if (loginStore.role() === 'ADMIN') {
    alert('Access Denied: Admins must use the Admin Panel.');
    return router.createUrlTree(['/people']);
  }

  return true;
};

export const adminGuard: CanActivateFn = () => {
  const loginStore = inject(LoginStore);
  const router = inject(Router);

  if (!loginStore.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }

  if (loginStore.role() !== 'ADMIN') {
    alert('Access Denied: Admin privileges required.');
    return router.createUrlTree(['/customer-dashboard']);
  }

  return true;
};

export const guestGuard: CanActivateFn = () => {
  const loginStore = inject(LoginStore);
  const router = inject(Router);

  if (loginStore.isAuthenticated()) {
    return loginStore.role() === 'ADMIN' 
      ? router.createUrlTree(['/people']) 
      : router.createUrlTree(['/customer-dashboard']);
  }

  return true;
};