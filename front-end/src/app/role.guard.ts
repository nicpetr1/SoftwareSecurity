import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { KeycloakService } from './services/keycloak.service';

export const roleGuard: CanActivateFn = (route, state) => {

  const keycloakService = inject(KeycloakService);
  const router = inject(Router);

  
  const expectedRole = route.data['role'] as string;

  
  if (keycloakService.hasRole(expectedRole)) {
    return true;
  }

  
  return router.createUrlTree(['/']); 
};