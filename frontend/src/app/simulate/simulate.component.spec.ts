import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SimulateComponent } from './simulate.component';

describe('PlateComponent', () => {
  let component: SimulateComponent;
  let fixture: ComponentFixture<SimulateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SimulateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SimulateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
