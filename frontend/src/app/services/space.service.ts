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
   * Persists space to the backend
   * @param space to persist
   */
  createSpace(space: Space, username: string): Observable<Space> {
    console.log('Create space with name ' + space.name);
    return this.httpClient.post<Space>(this.spaceBaseUri + '/' + username, space);
  }

}
