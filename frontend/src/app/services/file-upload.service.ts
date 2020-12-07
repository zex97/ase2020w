import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {AuthService} from './auth.service';
import {Space} from '../dtos/space';

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
   * @param spaceId
   * */
  uploadFile(file: File, spaceId: number): Observable<HttpResponse<Object>> {
    console.log(file.name + ' for ' + spaceId);
    const formData: FormData = new FormData();

    formData.append('file', file);
    return this.httpClient.post(`${this.userBaseUri}/single-file/` + spaceId, formData, {observe: 'response'});
  }

  getFile(space: Space, filename: string) {

    return this.httpClient.post(`${this.userBaseUri}/single-file/` + filename, space, {observe: 'response'})
      .subscribe((res) => {
          console.log(res.headers);
        }
      );
  }


}
