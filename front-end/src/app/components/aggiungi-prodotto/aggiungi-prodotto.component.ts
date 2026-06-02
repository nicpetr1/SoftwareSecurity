import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ProdottiService } from '../../services/prodotti.service';
import { FormInserimento } from '../../interfaces/formInserimento';

@Component({
    selector: 'app-aggiungi-prodotto',
    templateUrl: './aggiungi-prodotto.component.html',
    styleUrls: ['./aggiungi-prodotto.component.css'],
    standalone: false
})

export class AggiungiProdottoComponent {

  formModel: FormInserimento = {
    nome: '',
    descrizione: '',
    prezzo: 0,
    quantita: 1
  };

  message: string = '';
  isError: boolean = false;
  isLoading: boolean = false;

  constructor(
    private prodottiService: ProdottiService, 
    private router: Router
  ) {}

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