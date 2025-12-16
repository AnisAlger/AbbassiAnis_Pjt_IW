import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

// Composants standalone
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { EventList } from './components/event-list/event-list';
import { EventDetail } from './components/event-detail/event-detail';
import { EventForm } from './components/event-form/event-form';
import { OrganizerDashboard } from './components/organizer/organizer-dashboard';
import { ParticipantsList } from './components/participants-list/participants-list';
import { DashboardParticipant } from './components/participant/dashboard-participant';

// Navbar avec notifications
import { NavbarComponent } from './components/navbar/navbar';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterOutlet,
    HttpClientModule,

    // Navbar globale
    NavbarComponent,

    // Pages
    Login,
    Register,
    EventList,
    EventDetail,
    OrganizerDashboard,
    EventForm,
    ParticipantsList,
    DashboardParticipant,

  ],
  templateUrl: './app.html',
  styleUrls: ['./app.scss']
})
export class App { }
