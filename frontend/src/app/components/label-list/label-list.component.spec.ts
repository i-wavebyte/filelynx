import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LabelListComponent } from './label-list.component';

describe('LabelListComponent', () => {
  let component: LabelListComponent;
  let fixture: ComponentFixture<LabelListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LabelListComponent]
    });
    fixture = TestBed.createComponent(LabelListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
