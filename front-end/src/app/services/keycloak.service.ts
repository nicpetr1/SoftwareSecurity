import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js';
import { UserProfile } from '../interfaces/user-profile';

@Injectable({
  providedIn: 'root'
})
export class KeycloakService {

  private _keycloak: Keycloak | undefined;
  private _profile: UserProfile | undefined;

  get keycloak(){
      if(!this._keycloak){
          this._keycloak = new Keycloak(
              {
                  url: 'https://localhost:8443',
                  realm: 'NegAnt',
                  clientId: 'neg-ant-client'
              }
          );
      }
      return this._keycloak;
  }

  get profile(): UserProfile | undefined {
    return this._profile;
  }

    async init(){
        const authentecated = await this.keycloak?.init({
            onLoad: 'login-required'
        });

        if(authentecated){
            this._profile = (await this.keycloak?.loadUserProfile()) as UserProfile;
            this._profile.token = this._keycloak?.token;
        }
    }

    hasRole(role: string): boolean {
        return this.keycloak?.hasResourceRole(role) ?? false;
    }

    getUsername(): string {
        return this._profile?.username 
            || this.keycloak?.tokenParsed?.['preferred_username'] 
            || '';
    }

    login(){
        return this._keycloak?.login();
    }

    logout(){
        return this._keycloak?.logout();
    }
}
