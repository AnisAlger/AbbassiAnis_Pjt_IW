import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ParticipantService } from '../../services/participant';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-participants-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './participants-list.html',
  styleUrls: ['./participants-list.scss']
})
export class ParticipantsList implements OnInit {

  participants: any[] = [];
  registeredUserIds = new Set<string>();

  constructor(private participantService: ParticipantService

  ) {}

  ngOnInit() {

    // 1️⃣ Récupérer la table participants (inscrits)
    this.participantService.getAllParticipants().subscribe({
      next: (data) => {
        this.registeredUserIds = new Set(data.map(p => p.userId));
        console.log("IDs inscrits :", this.registeredUserIds);

        // 2️⃣ Charger les utilisateurs ayant le rôle participant
        this.loadUsers();
      },
      error: (err) => console.error('Erreur backend (table participants) :', err)
    });
  }

  loadUsers() {
  this.participantService.getAllUsersParticipants().subscribe({
    next: (users) => {
      console.log('Users de type participant:', users);

      // Récupérer toutes les inscriptions pour y associer eventTitle
      this.participantService.getAllParticipants().subscribe({
        next: (inscriptions) => {

          this.participants = users.map(u => {
            // 🔹 Récupérer toutes les inscriptions du participant
            const userInscriptions = inscriptions.filter(p => p.userId === u.id);

            // 🔹 Extraire tous les titres d'événements
            const eventTitles = userInscriptions.map(p => p.eventTitle);

            return {
              ...u,
              status: userInscriptions.length > 0 ? 'inscrit' : 'non inscrit',
              eventTitle: eventTitles.join(', ') // Tous les titres séparés par une virgule
            };
          });

        },
        error: (err) => console.error('Erreur récupération inscriptions:', err)
      });

    },
    error: (err) => console.error('Erreur backend (users participants):', err)
  });
}


}
