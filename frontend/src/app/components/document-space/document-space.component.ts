import {Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {SpaceService} from '../../services/space.service';
import {Space} from '../../dtos/space';
import {FileUploadService} from '../../services/file-upload.service';
import {MatSnackBar} from '@angular/material/snack-bar';

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
  filesToUploadNames: String[] = [];
  error: boolean = false;
  success: boolean = false;
  successMessage: String = '';
  errorMessage: string = '';
  fileUploadModuleError: boolean = false;
  fileUploadModuleErrorMessage: String = '';
  /**TODO: think what happens when the user deletes space with id 1*/
  spaceId: number = 1;
  private spaces: Space[];
  currentSpace: Space;
  selectSpace: Space;

  @ViewChild('documentComponent') documentComponent;

  constructor(private formBuilder: FormBuilder, private spaceService: SpaceService, private userService: UserService,
              private fileUploadService: FileUploadService, private snackBar: MatSnackBar) {
    this.spaceForm = this.formBuilder.group({
      name: ['', [
        Validators.required,
        Validators.minLength(1)
      ]],
    });
    this.fileUploadForm = this.formBuilder.group({
      name: ['', [
        Validators.required,
        Validators.minLength(1)
      ]]
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
   * Sets the value of the space user is currently observing
   * Necessary for document fetching and transfering space value
   * into individual modules
   * */
  setCurrentSpace(space: Space) {
    this.currentSpace = space;
    console.log(this.currentSpace);
    this.nameEditForm.patchValue({
      name: space.name
    });
  }

  getCurrentSpace() {
    return this.currentSpace;
  }

  setSelectSpace(space: Space) {
    this.selectSpace = space;
  }

  resetSpaceFrom() {
    this.spaceForm.reset();
  }

  /**
   * Save uploaded files into a global variable and check if any
   * of them exceed the upload limit.
   * */
  handleFileInput(files: File[]) {
    console.log('Handling files for space: ' + this.spaceId);
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
   * Important: If empty initialize with new array to
   * avoid file number of input field
   * */
  removeFileFromList(i: number) {
    this.filesToUpload.splice(i, 1);
    this.filesToUploadNames.splice(i, 1);


    if (this.filesToUpload.length === 0) {
      this.filesToUpload = [];
      this.filesToUploadNames = [];
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

  getFilesToUploadNames() {
    return this.filesToUploadNames;
  }


  /**
   * Iterate through the list of objects and send a post
   * request to backend for each one of them.
   * @param space where we are uploading the files
   * */
  uploadFiles(space: Space) {
    const spaceId = space.id;
    console.log('Uploading file for space: ' + spaceId);
    let successUploadCount: number = 0;
    // tslint:disable-next-line:forin
    for (let i = 0; i < this.filesToUpload.length; i++) {
      const file = this.filesToUpload[i];
      this.fileUploadService.uploadFile(file, spaceId).subscribe((res) => {
        if (res) {
          if (res.status === 200) {
            successUploadCount = successUploadCount + 1;
          } else {
            // create error message if at least one of the files fails to be uploaded
            this.error = true;
            this.errorMessage += file.name + ' failed to be uploaded; ';
          }
        }
        // create success message if all files successfully uploaded
        if (this.filesToUpload.length > 0 && this.filesToUpload.length === successUploadCount) {
          this.openSnackbar('You successfully uploaded ' + this.filesToUpload.length + ' file(s).', 'success-snackbar');
          this.success = true;
          this.documentComponent.ngOnChanges();
        }
      });
    }
    // if success is not true then an error has happened, notify the user
    if (this.success) {
      this.errorMessage = 'File Upload failed';
      this.error = true;
    }
  }

  /**
   * Resets the file input field after a close or submit
   * */
  clearInputFiles() {
    this.filesToUpload = [];
    this.filesToUploadNames = [];
  }

  getSpaces() {
    return this.spaces;
  }

  /**
   * Fetches a file as resource from the backend
   * */
  loadFile(space: Space, fileName: string) {
    this.fileUploadService.getFile(space, fileName);
  }

  /**
   * Builds a space dto and sends a creation request.
   */
  createSpace() {
    this.userService.getUserByUsername(localStorage.getItem('currentUser')).subscribe(res => {
      const space = new Space(0, this.spaceForm.controls.name.value, res);
      this.spaceService.createSpace(space).subscribe(
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
    this.spaceService.deleteSpace(id).subscribe(
      () => {
        // set chosen space as unselected
        this.selectSpace = null;
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
    this.userService.getUserByUsername(localStorage.getItem('currentUser')).subscribe(() => {
      space.name = this.nameEditForm.controls.name.value;
      this.spaceService.editSpace(space).subscribe(
        () => {
          this.loadAllSpaces();
          //location.reload();
        },
        error => {
          this.defaultErrorHandling(error);
        }
      );
    });
  }

  openSnackbar(message: string, type: string) {
    this.snackBar.open(message, 'close', {
      duration: 4000,
      panelClass: [type]
    });
  }


  /**
   * Saves the id of the space user clicked on,
   * to connect it with document component.
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
