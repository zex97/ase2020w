import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './components/login/login.component';
import {PageNotFoundComponent} from './components/page-not-found/page-not-found.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';


const routes: Routes = [
  { path: '', component: MessageComponent},
  { path: 'login', component: LoginComponent},
  { path: '404', component: PageNotFoundComponent },
  { path: 'message', canActivate: [AuthGuard], component: MessageComponent},
  { path: '**', component: PageNotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
