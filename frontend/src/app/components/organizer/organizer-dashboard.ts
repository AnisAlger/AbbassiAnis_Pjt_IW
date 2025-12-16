import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { EventService } from '../../services/event';
import { ParticipantService } from '../../services/participant';
import { NavbarComponent } from '../navbar/navbar';

@Component({
  selector: 'app-organizer-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    NavbarComponent
  ],
  templateUrl: './organizer-dashboard.html',
  styleUrls: ['./organizer-dashboard.scss']
})
export class OrganizerDashboard implements OnInit {

  firstName = '';
  lastName = '';
  initials = '';

  totalEvents = 0;
  totalParticipants = 0;
  totalUsers = 0;
  totalRegistrations = 0;

  constructor(
    private router: Router,
    private eventService: EventService,
    private participantService: ParticipantService
  ) { }

  ngOnInit(): void {
    this.firstName = localStorage.getItem('firstName') || '';
    this.lastName = localStorage.getItem('lastName') || '';

    this.initials = (this.firstName.charAt(0) + this.lastName.charAt(0)).toUpperCase();

    // Chargement des stats réelles
    this.loadStats();
  }

  loadStats() {
    // 1. Total Evenements
    this.eventService.getAll().subscribe({
      next: (events: any[]) => {
        this.totalEvents = events.length;
      },
      error: err => console.error(err)
    });

    // 2. Total Inscriptions (Registrations)
    this.participantService.getAllParticipants().subscribe({
      next: (registrations: any[]) => {
        this.totalRegistrations = registrations.length;

        // Calculer les participants uniques ayant au moins une inscription
        const uniqueParticipants = new Set(registrations.map(r => r.userId));
        this.totalParticipants = uniqueParticipants.size;
      },
      error: err => console.error(err)
    });

    // 3. Total Utilisateurs (Rôle participant)
    this.participantService.getAllUsersParticipants().subscribe({
      next: (users: any[]) => {
        this.totalUsers = users.length;
      },
      error: err => console.error(err)
    });
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
