import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {SpaceService} from '../../services/space.service';
import {Space} from '../../dtos/space';
import {FileUploadService} from '../../services/file-upload.service';

@Component({
  selector: 'app-document-space',
  templateUrl: './document-space.component.html',
  styleUrls: ['./document-space.component.scss']
})

export class DocumentSpaceComponent implements OnInit {

  spaceForm: FormGroup;
  nameEditForm: FormGroup;
  fileUploadForm: FormGroup;
  filesToUpload: File[] = [];
  error: boolean = false;
  success: boolean = false;
  successMessage: String = '';
  errorMessage: string = '';
  fileUploadModuleError: boolean = false;
  fileUploadModuleErrorMessage: String = '';
  /**TODO: think what happens when the user deletes space with id 1*/
  spaceId: number = 1;
  private spaces: Space[];


  constructor(private formBuilder: FormBuilder, private spaceService: SpaceService, private userService: UserService,
              private fileUploadService: FileUploadService) {
    this.spaceForm = this.formBuilder.group({
      name: ['']
    });
    this.fileUploadForm = this.formBuilder.group({
      name: ['']
    });
    this.nameEditForm = this.formBuilder.group({
      name: ['']
    });
  }

  ngOnInit(): void {
    this.loadAllSpaces();
  }

  /**
   * Sends a request to load all spaces belonging to the currently logged-in user.
   */
  loadAllSpaces() {
    this.spaceService.getSpaces(localStorage.getItem('currentUser')).subscribe(
      (spaceList: Space[]) => {
        this.spaces = spaceList;
      },
      error => {
        this.defaultErrorHandling(error);
      }
    );
  }

  vanishSuccessMessage() {
    this.success = false;
    this.successMessage = '';
  }

  vanishModuleErrorMessage() {
    this.fileUploadModuleError = false;
    this.fileUploadModuleErrorMessage = '';
  }

  /**
   * Save uploaded files into a global variable and check if any of them exceed the upload limit.
   * */
  handleFileInput(files: File[]) {
    this.filesToUpload = Array.from(files);
    this.vanishModuleErrorMessage();
    for (let i = 0; i < this.filesToUpload.length; i++) {
      const file = this.filesToUpload[i];
      if (file.size > 20000000) {
        console.log(file.size);
        this.fileUploadModuleError = true;
        this.fileUploadModuleErrorMessage = 'File \'' + file.name + '\' size exceeds maximum size of 20MB';
      }
    }
  }

  /**
   * If file is removed check if the list is empty.
   * Important: If empty initialize with new array to avoid file number of input field
   * */
  removeFileFromList(i: number) {
    this.filesToUpload.splice(i, 1);

    // delete this.filesToUpload[i];

    if (this.filesToUpload.length === 0) {
      this.filesToUpload = [];
    }
    this.handleFileInput(this.filesToUpload);
  }


  /**
   * Confirms that all files are valid size and there is at least one file to be uploaded
   * */
  filesReadyForUpload() {
    return (this.filesToUpload.length > 0 && !this.fileUploadModuleError);
  }

  getFilesToUpload() {
    return Array.from(this.filesToUpload);
  }


  /**
   * Iterate through the list of objects and send a post request to backend for each one of them
   * */
  uploadFile(space: Space) {
    const user = localStorage.getItem('currentUser');
    // tslint:disable-next-line:forin
    for (let i = 0; i < this.filesToUpload.length; i++) {
      // TODO: should file types be validated in frontend???
      const file = this.filesToUpload[i];
      this.fileUploadService.uploadFile(file, space).subscribe(res => {
        console.log('file uploaded successfully');
      });
    }
    if (this.filesToUpload.length > 0) {
      this.successMessage = 'You successfully uploaded ' + this.filesToUpload.length + ' file(s).';
    } else {
      this.successMessage = 'No files were chosen.';
    }
    this.success = true;
  }

  clearInputFiles() {
    this.filesToUpload = [];
  }

  getSpaces() {
    return this.spaces;
  }

  /**
   * Builds a space dto and sends a creation request.
   */
  createSpace() {
    this.userService.getUserByUsername(localStorage.getItem('currentUser')).subscribe(res => {
      const space = new Space(0, this.spaceForm.controls.name.value, res);
      this.spaceService.createSpace(space, localStorage.getItem('currentUser')).subscribe(
        () => {
          this.loadAllSpaces();
        },
        error => {
          this.defaultErrorHandling(error);
        }
      );
    });
  }

  /**
   * Sends a request to delete a specific space.
   */
  deleteSpace(id: number) {
    this.spaceService.deleteSpace(id, localStorage.getItem('currentUser')).subscribe(
      () => {
        this.loadAllSpaces();
      },
      error => {
        this.defaultErrorHandling(error);
      });
  }

  /**
   * Sends a put request to change a specific space.
   */
  saveEdits(space: Space) {
    // send edits to backend
    this.userService.getUserByUsername(localStorage.getItem('currentUser')).subscribe(res => {
      space.name = this.nameEditForm.controls.name.value;
      this.spaceService.editSpace(space, localStorage.getItem('currentUser')).subscribe(
        () => {
          this.loadAllSpaces();
          location.reload();
        },
        error => {
          this.defaultErrorHandling(error);
        }
      );
    });
  }


  /**
   * Saves the id of the space user clicked on, to connect it with document component.
   */
  loadSpaceDetails(id: number) {
    this.spaceId = id;
  }

  private defaultErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    this.errorMessage = '';
    this.errorMessage = error.error.message;
  }

}
