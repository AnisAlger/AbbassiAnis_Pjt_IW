import { TestBed } from '@angular/core/testing';

import * as authModule from './auth';

describe('Auth', () => {
  let service: any;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    const AuthClass = (authModule as any).Auth ?? (authModule as any).default ?? (authModule as any).AuthService;
    service = TestBed.inject(AuthClass);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
