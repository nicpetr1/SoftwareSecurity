import { Component , OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProdottiService } from '../../services/prodotti.service';
import { Prodotto } from '../../interfaces/prodotto';
import Swal from 'sweetalert2'

@Component({
    selector: 'app-modifica-prodotto',
    templateUrl: './modifica-prodotto.component.html',
    styleUrls: ['./modifica-prodotto.component.css'],
    standalone: false
})
export class ModificaProdottoComponent implements OnInit {

  codiceProdotto = '';
  public feedback: string | null = null;
  public feedbackType: 'success' | 'error' = 'success';
  private toastTimer: any = null;
  
  formModel: Partial<Prodotto> = {
    nome: '',
    descrizione: '',
    prezzo: 0,
    quantita: 0
  };

  isLoading = false;
  isError = false;
  message = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private prodottiService: ProdottiService
  ) {}

  ngOnInit(): void {

    this.codiceProdotto = this.route.snapshot.paramMap.get('codice') || '';

    if (this.codiceProdotto) {
      this.caricaDatiProdotto();
    } else {
      this.setFeedback('Codice prodotto mancante', 'error');
    }
  }

  caricaDatiProdotto() {
    this.isLoading = true;
    this.prodottiService.getProdottoByCodice(this.codiceProdotto).subscribe({
      next: (data) => {
        this.formModel = {
          nome: data.nome,
          descrizione: data.descrizione,
          prezzo: data.prezzo,
          quantita: data.quantita
        };
        this.isLoading = false;
      },
      error: (err) => {
        this.setFeedback('Impossibile recuperare i dati del prodotto.', 'error');
        this.isLoading = false;
      }
    });
  }

onSubmit() {
    this.isLoading = true;
    this.isError = false;

    this.prodottiService.aggiornaProdotto(this.codiceProdotto, this.formModel).subscribe({
      next: (res) => {
        console.log('Aggiornamento riuscito:', res);
        this.isLoading = false;
        Swal.fire({
          icon: 'success',
          title: 'Ottimo!',
          text: 'Prodotto aggiornato con successo',
          timer: 2000,
          showConfirmButton: false
        }).then(() => {
          this.router.navigate(['']); 
        });

      },
      error: (err) => {
        console.error('Errore update:', err);
        this.isLoading = false;
        
        const msg = err.error?.error || err.error?.message || "Errore durante l'aggiornamento.";
        
        Swal.fire({
          icon: 'error',
          title: 'Errore',
          text: msg
        });
      }
    });
  }

  annulla() {
    this.router.navigate(['']);
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