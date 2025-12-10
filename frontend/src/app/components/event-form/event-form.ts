import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { EventService } from '../../services/event';

@Component({
  selector: 'app-event-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './event-form.html',
  styleUrls: ['./event-form.scss']
})
export class EventForm {
  event = {
    title: '',
    description: '',
    date: '',
    location: ''
  };

  imageFile: File | null = null;

  constructor(private eventService: EventService, private router: Router) {}

  onFileSelected(event: any) {
    this.imageFile = event.target.files[0];
  }

  createEvent() {
    const formData = new FormData();
    formData.append('title', this.event.title);
    formData.append('description', this.event.description);
    formData.append('date', this.event.date);
    formData.append('location', this.event.location);
    if (this.imageFile) {
      formData.append('image', this.imageFile);
    }

    this.eventService.createWithImage(formData).subscribe(() => {
      this.router.navigate(['/events']);
    });
  }
}
