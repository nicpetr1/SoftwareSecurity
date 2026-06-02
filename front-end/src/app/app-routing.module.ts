import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { ListaProdottiComponent } from './components/lista-prodotti/lista-prodotti.component';
import { CarrelloComponent } from './components/carrello/carrello.component';
import { AggiungiProdottoComponent } from './components/aggiungi-prodotto/aggiungi-prodotto.component';
import { ModificaProdottoComponent } from './components/modifica-prodotto/modifica-prodotto.component';
import { roleGuard } from './role.guard';


const routes: Routes = [
  { path: '', component: ListaProdottiComponent},
  { path: 'carrello', component: CarrelloComponent, canActivate: [roleGuard], data: { role: 'client_cliente' }  },
  { path: 'aggiungi-prodotto', component: AggiungiProdottoComponent, canActivate: [roleGuard], data: { role: 'client_gestore' } },
  { path: 'modifica-prodotto/:codice', component: ModificaProdottoComponent, canActivate: [roleGuard], data: { role: 'client_gestore' } },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }