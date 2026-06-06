import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { ProdottiService } from '../../services/prodotti.service';
import { FormInserimento } from '../../interfaces/formInserimento';
import { FormsModule } from '@angular/forms';

@Component({
    selector: 'app-aggiungi-prodotto',
    templateUrl: './aggiungi-prodotto.component.html',
    styleUrls: ['./aggiungi-prodotto.component.css'],
    imports: [FormsModule]
})

export class AggiungiProdottoComponent {
  private prodottiService = inject(ProdottiService);
  private router = inject(Router);


  formModel: FormInserimento = {
    nome: '',
    descrizione: '',
    prezzo: 0,
    quantita: 1
  };

  message = '';
  isError = false;
  isLoading = false;

  onSubmit() {
    this.isLoading = true;
    this.message = '';
    this.isError = false;

    this.prodottiService.aggiungiProdotto(this.formModel).subscribe({
      next: (prodottoCreato) => {
        console.log('Prodotto creato con successo:', prodottoCreato);
        this.isLoading = false;
        
        this.router.navigate(['']); 
      },
      error: (err) => {
        console.error('Errore creazione prodotto:', err);
        this.isLoading = false;
        this.isError = true;
        
        this.message = err.error?.message || 'Si è verificato un errore durante l\'aggiunta del prodotto.';
      }
    });
  }

  annulla() {
    this.router.navigate(['']);
  }
}