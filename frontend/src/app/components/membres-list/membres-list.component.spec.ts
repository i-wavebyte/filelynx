import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MembresListComponent } from './membres-list.component';

describe('MembresListComponent', () => {
  let component: MembresListComponent;
  let fixture: ComponentFixture<MembresListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MembresListComponent]
    });
    fixture = TestBed.createComponent(MembresListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
