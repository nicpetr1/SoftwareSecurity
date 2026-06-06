import { APP_INITIALIZER, importProvidersFrom } from "@angular/core";
import { kcFactory } from './app/app.module';
import { KeycloakService } from "./app/services/keycloak.service";
import { provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { BrowserModule, bootstrapApplication } from "@angular/platform-browser";
import { AppRoutingModule } from "./app/app-routing.module";
import { FormsModule } from "@angular/forms";
import { AppComponent } from "./app/app.component";


bootstrapApplication(AppComponent, {
    providers: [
        importProvidersFrom(BrowserModule, AppRoutingModule, FormsModule),
        {
            provide: APP_INITIALIZER,
            deps: [KeycloakService],
            useFactory: kcFactory,
            multi: true
        },
        provideHttpClient(withInterceptorsFromDi())
    ]
})
  .catch(err => console.error(err));
