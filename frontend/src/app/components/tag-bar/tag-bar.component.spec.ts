import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TagBarComponent } from './tag-bar.component';

describe('TagBarComponent', () => {
  let component: TagBarComponent;
  let fixture: ComponentFixture<TagBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TagBarComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TagBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
