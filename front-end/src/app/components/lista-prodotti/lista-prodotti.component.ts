import { Component, OnInit, inject,ChangeDetectorRef  } from '@angular/core';
import { Prodotto } from '../../interfaces/prodotto'
import { ProdottiService } from 'src/app/services/prodotti.service';
import { Router, RouterLink } from '@angular/router';
import { CarrelloService } from 'src/app/services/carrello.service';
import { KeycloakService } from 'src/app/services/keycloak.service';
import Swal from 'sweetalert2';
import { NgClass, CurrencyPipe } from '@angular/common';

@Component({
    selector: 'app-lista-prodotti',
    templateUrl: './lista-prodotti.component.html',
    styleUrls: ['./lista-prodotti.component.css'],
    imports: [RouterLink, NgClass, CurrencyPipe]
})
export class ListaProdottiComponent implements OnInit{
  private prodottiService = inject(ProdottiService);
  private keycloakService = inject(KeycloakService);
  private router = inject(Router);
  private carrelloService = inject(CarrelloService);
  private cdr = inject(ChangeDetectorRef);


  prodotti: Prodotto[] = [];
  isLoading = true;
  error: string | null = null;
  isGestore = false;
  isCliente = false;

  public feedback: string | null = null;
  public feedbackType: 'success' | 'error' = 'success';
  private toastTimer: any = null;


  fileChangeEvent(fileInput: any, prodotto: Prodotto) {
    
    if (fileInput.target.files && fileInput.target.files[0]) {
      const file = fileInput.target.files[0];
      const reader = new FileReader();
      reader.readAsDataURL(file);
      console.log('prodotto: ', prodotto);
    }
  }

  ngOnInit(): void {
    this.isGestore = this.keycloakService.hasRole('client_gestore');
    this.isCliente = this.keycloakService.hasRole('client_cliente');
    this.loadProdotti();
  }

  private loadProdotti(): void {
    this.isLoading = true;
    this.error = null;

    if(this.isCliente){

      this.prodottiService.getProdotti().subscribe({
        next: (data: Prodotto[]) => {
          this.prodotti = data.filter(prodotto => prodotto.quantita > 0);
          this.isLoading = false;
          console.log('Prodotti loaded:', this.prodotti);
          this.cdr.detectChanges();
        },
        error: () => {
          this.error = 'Failed to load products.';
          this.isLoading = false;
          this.cdr.detectChanges();
        },
      });
    }

    else if(this.isGestore){

      this.prodottiService.getProdottiGestore().subscribe({
        next: (data: Prodotto[]) => {
          this.prodotti = data;
          this.isLoading = false;
          console.log('Prodotti loaded:', this.prodotti);
          this.cdr.detectChanges();
        },
        error: () => {
          this.error = 'Failed to load products.';
          this.isLoading = false;
          this.cdr.detectChanges();
        },
      });
    }
  }

  public clearFeedback() {
    this.feedback = null;
    if (this.toastTimer) {
      clearTimeout(this.toastTimer);
      this.toastTimer = null;
    }
  }

public logout(){
  this.keycloakService.logout();
}

public aggiungiAlCarrello(prodotto: Prodotto): void {
    console.log('Aggiungo:', prodotto.nome);
    
    if (prodotto.quantita <= 0) {
      this.setFeedback('Questo prodotto è appena terminato!', 'error');
      return;
    }
    
    this.carrelloService.aggiungi(prodotto.codice.toString(), 1).subscribe(response => {
      
      if (response.success) {

        prodotto.quantita -= 1; 

        this.setFeedback('Prodotto aggiunto al carrello!', 'success');
        this.cdr.detectChanges();

      } else {
        this.setFeedback('Errore: impossibile aggiungere il prodotto.', 'error');
      }
    });
  }


rimuoviProdotto(codice: string) {
    
  
    Swal.fire({
      title: 'Sei sicuro?',
      text: "Non potrai annullare questa operazione!",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Sì, elimina!',
      cancelButtonText: 'Annulla'
    }).then((result) => {
      
      if (result.isConfirmed) {
        
        this.prodottiService.eliminaProdotto(codice).subscribe({
          next: () => {
            this.prodotti = this.prodotti.filter(p => p.codice !== codice);
            this.cdr.detectChanges();
            
            Swal.fire(
              'Eliminato!',
              'Il prodotto è stato rimosso.',
              'success'
            );
          },
          error: (err) => {
            const errorMsg = err.error?.message || "Errore durante l'eliminazione";
            Swal.fire(
              'Errore!',
              errorMsg,
              'error'
            );
          }
        });
      }
    });
  }

  public getUserName(): string | null {
    return this.keycloakService.getUsername();
  }

private setFeedback(message: string, type: 'success' | 'error') {
    this.feedback = message;
    this.feedbackType = type;


    if (this.toastTimer) {
      clearTimeout(this.toastTimer);
    }


    this.toastTimer = setTimeout(() => {
    this.feedback = null;
    this.toastTimer = null;
    }, 4000);
  }
}




