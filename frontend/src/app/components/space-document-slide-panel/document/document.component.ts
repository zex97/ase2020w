import {Component, EventEmitter, Input, OnChanges, OnInit, Output, ViewChild} from '@angular/core';
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
  documentOpen: boolean = false;
  documentNameSearch: string = '';

  constructor(private spaceService: SpaceService, private fileUploadService: FileUploadService, private sanitizer: DomSanitizer,
              private dialog: MatDialog, private snackBar: MatSnackBar, private formBuilder: FormBuilder) {
  }

  @Input() space: Space;
  @Output() documentEvents = new EventEmitter();
  @ViewChild('sortOption') sortOption;

  ngOnInit() {
    // this.loadAllDocuments(this.spaceId);
  }

  /**
   * If different space is selected makes sure
   * that the documents are loaded for the new space.
   * */
  async ngOnChanges() {
    // if selected space changes load the documents for the newly selected space
    // console.log('loading all documents of space ' + this.space.name);
    await delay(100);
    this.loadAllDocuments(this.space.id);
    window.scrollTo(0, 0);

    // add delay for the reload of all documents after adding a new one
    function delay(ms: number) {
      return new Promise(resolve => setTimeout(resolve, ms));
    }
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
      if (this.sortOption != null) {
      switch (this.sortOption.value) {
        case 'name-asc': {
          return this.documentsOfSpace.sort((s1, s2) => s1.name.localeCompare(s2.name));
        }
        case 'name-desc': {
          return this.documentsOfSpace.sort((s1, s2) => s2.name.localeCompare(s1.name));
        }
        case 'default': {
          return this.documentsOfSpace;
        }
        default: {
          return this.documentsOfSpace;
        }
      }
    }
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



  /**
   * Fetches a file as resource from the backend
   * */
  loadFile(document: Document) {
    if (this.documentOpen) {
      return;
    }
    this.documentOpen = true;
    this.fileUploadService.getFile(this.space, document.name).subscribe(
      (res) => {
        this.handleFileExtensions(document, res);
        const ref = this.dialog.open(DocumentDialogComponent, {
          data: {
            currentDocument: document,
            blobUrl: this.blobUrl
          }
        });
        ref.afterClosed().subscribe(() => {
          this.documentOpen = false;
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


  searchDocumentsByName() {
    // Uncomment when backend implementation of document search is finished
    /*this.spaceService.getDocumentsByName(localStorage.getItem('currentUser'), this.space.id, this.documentNameSearch).subscribe(
      (documentList: Document[]) => {
        this.documentsOfSpace = documentList;
      },
      error => {
        this.defaultErrorHandling(error);
      }
    );*/
  }

  backToAll() {
    this.loadAllDocuments(this.space.id);
    this.documentNameSearch = '';
  }

}
