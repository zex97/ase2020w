import { Component, OnInit, Input } from '@angular/core';
import {MatChipInputEvent} from '@angular/material/chips';
import { Tag } from 'src/app/dtos/tag';
import { SpaceService } from 'src/app/services/space.service';
import {Document} from 'src/app/dtos/document';

@Component({
  selector: 'app-tag-bar',
  templateUrl: './tag-bar.component.html',
  styleUrls: ['./tag-bar.component.scss']
})
export class TagBarComponent implements OnInit {

  constructor(private spaceService: SpaceService) { }

  @Input() doc: Document;


  ngOnInit(): void {
  }

  add(event: MatChipInputEvent): void {
    console.log("adding tag");
    const input = event.input;
    const value = event.value;

    if ((value || '').trim()) {
      const tagDto = new Tag(value);
      this.spaceService.addTag(tagDto, this.doc.id).subscribe(
        () => {
          this.doc.tags.push(value);

          if (input) {
            input.value = '';
          }
        }
      );
    }
  }

  remove(tag: string): void {
    console.log("removing tag");
    this.spaceService.deleteTag(tag, this.doc.id).subscribe(
      () => {
        const index = this.doc.tags.indexOf(tag, 0);
        if (index > -1) {
          this.doc.tags.splice(index, 1);
        }
      }
    );
  }
}
