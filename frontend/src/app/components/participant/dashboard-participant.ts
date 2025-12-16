import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EventService } from '../../services/event';
import { ParticipantService } from '../../services/participant';
import { RouterOutlet, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NavbarComponent } from '../navbar/navbar';

@Component({
  selector: 'app-dashboard-participant',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    RouterLinkActive,
    FormsModule,
    NavbarComponent
  ],
  templateUrl: './dashboard-participant.html',
  styleUrls: ['./dashboard-participant.scss']
})
export class DashboardParticipant implements OnInit {

  events: any[] = [];
  myRegistrations: any[] = [];
  userId: string = '';
  filteredEvents: any[] = [];
  searchTerm: string = '';

  constructor(
    private eventService: EventService,
    private participantService: ParticipantService,
    private router: Router
  ) { }

  ngOnInit() {
    this.userId = localStorage.getItem('userId') || '';

    if (!this.userId) return console.error('Utilisateur non connecté');

    this.eventService.getAll().subscribe({
      next: data => {
        this.events = data;
        this.filteredEvents = data;
      },
      error: err => console.error('Erreur chargement événements:', err)
    });

    this.participantService.getParticipants(this.userId).subscribe({
      next: data => this.myRegistrations = data,
      error: err => console.error('Erreur chargement inscriptions:', err)
    });
  }

  filterEvents() {
    if (!this.searchTerm) {
      this.filteredEvents = this.events;
    } else {
      const lowerTerm = this.searchTerm.toLowerCase();
      this.filteredEvents = this.events.filter(e =>
        e.title.toLowerCase().includes(lowerTerm) ||
        e.location.toLowerCase().includes(lowerTerm) ||
        (e.date && e.date.includes(lowerTerm))
      );
    }
  }

  // Inscription
  affect(eventId: string) {
    const firstName = localStorage.getItem('firstName') || '';
    const lastName = localStorage.getItem('lastName') || '';
    const email = localStorage.getItem('email') || '';
    const event = this.events.find(e => e.id === eventId);
    const eventTitle = event ? event.title : '';

    if (!this.userId || !firstName || !lastName || !email) return;

    // Vérifie si déjà inscrit
    if (this.myRegistrations.some(r => r.eventId === eventId)) {
      alert('Vous êtes déjà inscrit à cet événement !');
      return;
    }

    this.participantService.assignToEvent(this.userId, eventId, firstName, lastName, email, eventTitle)
      .subscribe({
        next: res => {
          this.myRegistrations = [...this.myRegistrations, res];
        },
        error: err => alert(err.error || 'Erreur inscription')
      });
  }

  // Désinscription
  unsubscribe(participantId: string) {
    this.participantService.delete(participantId).subscribe({
      next: () => {
        this.myRegistrations = this.myRegistrations.filter(p => p.id !== participantId);
      },
      error: err => console.error('Erreur désinscription:', err)
    });
  }

  getEventTitle(eventId: string): string {
    const event = this.events.find(e => e.id === eventId);
    return event ? event.title : 'Événement inconnu';
  }

  getEventImage(eventId: string): string | null {
    const event = this.events.find(e => e.id === eventId);
    return event ? event.imageUrl : null;
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
