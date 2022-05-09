import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MatSliderModule} from '@angular/material/slider';
import {HomeComponent} from './home/home.component';
import {SimulateComponent} from './simulate/simulate.component';

const routes: Routes = [
  { path: '', redirectTo: 'home',  pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'simulate', component: SimulateComponent},
  { path: '**', component: HomeComponent },

];

@NgModule({
  imports: [RouterModule.forRoot(routes), MatSliderModule],
  exports: [RouterModule]
})
export class AppRoutingModule { }
