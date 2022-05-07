import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MatSliderModule} from '@angular/material/slider';
import {HomeComponent} from './home/home.component';
import {FaqComponent} from './faq/faq.component';
import {SimulateComponent} from './simulate/simulate.component';

const routes: Routes = [
  { path: '', redirectTo: 'home',  pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'faq', component: FaqComponent},
  { path: 'simulate', component: SimulateComponent},
  { path: '**', component: HomeComponent },

];

@NgModule({
  imports: [RouterModule.forRoot(routes), MatSliderModule],
  exports: [RouterModule]
})
export class AppRoutingModule { }
