import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {FormBuilder} from '@angular/forms';
import {AuthService} from './auth.service';
import {User} from '../dtos/user';
import {Space} from '../dtos/space';


@Injectable({
  providedIn: 'root'
})
export class SpaceService {

  private spaceBaseUri: string = this.globals.backendUri + '/api/space';

  constructor(private httpClient: HttpClient, private globals: Globals, private authService: AuthService) {
  }

    /**
     * Loads all spaces from the backend
     */
    getSpaces(username: string): Observable<Space[]> {
      console.log('Searching for spaces.')
      return this.httpClient.get<Space[]>(this.spaceBaseUri + '/' + username);
    }

  /**
   * Persists space to the backend
   * @param space to persist
   */
  createSpace(space: Space, username: string): Observable<Space> {
    console.log('Create space with name ' + space.name);
    return this.httpClient.post<Space>(this.spaceBaseUri + '/' + username, space);
  }

  /**
     * Delete space from backend
     * @param id of the space to delete
     * @param username of the user
     */
    deleteSpace(id: number, username: string): Observable<Space> {
      console.log('Delete a space');
      return this.httpClient.delete<Space>(this.spaceBaseUri + '/' + username + '/'+ id);
    }

    /**
    * Change space name in the backend
    * @param space to make changes to
    */
    editSpace(space: Space, username: string): Observable<Space> {
         console.log('Change the space name to ' + space.name);
        return this.httpClient.put<Space>(this.spaceBaseUri + '/' + username, space);
      }
}
