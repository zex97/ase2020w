import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {SpaceService} from '../../services/space.service';
import {Document} from '../../dtos/document';
import {Space} from '../../dtos/space';
import {FileUploadService} from '../../services/file-upload.service';

@Component({
  selector: 'app-document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent implements OnInit, OnChanges {

  documentsOfSpace: Document[] = [];
  error: boolean = false;
  errorMessage: string = '';
  success: boolean = false;
  successMessage: string = '';

  constructor(private spaceService: SpaceService, private fileUploadService: FileUploadService) {
  }

  @Input() space: Space;

  ngOnInit() {
    // this.loadAllDocuments(this.spaceId);
  }

  /**
   * If different space is selected makes sure
   * that the documents are loaded for the new space.
   * */
  ngOnChanges() {
    // if selected space changes load the documents for the newly selected space
    this.loadAllDocuments(this.space.id);
  }

  loadAllDocuments(spaceId: number) {
    this.documentsOfSpace = [];
    this.spaceService.getAllDocuments(localStorage.getItem('currentUser'), spaceId).subscribe(
      (res) => {
        this.documentsOfSpace = res as Document[];
      },
      error => {
        this.defaultErrorHandling(error);
      }
    );
  }

  getAllDocuments() {
    return this.documentsOfSpace;
  }

  deleteDocument(doc: Document) {
    console.log('del document ' + doc.id);
    this.spaceService.deleteDocument(this.space, doc.id).subscribe(
      () => {
        // update frontend display if the delete is successful, no need for another GET
        this.documentsOfSpace = this.documentsOfSpace.filter(document => document.id !== doc.id);
        this.defaultSuccessHandling('You successfully deleted document ' + doc.name);

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
