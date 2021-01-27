import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Document} from '../../dtos/document';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SpaceService} from '../../services/space.service';
import {MatSnackBar} from '@angular/material/snack-bar';


@Component({
  selector: 'app-document-dialog',
  templateUrl: './document-dialog.component.html',
  styleUrls: ['./document-dialog.component.scss']
})
export class DocumentDialogComponent implements OnInit {

  currentDocument: Document;
  blobUrl: any;
  transcriptionEditForm: FormGroup;
  editState: boolean = false;

  constructor(public dialogRef: MatDialogRef<DocumentDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private formBuilder: FormBuilder, private spaceService: SpaceService,
              private snackBar: MatSnackBar) {
              this.transcriptionEditForm = this.formBuilder.group({
                content: ['', [
                  Validators.required,
                  Validators.minLength(1)
                ]]
              });
  }

  ngOnInit() {
     this.currentDocument = this.data.currentDocument;
     this.blobUrl = this.data.blobUrl;
     this.editState = false;
  }

  prepareEdit() {
    this.editState = true;
    this.transcriptionEditForm.patchValue({
      content: this.currentDocument.transcription
    });
  }

  editTranscription() {
      this.editState = false;
      this.currentDocument.transcription = this.transcriptionEditForm.controls.content.value;
      this.spaceService.editTranscription(this.currentDocument).subscribe(
        () => {
            this.openSnackbar('You successfully edited a transcription!', 'success-snackbar');
          },
        error => {
          this.openSnackbar('Could not edit the transcription!', 'warning-snackbar');
        });
  }

  isTranscriptionDone() {
    // this.dialogRef.componentInstance.data = ;
    return this.currentDocument.transcription.startsWith('Transcription in process...');
  }

  openSnackbar(message: string, type: string) {
    this.snackBar.open(message, 'close', {
      duration: 4000,
      panelClass: [type]
    });
  }

}
