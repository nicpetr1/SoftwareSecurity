import { Component , OnInit  } from '@angular/core';
import { Router } from '@angular/router';
import { CarrelloService } from '../../services/carrello.service';
import Swal from 'sweetalert2'

@Component({
    selector: 'app-carrello',
    templateUrl: './carrello.component.html',
    styleUrls: ['./carrello.component.css'],
    standalone: false
})
export class CarrelloComponent implements OnInit {

  public items: any[] = [];
  public isLoading = true;
  public totale = 0.0;
  public checkoutMessage: { type: 'success' | 'error', text: string } | null = null;

  constructor(
    private carrelloService: CarrelloService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.caricaCarrello();
  }


  caricaCarrello(): void {
    this.isLoading = true;
    this.carrelloService.getItems().subscribe((data: any[]) => {
      this.items = data;
      this.calcolaTotale();
      this.isLoading = false;
    });
  }

  rimuoviDalCarrello(item: any): void {
    // Assumendo che il codice sia in 'item.prodotto.codice'
    this.carrelloService.rimuovi(item.prodotto.codice).subscribe({
      next: (response: any) => {
        this.caricaCarrello();
       },
      error: (err) => {
        alert('Errore: impossibile rimuovere il prodotto.');
      }
    });
  }

  /**
   * Calcola il totale localmente dai dati ricevuti.
   */
  calcolaTotale(): void {
    this.totale = this.items.reduce((acc, item) => {
      // Calcola (prezzo * quantità) per ogni riga
      return acc + (item.prodotto.prezzo * item.quantitaInserita);
    }, 0);
  }

procediAlCheckout() {
    // Mostriamo un caricamento mentre elabora
    Swal.fire({
      title: 'Elaborazione in corso...',
      didOpen: () => {
        Swal.showLoading();
      }
    });

    this.carrelloService.checkout().subscribe({
      next: (response: any) => {
        // SUCCESSO (Status 200)
        // response contiene: { message: "...", codiceOrdine: "...", prezzoTotale: ... }
        
        Swal.fire({
          icon: 'success',
          title: 'Acquisto completato!',
          html: `Codice Ordine: <b>${response.codiceOrdine}</b><br>Totale: €${response.prezzoTotale}`,
          confirmButtonText: 'Ok'
        }).then(() => {
          // Reindirizza alla home o allo storico ordini
          this.router.navigate(['/']); 
        });
      },
      error: (err) => {
        // ERRORE (Status 400, 500, etc.)
        console.error("Errore checkout:", err);
        
        // Se è un 400, il messaggio sarà "Il carrello è vuoto" (preso dal body della risposta Java)
        const msg = err.error?.error || "Si è verificato un errore durante l'acquisto.";

        Swal.fire({
          icon: 'error',
          title: 'Attenzione',
          text: msg
        });
      }
    });
  }

  public chiudiPopup(): void {
    if (this.checkoutMessage?.type === 'success') {
      this.router.navigate(['/cliente']);
    }
    this.checkoutMessage = null;
  }
}