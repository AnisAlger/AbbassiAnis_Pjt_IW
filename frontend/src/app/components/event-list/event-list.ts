import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { EventService } from '../../services/event';
import { ParticipantService } from '../../services/participant';

import { NavbarComponent } from '../navbar/navbar';

@Component({
  selector: 'app-event-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, NavbarComponent],
  templateUrl: './event-list.html',
  styleUrls: ['./event-list.scss']
})
export class EventList {

  events: any[] = [];
  participants: any[] = [];
  userId: string = '';

  constructor(
    private eventService: EventService,
    private participantService: ParticipantService,
    private router: Router
  ) { }

  ngOnInit() {
    this.userId = localStorage.getItem('userId') || '';

    if (!this.userId) {
      console.error('Utilisateur non connecté');
      return;
    }

    this.loadEvents();

    this.participantService.getParticipants(this.userId).subscribe(res => {
      this.participants = res;
    });
  }

  // Charger les événements
  loadEvents() {
    this.eventService.getAll().subscribe(res => {
      this.events = res;
    });
  }
}
