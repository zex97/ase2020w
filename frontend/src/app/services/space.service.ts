import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {FormBuilder} from '@angular/forms';
import {AuthService} from './auth.service';
import {User} from '../dtos/user';



@Injectable({
  providedIn: 'root'
})
export class SpaceService {

  private spaceBaseUri: string = this.globals.backendUri + '/api/space';

  constructor(private httpClient: HttpClient, private globals: Globals, private authService: AuthService) {
  }

}
