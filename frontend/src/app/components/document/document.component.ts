import {Component, Input, OnInit, SimpleChanges} from '@angular/core';
import {SpaceService} from '../../services/space.service';
import {Document} from '../../dtos/document';

@Component({
  selector: 'app-document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent implements OnInit {

  documentsOfSpace: Document[] = [];

  constructor(private spaceService: SpaceService) {
  }

  @Input() spaceId: number;

  ngOnInit() {
    // this.loadAllDocuments(this.spaceId);
  }

  ngOnChanges(changes: SimpleChanges) {
    // if selected space changes load the documents for the newly selected space
    this.loadAllDocuments(this.spaceId);
  }

  loadAllDocuments(spaceId: number) {
    this.documentsOfSpace = [];
    this.spaceService.getAllDocuments(localStorage.getItem('currentUser'), spaceId).subscribe(
      (res) => {
        this.documentsOfSpace = res as Document[];
      }
      /*(documentList: Document[]) => {
        this.documentsOfSpace = documentList;
      },*/
    );
  }

  getAllDocuments() {
    return this.documentsOfSpace;
  }

}
