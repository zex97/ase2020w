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
  getFile(space: Space, filename: string): Observable<Blob> {
    return this.httpClient.post(`${this.userBaseUri}/file/` + filename, space, {responseType: 'blob'});
  }

  /**
   * Delete a file from space
   * @param space for which we are deleting a file
   * @param fileName of the file we want to delete
   */
  deleteFile(space: Space, fileName: string) {
    console.log('Deleting file ' + fileName + ' from space' + space.name);
    return this.httpClient.delete(this.userBaseUri + '/delete-file/' + space.id + '/' + fileName);
  }

  /**
   * Delete space folder and files
   * @param spaceId id of space being deleted
   */
  deleteSpaceFiles(spaceId: number) {
    console.log('Deleting files from space.');
    return this.httpClient.delete(this.userBaseUri + '/delete-space-folder/' + localStorage.getItem('currentUser') + '/' + spaceId);
  }

}
