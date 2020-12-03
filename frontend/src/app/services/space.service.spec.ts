import {TestBed} from '@angular/core/testing';

import {SpaceService} from './space.service';
import {AppModule} from '../app.module';

describe('SpaceService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    declarations: [],
    imports: [AppModule]
  }));

  it('should be created', () => {
    const service: FlashcardService = TestBed.get(FlashcardService);
    expect(service).toBeTruthy();
  });
});
