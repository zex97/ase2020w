import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpaceDocumentSlidePanelComponent } from './space-document-slide-panel.component';

describe('SpaceDocumentSlidePanelComponent', () => {
  let component: SpaceDocumentSlidePanelComponent;
  let fixture: ComponentFixture<SpaceDocumentSlidePanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SpaceDocumentSlidePanelComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SpaceDocumentSlidePanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
