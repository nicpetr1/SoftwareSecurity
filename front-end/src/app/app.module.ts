import { KeycloakService } from './services/keycloak.service';

export function kcFactory(kcService: KeycloakService){
  return () => kcService.init();
}


