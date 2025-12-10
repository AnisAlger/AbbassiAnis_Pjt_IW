import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-organizer-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive
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

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.firstName = localStorage.getItem('firstName') || '';
    this.lastName  = localStorage.getItem('lastName') || '';

    this.initials = (this.firstName.charAt(0) + this.lastName.charAt(0)).toUpperCase();

    // TODO 🔥 remplacer par des vraies valeurs venant du backend
    this.totalEvents = 12;
    this.totalParticipants = 53;
    this.totalUsers = 8;
    this.totalRegistrations = 137;
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
