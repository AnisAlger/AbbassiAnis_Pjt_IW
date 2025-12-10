import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { EventService } from '../../services/event';

@Component({
  selector: 'app-event-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './event-detail.html',
  styleUrls: ['./event-detail.scss']
})
export class EventDetail {
  event: any;
  editMode: boolean = false;
  editedEvent: any;
  imageFile: File | null = null; // pour la nouvelle image

  constructor(
    private route: ActivatedRoute,
    private eventService: EventService,
    private router: Router
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.params['id'];
    this.loadEvent(id);
  }

  loadEvent(id: string) {
    this.eventService.getById(id).subscribe({
      next: (res: any) => {
        if (res.date) res.date = new Date(res.date);
        this.event = res;
      },
      error: (err) => console.error('Erreur chargement événement:', err)
    });
  }

  // 🔹 Passer en mode édition
  editEvent() {
    this.editMode = true;
    this.editedEvent = { ...this.event };
    this.imageFile = null;
  }

  // 🔹 Annuler modification
  cancelEdit() {
    this.editMode = false;
    this.editedEvent = null;
    this.imageFile = null;
  }

  // 🔹 Gestion sélection image
  onFileSelected(event: any) {
    this.imageFile = event.target.files[0];
  }

  // 🔹 Enregistrer modifications
  updateEvent() {
  if (this.imageFile) {
    const formData = new FormData();
    formData.append('title', this.editedEvent.title);
    formData.append('description', this.editedEvent.description);

    // Conversion date sécurisée
    const dateObj = new Date(this.editedEvent.date);
    formData.append('date', isNaN(dateObj.getTime()) ? this.editedEvent.date : dateObj.toISOString());

    formData.append('location', this.editedEvent.location);
    formData.append('image', this.imageFile);

    this.eventService.updateWithImage(this.event.id, formData).subscribe({
      next: (res) => {
        this.event = res;
        this.editMode = false;
        this.editedEvent = null;
        this.imageFile = null;
      },
      error: (err) => console.error('Erreur mise à jour:', err)
    });
    return;
  }

  // Update classique si pas d'image
  const updatedPayload = {
    ...this.editedEvent,
    date: new Date(this.editedEvent.date)
  };

  this.eventService.update(this.event.id, updatedPayload).subscribe({
    next: (res) => {
      this.event = res;
      this.editMode = false;
      this.editedEvent = null;
    },
    error: (err) => console.error('Erreur mise à jour:', err)
  });
}



  // 🔹 Supprimer événement
  deleteEvent() {
    if (!confirm(`Voulez-vous supprimer l'événement : ${this.event.title} ?`)) return;

    this.eventService.delete(this.event.id).subscribe({
      next: () => this.router.navigate(['/events']),
      error: (err) => console.error('Erreur suppression:', err)
    });
  }
}
