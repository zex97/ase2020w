import {Component, OnInit, Input} from '@angular/core';

@Component({
  selector: 'app-document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.scss']
})
export class DocumentComponent implements OnInit {

  constructor() { }

  @Input() spaceId: number

  ngOnInit() {
  }

}
