import {TestBed} from '@angular/core/testing';

import {FlashcardService} from './flashcard.service';
import {AppModule} from '../app.module';

describe('FlashcardService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    declarations: [],
    imports: [AppModule]
  }));

  it('should be created', () => {
    const service: FlashcardService = TestBed.get(FlashcardService);
    expect(service).toBeTruthy();
  });
});
