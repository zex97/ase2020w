import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {User} from '../dtos/user';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {AuthService} from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userBaseUri: string = this.globals.backendUri + '/api/user';

  constructor(private httpClient: HttpClient, private globals: Globals, private authService: AuthService) {
  }

  /**
   * Loads all users from the backend
   */
  getUsers(): Observable<User[]> {
    console.log('Searching for users.');
    return this.httpClient.get<User[]>(this.userBaseUri);
  }

  /**
   * Loads specific user from the backend
   * @param id of a user to load
   */
  getUserById(id: number): Observable<User> {
    console.log('Load user with id ' + id);
    return this.httpClient.get<User>(this.userBaseUri + '/' + id);
  }

  getUserByUsername(username: string): Observable<User> {
      console.log('Load user with username ' + username);
      return this.httpClient.get<User>(this.userBaseUri + '/username' + username);
    }

  /**
   * Persists user to the backend
   * @param user to persist
   */
  createUser(user: User): Observable<void> {
    console.log('Create user with username ' + user.username);
    return this.httpClient.post<void>(this.userBaseUri, user);
  }

  /**
   * Persists user with changed password to the backend
   * @param user to update
   */
  updateUserPassword(user: User): Observable<void> {
    console.log('Updating password for user ' + user.username);
    return this.httpClient.put<void>(this.userBaseUri, user);
  }

  /**
   * Resets the login count of the a user
   * @param id of the user
   */
  resetLoginAttempts(id): Observable<void> {
    console.log('Resetting login attempts.');
    return this.httpClient.put<void>(this.userBaseUri + '/' + id, null);
  }


  checkEmailAndRecover(email: string): Observable<void> {
    console.log('Checking email ' + email + ' and sending recovery link.');
    return this.httpClient.post<void>(this.userBaseUri + '/reset/' + email, null);
  }

  verifyToken(token: string): Observable<any> {
    console.log('Verifying token.');
    return this.httpClient.post<any>(this.userBaseUri + '/reset/token/' + token, null);
  }

  changePasswordWithToken(token: string, user: User): Observable<void> {
    console.log('Changing password.');
    return this.httpClient.post<void>(this.userBaseUri + '/reset/change/' + token, user);
  }
}
