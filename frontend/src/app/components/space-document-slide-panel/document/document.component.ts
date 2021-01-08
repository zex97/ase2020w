import {Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
import {SpaceService} from '../../../services/space.service';
import {Document} from '../../../dtos/document';
import {Space} from '../../../dtos/space';
import {FileUploadService} from '../../../services/file-upload.service';
import {DomSanitizer} from '@angular/platform-browser';
import {saveAs} from 'file-saver';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDialogComponent} from '../../confirm-dialog/confirm-dialog.component';
import {MatSnackBar} from '@angular/material/snack-bar';
import {DocumentDialogComponent} from '../../document-dialog/document-dialog.component';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';


@Component({
  selector: 'app-document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss'],
  entryComponents: [ConfirmDialogComponent]
})
export class DocumentComponent implements OnInit, OnChanges {

  documentsOfSpace: Document[] = [];
  error: boolean = false;
  errorMessage: string = '';
  success: boolean = false;
  successMessage: string = '';
  currentDocument: Document;
  fileObject: Blob;
  blobUrl: any;
  docEditForm: FormGroup;

  constructor(private spaceService: SpaceService, private fileUploadService: FileUploadService, private sanitizer: DomSanitizer,
              private dialog: MatDialog, private snackBar: MatSnackBar, private formBuilder: FormBuilder) {
    this.docEditForm = this.formBuilder.group({
      title: ['', [
        Validators.required,
        Validators.minLength(1)
      ]]
    });
  }

  @Input() space: Space;
  @Output() toggleSlideEvent = new EventEmitter();

  ngOnInit() {
    // this.loadAllDocuments(this.spaceId);
  }

  /**
   * If different space is selected makes sure
   * that the documents are loaded for the new space.
   * */
  ngOnChanges() {
    // if selected space changes load the documents for the newly selected space
    // console.log('loading all documents of space ' + this.space.name);
    this.loadAllDocuments(this.space.id);
    window.scrollTo(0, 0);
  }

  loadAllDocuments(spaceId: number) {
    this.documentsOfSpace = [];
    this.spaceService.getAllDocuments(localStorage.getItem('currentUser'), spaceId).subscribe(
      (res) => {
        this.documentsOfSpace = res as Document[];
        this.documentsOfSpace.sort((doc1, doc2) => doc1.name.localeCompare(doc2.name));
      },
      error => {
        this.defaultErrorHandling(error);
      }
    );
  }

  isEmpty() {
    return this.documentsOfSpace?.length === 0;
  }

  getAllDocuments() {
    // console.log(this.documentsOfSpace.length);
    return this.documentsOfSpace;
  }

  deleteDocument(doc: Document) {
    const confirmDialog = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirm Remove Document',
        message: 'Are you sure, you want to remove the document: ' + doc.name
      }
    });
    confirmDialog.afterClosed().subscribe(result => {
      if (result === true) {
        console.log('del document ' + doc.id);
        this.spaceService.deleteDocument(this.space, doc.id).subscribe(
          () => {
            // update frontend display if the delete is successful, no need for another GET
            this.documentsOfSpace = this.documentsOfSpace.filter(document => document.id !== doc.id);
            this.openSnackbar('Successfully deleted ' + doc.name + '!', 'success-snackbar');

            // if document is deleted, then delete the file related to it as well
            this.fileUploadService.deleteFile(this.space, doc.name).subscribe(
              () => {
                console.log('File has been deleted');
              },
              error => {
                this.defaultErrorHandling(error);
              });
          },
          error => {
            this.defaultErrorHandling(error);
          });
      }
    });
  }

  editTranscription(doc: Document) {
    doc.transcription = this.docEditForm.controls.title.value;
    this.spaceService.editTranscription(doc).subscribe(
      () => {
          this.openSnackbar('You successfully edited a transcription!', 'success-snackbar');
        },
      error => {
        this.error = true;
        this.errorMessage = 'Could not edit the transcription!';
        this.openSnackbar(this.errorMessage, 'warning-snackbar');
      });
  }

  /**
   * Fetches a file as resource from the backend
   * */
  loadFile(document: Document) {
    this.fileUploadService.getFile(this.space, document.name).subscribe(
      (res) => {
        this.handleFileExtensions(document, res);
        this.dialog.open(DocumentDialogComponent, {
          data: {
            currentDocument: document,
            blobUrl: this.blobUrl
          }
        });
      },
      error => {
        this.defaultErrorHandling(error);
      }
    );
    this.currentDocument = document;
  }

  handleFileExtensions(document: Document, res) {
    if (document.name.includes('.mp3') || document.name.includes('.mp4')) {
      this.fileObject = res;
      this.blobUrl = URL.createObjectURL(this.fileObject);
    } else if (document.name.includes('.pdf')) {
      this.fileObject = new Blob([res], {type: 'application/pdf'});
      this.blobUrl = URL.createObjectURL(this.fileObject);
    } else {
      this.fileObject = res;
      this.blobUrl = this.sanitizer.bypassSecurityTrustResourceUrl(URL.createObjectURL(this.fileObject));
    }
  }

  downloadFileFromList(document: Document) {
    this.fileUploadService.getFile(this.space, document.name).subscribe(
      (res) => {
        this.handleFileExtensions(document, res);
        saveAs(this.fileObject, document.name);
      },
      error => {
        this.defaultErrorHandling(error);
      }
    );
  }

  openSnackbar(message: string, type: string) {
    this.snackBar.open(message, 'close', {
      duration: 4000,
      panelClass: [type]
    });
  }

  private defaultSuccessHandling(message: string) {
    console.log(message);
    this.success = true;
    this.successMessage = '';
    this.successMessage = message;
  }

  vanishSuccessMessage() {
    this.success = false;
    this.successMessage = '';
  }

  private defaultErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    this.errorMessage = '';
    this.errorMessage = error.error.message;
  }

}
