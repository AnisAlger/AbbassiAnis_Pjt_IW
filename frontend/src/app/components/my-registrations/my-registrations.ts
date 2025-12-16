import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar';
import { ParticipantService } from '../../services/participant';
import { catchError, map, of } from 'rxjs';

@Component({
    selector: 'app-my-registrations',
    standalone: true,
    imports: [CommonModule, RouterModule, NavbarComponent],
    templateUrl: './my-registrations.html',
    styleUrls: ['./my-registrations.scss']
})
export class MyRegistrations implements OnInit {
    myRegistrations: any[] = [];
    loading = true;
    userId: string | null = null;

    constructor(private participantService: ParticipantService) { }

    ngOnInit() {
        this.userId = localStorage.getItem('userId');
        if (this.userId) {
            this.loadRegistrations();
        } else {
            this.loading = false;
        }
    }

    loadRegistrations() {
        this.loading = true;
        // Get all participants (inscriptions) is the current API structure
        // We should probably filter by user ID on frontend or have a backend endpoint
        // Looking at participant.ts, getParticipants(userId) exists!

        if (!this.userId) return;

        this.participantService.getParticipants(this.userId).subscribe({
            next: (data) => {
                this.myRegistrations = data;
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading registrations', err);
                this.loading = false;
            }
        });
    }

    unregister(registrationId: string) {
        if (!confirm('Êtes-vous sûr de vouloir vous désinscrire de cet événement ?')) return;

        this.participantService.delete(registrationId).subscribe({
            next: () => {
                // Optimistic update
                this.myRegistrations = this.myRegistrations.filter(r => r.id !== registrationId);
            },
            error: (err) => alert("Erreur lors de la désinscription")
        });
    }
}
