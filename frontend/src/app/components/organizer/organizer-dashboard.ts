import { Component } from '@angular/core';
import { RouterOutlet, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-organizer-dashboard',
  standalone: true,
  imports: [
    CommonModule, 
    RouterOutlet, 
    RouterLink,        // ← Ajouter RouterLink
    RouterLinkActive   // ← Ajouter RouterLinkActive pour la classe active
  ],
  templateUrl: './organizer-dashboard.html',
  styleUrls: ['./organizer-dashboard.scss']
})
export class OrganizerDashboard {

  constructor(private router: Router) {}

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}