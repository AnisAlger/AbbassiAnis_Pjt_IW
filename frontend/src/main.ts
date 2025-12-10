import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter, Routes } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

import { App } from './app/app';

import { Login } from './app/components/login/login';
import { Register } from './app/components/register/register';
import { EventList } from './app/components/event-list/event-list';
import { EventDetail } from './app/components/event-detail/event-detail';
import { EventForm } from './app/components/event-form/event-form';
import { OrganizerDashboard } from './app/components/organizer/organizer-dashboard';
import { ParticipantsList } from './app/components/participants-list/participants-list';
import { DashboardParticipant } from './app/components/participant/dashboard-participant';
import { NavbarComponent } from './app/components/navbar/navbar';

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  { path: 'login', component: Login },
  { path: 'register', component: Register },

  { path: 'events', component: EventList },
  { path: 'events/:id', component: EventDetail },

  { path: 'event-form', component: EventForm },

  { path: 'participants', component: ParticipantsList },
  { path: 'navbar', component: NavbarComponent },

  { path: 'dashboard-organizer', component: OrganizerDashboard },
  { path: 'dashboard-participant', component: DashboardParticipant },

  { path: '**', redirectTo: 'login', pathMatch: 'full' }
];

bootstrapApplication(App, {
  providers: [
    provideRouter(routes),
    provideHttpClient()
  ]
});
