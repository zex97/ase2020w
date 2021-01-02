import { Component, OnInit, Input } from '@angular/core';
import {MatChipInputEvent, MatChipsModule} from '@angular/material/chips';
import { Tag } from 'src/app/dtos/Tag';
import { SpaceService } from 'src/app/services/space.service';
import {Document} from 'src/app/dtos/document';

@Component({
  selector: 'app-tag-bar',
  templateUrl: './tag-bar.component.html',
  styleUrls: ['./tag-bar.component.scss']
})
export class TagBarComponent implements OnInit {

  tags: string[] = ['Lemon'];

  constructor(private spaceService: SpaceService) { }
  @Input() doc: Document;


  ngOnInit(): void {
  }

  add(event: MatChipInputEvent): void {
    // local test
    console.log("adding tag")
    const input = event.input;
    const value = event.value;

  
    if ((value || '').trim()) {
      this.tags.push(value.trim());
    }

  
    if (input) {
      input.value = '';
    }

  }

  remove(tag: string, doc: Document): void {
    console.log("removing tag")
    const deletedTag = new Tag(tag);
    this.spaceService.deleteTag(deletedTag, doc.id);
  }
}
