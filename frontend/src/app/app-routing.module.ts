import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './components/login/login.component';
import {PageNotFoundComponent} from './components/page-not-found/page-not-found.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {HomeComponent} from './components/home/home.component';
import {AppComponent} from './app.component';
import {RegisterComponent} from './components/register/register.component';
import {FlashcardManagerComponent} from './components/flashcard-manager/flashcard-manager.component';
import {DocumentSpaceComponent} from './components/space-document-slide-panel/document-space/document-space.component';
import {DocumentComponent} from './components/space-document-slide-panel/document/document.component';
import {PasswordResetComponent} from './components/password-reset/password-reset.component';
import {ChangePasswordComponent} from './components/change-password/change-password.component';


const routes: Routes = [
  { path: '',
    component: AppComponent,
    children: [
      {
        path: '',
        component: LoginComponent
      },
      {
        path: 'login',
        component: LoginComponent
      },
      {
        path: 'register',
        component: RegisterComponent
      },
      {
        path: 'reset',
        component: PasswordResetComponent
      },
      {
        path: 'changePassword',
        component: ChangePasswordComponent
      },
      {
        path: 'home',
        component: HomeComponent,
        canActivate: [AuthGuard],
        children: [
          {
            path: '',
            component: DocumentSpaceComponent,
            outlet: 'view'
          },
          {
            path: 'message',
            component: MessageComponent,
            outlet: 'view'
          },
          {
            path: 'flashcards',
            component: FlashcardManagerComponent,
            outlet: 'view'
          },
          {
            path: 'spaces',
            component: DocumentSpaceComponent,
            outlet: 'view',
            children: [
              {
                path: 'document',
                component: DocumentComponent
              }
            ]
          }
        ]
      }
    ]
  },
  { path: '**', component: PageNotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
