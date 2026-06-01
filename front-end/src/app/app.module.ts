import { NgModule } from '@angular/core';
import { APP_INITIALIZER } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CarrelloComponent } from './components/carrello/carrello.component';
import { ListaProdottiComponent } from './components/lista-prodotti/lista-prodotti.component';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AggiungiProdottoComponent } from './components/aggiungi-prodotto/aggiungi-prodotto.component';
import { KeycloakService } from './services/keycloak.service';
import { ModificaProdottoComponent } from './components/modifica-prodotto/modifica-prodotto.component';

export function kcFactory(kcService: KeycloakService){
  return () => kcService.init();
}

@NgModule({
  declarations: [
    AppComponent,
    CarrelloComponent,
    ListaProdottiComponent,
    AggiungiProdottoComponent,
    ModificaProdottoComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [   
    {
        provide: APP_INITIALIZER,
        deps: [KeycloakService],
        useFactory: kcFactory,
        multi:true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
