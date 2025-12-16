import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ParticipantService } from '../../services/participant';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user';

import { NavbarComponent } from '../navbar/navbar';

@Component({
  selector: 'app-participants-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, NavbarComponent],
  templateUrl: './participants-list.html',
  styleUrls: ['./participants-list.scss']
})
export class ParticipantsList implements OnInit {

  participants: any[] = [];
  registeredUserIds = new Set<string>();

  constructor(
    private participantService: ParticipantService,
    private userService: UserService
  ) { }

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

              // 🔹 Map event titles with their IDs for unregistering specific events
              const eventTags = userInscriptions.map(p => ({
                id: p.id,
                title: p.eventTitle
              }));

              // Titles string for display fallback (optional)
              const eventTitles = eventTags.map(t => t.title).join(', ');

              return {
                ...u,
                status: userInscriptions.length > 0 ? 'inscrit' : 'non inscrit',
                eventTitle: eventTitles, // Legacy, can keep for debug
                registrations: eventTags // New array for interactive tags
              };
            });

          },
          error: (err) => console.error('Erreur récupération inscriptions:', err)
        });

      },
      error: (err) => console.error('Erreur backend (users participants):', err)
    });
  }

  // 🗑️ Désinscrire d'un événement spécifique
  unregister(registrationId: string) {
    if (!confirm('Voulez-vous vraiment désinscrire cet utilisateur de cet événement ?')) return;

    this.participantService.delete(registrationId).subscribe({
      next: () => {
        alert('Désinscription réussie');
        this.ngOnInit(); // Reload data
      },
      error: (err) => alert('Erreur lors de la désinscription')
    });
  }

  // 🗑️ Supprimer l'utilisateur définitivement
  deleteUser(userId: string) {
    if (!confirm('ATTENTION: Cela supprimera définitivement le compte utilisateur. Continuer ?')) return;

    this.userService.delete(userId).subscribe({
      next: () => {
        alert('Utilisateur supprimé avec succès');
        this.ngOnInit(); // Reload data
      },
      error: (err) => alert('Erreur lors de la suppression de l\'utilisateur')
    });
  }
}
