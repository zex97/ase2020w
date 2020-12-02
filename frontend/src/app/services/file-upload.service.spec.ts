import {TestBed} from '@angular/core/testing';

import {UserService} from './user.service';
import {AppModule} from '../app.module';

describe('FileUploadService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    declarations: [],
    imports: [AppModule]
  }));

  it('should be created', () => {
    const service: UserService = TestBed.get(UserService);
    expect(service).toBeTruthy();
  });
});
