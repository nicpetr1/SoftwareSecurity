import { Injectable, inject } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';

import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Prodotto } from '../interfaces/prodotto';
import { FormInserimento } from '../interfaces/formInserimento';
import { KeycloakService } from './keycloak.service';


@Injectable({
  providedIn: 'root'
})
export class ProdottiService {
  private http = inject(HttpClient);
  private keycloakService = inject(KeycloakService);


  private prodottiUrl = 'https://localhost/api';

  /** Inserted by Angular inject() migration for backwards compatibility */
  constructor(...args: unknown[]);

  constructor() {
  }


  private httpOptions = {
    headers: new HttpHeaders(),
  };

  getProdotti(): Observable<Prodotto[]> {
    const token = this.keycloakService.profile?.token;
    const headers = new HttpHeaders({
      Authorization: "Bearer " + token            
  });
    return this.http.get<Prodotto[]>(`${this.prodottiUrl}/cliente/prodotti`, { headers });
  }

  getProdottiGestore(): Observable<Prodotto[]> {
    const token = this.keycloakService.profile?.token;
    const headers = new HttpHeaders({
      Authorization: "Bearer " + token            
  });
    return this.http.get<Prodotto[]>(`${this.prodottiUrl}/gestore/prodotti`, { headers });
  }

  aggiungiProdotto(prodotto: FormInserimento): Observable<Prodotto> {
    
    const token = this.keycloakService.profile?.token; 
    
    let params = new HttpParams();
    (Object.keys(prodotto) as (keyof FormInserimento)[]).forEach(key => {
      const value = prodotto[key];
      if (value !== null) { 
        params = params.set(key, value.toString());
      }
    });

    const headers = new HttpHeaders({
      'Authorization': "Bearer " + token,
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    return this.http.post<Prodotto>( 
      `${this.prodottiUrl}/gestore/aggiungi`,
      params.toString(),
      {
        headers: headers
      }
    ).pipe(
      catchError(this.handleError)
    );
  }

  getProdottoByCodice(codice: string): Observable<Prodotto> {
    const token = this.keycloakService.profile?.token;
    const headers = new HttpHeaders({
      Authorization: "Bearer " + token            
  });
    return this.http.get<Prodotto>(`${this.prodottiUrl}/gestore/prodotto/${codice}`, { headers });
  }


aggiornaProdotto(codice: string, dati: Partial<Prodotto>): Observable<any> {

  const token = this.keycloakService.profile?.token; 

  const headers = new HttpHeaders({
      'Authorization': "Bearer " + token,
    });

    return this.http.post<Prodotto>( 
      `${this.prodottiUrl}/gestore/aggiorna/${codice}`,
      dati,
      {
        headers: headers
      }
    ).pipe(
      catchError(this.handleError)
    );

}

eliminaProdotto(codice: string): Observable<any> {

  const token = this.keycloakService.profile?.token; 

  const headers = new HttpHeaders({
      'Authorization': "Bearer " + token,
    });

    return this.http.post(
      `${this.prodottiUrl}/gestore/rimuovi/${codice}`,
      {},
      {
        headers: headers
      }
    ).pipe(
      catchError(this.handleError)
    );
  }


  private handleError(error: HttpErrorResponse): Observable<never> {
    
    const errorMessage = error.error?.message || error.message || 'Errore sconosciuto del server';
    console.error('Errore nel servizio prodotti:', error);

    return throwError(() => new Error(errorMessage));
  }
}

