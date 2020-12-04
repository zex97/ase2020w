import {Injectable} from '@angular/core';
import {HttpClient, HttpEvent, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {AuthService} from './auth.service';
import {Space} from '../dtos/space';
import {stringify} from 'querystring';

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {

  private userBaseUri: string = this.globals.backendUri + '/api/upload';

  constructor(private httpClient: HttpClient, private globals: Globals, private authService: AuthService) {
  }

  /**
   * Send a single file request to backend
   * @param file to upload
   * @param userId name of the user that is uploading the file
   * */
  uploadFile(file: File, space: Space): Observable<HttpEvent<unknown>> {

    const formData: FormData = new FormData();

    formData.append('file', file);

    const req = new HttpRequest('POST', `${this.userBaseUri}/single-file/` + space.id, formData, {
      // reportProgress: true,
      responseType: 'json'
    });

    return this.httpClient.request(req);
  }

}
