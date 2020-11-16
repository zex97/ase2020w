import {Injectable} from '@angular/core';
import {AuthRequest} from '../dtos/auth-request';
import {interval, Observable} from 'rxjs';
import {AuthResponse} from '../dtos/auth-response';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {tap} from 'rxjs/operators';
import jwt_decode from 'jwt-decode';
import {Globals} from '../global/globals';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private authBaseUri: string = this.globals.backendUri + '/authentication';
  private authScheduler: Observable<any> = interval(1000);

  private isLoggedInHELP: boolean = false;

  constructor(private httpClient: HttpClient, private globals: Globals) {
    /**this.scheduleReAuthentication();*/
  }

  /**
   * Login in the user. If it was successful, a valid JWT token will be stored
   * @param authRequest User data

  loginUser(authRequest: AuthRequest): Observable<AuthResponse> {
    return this.httpClient.post<AuthResponse>(this.authBaseUri, authRequest)
      .pipe(
        tap((authResponse: AuthResponse) => this.setToken(authResponse))
      );
  }*/

  /**
   * Login in the user. If it was successful, a valid JWT token will be stored
   * @param authRequest User data
   */
  loginUser(authRequest: AuthRequest): Observable<AuthResponse> {
    this.isLoggedInHELP = true;
    return new Observable<AuthResponse>();
  }



  /**
   * Check if a valid JWT token is saved in the localStorage
   */
  isLoggedIn() {
    /** return !!this.getToken() && (this.getTokenExpirationDate(this.getToken()).valueOf() > new Date().valueOf());
    */
    return this.isLoggedInHELP;
     }

  logoutUser() {
    this.isLoggedInHELP = false;
    /**console.log('Logout');
    localStorage.removeItem('currentToken');
    localStorage.removeItem('futureToken');*/
  }

  getToken() {
    return localStorage.getItem('currentToken');
  }

  getFutureToken() {
    return localStorage.getItem('futureToken');
  }

  /**
   * Returns the user role based on the current token
   */
  getUserRole() {
    return 'ADMIN';
    /**
    if (this.getToken() != null) {
      const decoded: any = jwt_decode(this.getToken());
      const authInfo = decoded.aut;
      if (authInfo.includes('ADMIN')) {
        return 'ADMIN';
      } else if (authInfo.includes('USER')) {
        return 'USER';
      }
    }
    return 'UNDEFINED';*/
  }

  private setToken(authResponse: AuthResponse) {
    localStorage.setItem('currentToken', authResponse.currentToken);
    localStorage.setItem('futureToken', authResponse.futureToken);
  }

  /**
   * JWT token expires after 10 minutes, therefore a new token will requested 1 minute before the expiration
   */
  private scheduleReAuthentication() {
    this.authScheduler.subscribe(() => {
      if (this.isLoggedIn()) {
        const timeLeft = this.getTokenExpirationDate(this.getToken()).valueOf() - new Date().valueOf();
        if ((timeLeft / 1000) < 60) {
          this.reAuthenticate().subscribe(
            () => {
              console.log('Re-authenticated successfully');
            },
            error => {
              console.log('Could not re-authenticate ' + error);
            });
        }
      }
    });
  }

  /**
   * Use futureToken as new token and request a new futureToken
   */
  private reAuthenticate(): Observable<AuthResponse> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Authorization': 'Bearer ' + this.getFutureToken()
      })
    };
    return this.httpClient.get<AuthResponse>(this.authBaseUri, httpOptions)
      .pipe(
        tap((authResponse: AuthResponse) => this.setToken(authResponse))
      );
  }

  private getTokenExpirationDate(token: string): Date {

    const decoded: any = jwt_decode(token);
    if (decoded.exp === undefined) {
      return null;
    }

    const date = new Date(0);
    date.setUTCSeconds(decoded.exp);
    return date;
  }

}
