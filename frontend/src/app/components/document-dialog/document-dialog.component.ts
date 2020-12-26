import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Document} from '../../dtos/document';

@Component({
  selector: 'app-document-dialog',
  templateUrl: './document-dialog.component.html',
  styleUrls: ['./document-dialog.component.scss']
})
export class DocumentDialogComponent implements OnInit {
  currentDocument: Document;
  blobUrl: any;
  constructor(public dialogRef: MatDialogRef<DocumentDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
    this.currentDocument = this.data.currentDocument;
    this.blobUrl = this.data.blobUrl;
  }
}
