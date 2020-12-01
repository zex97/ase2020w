import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlashcardManagerComponent } from './flashcard-manager.component';

describe('FlashcardManagerComponent', () => {
  let component: FlashcardManagerComponent;
  let fixture: ComponentFixture<FlashcardManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FlashcardManagerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FlashcardManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
