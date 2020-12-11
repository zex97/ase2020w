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
   * @param spaceId space to which we want to upload
   * */
  uploadFile(file: File, spaceId: number): Observable<HttpResponse<Object>> {
    console.log(file.name + ' for ' + spaceId);
    const formData: FormData = new FormData();

    formData.append('file', file);
    return this.httpClient.post(`${this.userBaseUri}/single-file/` + spaceId, formData, {observe: 'response'});
  }

  /**
   * Fetch a file as resource to be shown in the application
   * @param space for which we are fetching the file
   * @param filename name of the file we want to fetch
   * */
  getFile(space: Space, filename: string) {

    return this.httpClient.post(`${this.userBaseUri}/single-file/` + filename, space, {observe: 'response'})
      .subscribe((res) => {
          console.log(res.headers);
        }
      );
  }


}
