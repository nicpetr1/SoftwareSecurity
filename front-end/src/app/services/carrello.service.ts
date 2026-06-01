import { Injectable,} from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { KeycloakService } from './keycloak.service';

@Injectable({
  providedIn: 'root'
})
export class CarrelloService {

  private backendUrl = 'https://localhost/api';

  constructor(private http: HttpClient, private keycloakService: KeycloakService) { }


aggiungi(codiceProdotto: string, quantita: number): Observable<{ success: boolean; message: string | null }> {
    
    const params = new HttpParams().set('quantitaInserita', quantita.toString());
    const token = this.keycloakService.profile?.token;
    const headers = new HttpHeaders({
      Authorization: "Bearer " + token
    })
      .set('Content-Type', 'application/x-www-form-urlencoded')

    return this.http.post<any>( 
      `${this.backendUrl}/cliente/prodotti/inserisci/${codiceProdotto}`,
      params.toString(),
      {
        headers: headers,
        withCredentials: true 
      }
    ).pipe(
      map((response: any) => {
        return { success: true, message: response.message };
      }),
      catchError((error: HttpErrorResponse) => {
        const message = error.error?.message || 'Errore sconosciuto';
        return of({ success: false, message: message }); 
      })
    );
  }

  getItems(): Observable<any[]> {
    const token = this.keycloakService.profile?.token;
    const headers = new HttpHeaders({
      Authorization: "Bearer " + token
    });

    return this.http.get<any[]>(`${this.backendUrl}/cliente/visualizza`, {
      headers: headers,
      withCredentials: true
    }).pipe(
      catchError(() => of([]))
    );
  }

rimuovi(codiceProdotto: string): Observable<boolean> {

    const token = this.keycloakService.profile?.token;
    const headers = new HttpHeaders({
      Authorization: "Bearer " + token
    });

    const url = `${this.backendUrl}/carrello/prodotti/${codiceProdotto}`;

    return this.http.get(
      url,
      {
        headers: headers,
        withCredentials: true
      }
    ).pipe(
      map(() => true),
      catchError((error) => {
        console.error('Errore durante la rimozione:', error);
        return of(false);
      })
    );
  }

  checkout(): Observable<any> {

    const token = this.keycloakService.profile?.token;
    const headers = new HttpHeaders({
      'Authorization': "Bearer " + token
    });

    return this.http.post(
      `${this.backendUrl}/cliente/acquista`,
      {},
      {
        headers: headers
      }
    )
  }
}