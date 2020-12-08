import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HomeComponent} from './components/home/home.component';
import {FooterComponent} from './components/footer/footer.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import {Globals} from './global/globals';
import {PageNotFoundComponent} from './components/page-not-found/page-not-found.component';
import {RegisterComponent} from './components/register/register.component';
import {FlashcardManagerComponent} from './components/flashcard-manager/flashcard-manager.component';
import {DocumentSpaceComponent} from './components/document-space/document-space.component';
import {DocumentComponent} from './components/document/document.component';
import {BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatSelectModule} from '@angular/material/select';
import {MatCardModule} from '@angular/material/card';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    FooterComponent,
    LoginComponent,
    MessageComponent,
    PageNotFoundComponent,
    RegisterComponent,
    FlashcardManagerComponent,
    DocumentSpaceComponent,
    DocumentComponent,
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        ReactiveFormsModule,
        HttpClientModule,
        NgbModule,
        FormsModule,
        BrowserAnimationsModule,
        MatButtonModule,
        MatIconModule,
        MatSelectModule,
        MatCardModule
    ],
  providers: [httpInterceptorProviders, Globals],
  bootstrap: [AppComponent]
})
export class AppModule {
}
