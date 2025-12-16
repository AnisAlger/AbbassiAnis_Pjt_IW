import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { EventService } from '../../services/event';
import { ParticipantService } from '../../services/participant';
import { Chart, registerables } from 'chart.js';
import { NavbarComponent } from '../navbar/navbar';
import { NotificationService } from '../../services/notification';

Chart.register(...registerables);

@Component({
  selector: 'app-organizer-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    NavbarComponent,
    FormsModule
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

  recentActivities: any[] = [];
  registrations: any[] = [];
  chart: any;

  // Announcement Modal State
  showModal = false;
  announcementMessage = '';
  usersList: any[] = [];
  sending = false;

  constructor(
    private router: Router,
    private eventService: EventService,
    private participantService: ParticipantService,
    private notifService: NotificationService
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
        this.updateChartData();
      },
      error: err => console.error(err)
    });

    // 2./3. Participants & Users & Activity
    this.participantService.getAllParticipants().subscribe({
      next: (registrations: any[]) => {
        this.registrations = registrations;
        this.totalRegistrations = registrations.length;
        this.totalParticipants = new Set(registrations.map(r => r.userId)).size;

        // Mock Recent Activity from registrations (taking last 5)
        this.recentActivities = registrations.slice(-5).reverse().map(r => ({
          type: 'inscription',
          message: `${r.firstName} ${r.lastName} s'est inscrit à ${r.eventTitle}`,
          time: 'Récemment'
        }));

        this.updateChartData();
      },
      error: err => console.error(err)
    });

    this.participantService.getAllUsersParticipants().subscribe({
      next: (users: any[]) => {
        this.totalUsers = users.length;
        this.usersList = users;
        this.updateChartData();
      }
    });
  }

  updateChartData() {
    if (this.chart) {
      this.chart.data.datasets[0].data = [
        this.totalEvents,
        this.totalParticipants,
        this.totalUsers,
        this.totalRegistrations
      ];
      this.chart.update();
    } else {
      this.initChart();
    }
  }

  initChart() {
    const ctx = document.getElementById('dashboardChart') as HTMLCanvasElement;
    if (!ctx) return;

    this.chart = new Chart(ctx, {
      type: 'bar', // or 'line'
      data: {
        labels: ['Événements', 'Participants', 'Utilisateurs', 'Inscriptions'],
        datasets: [{
          label: 'Statistiques Globales',
          data: [this.totalEvents, this.totalParticipants, this.totalUsers, this.totalRegistrations],
          backgroundColor: [
            'rgba(59, 130, 246, 0.5)', // Blue
            'rgba(16, 185, 129, 0.5)', // Green
            'rgba(139, 92, 246, 0.5)', // Purple
            'rgba(249, 115, 22, 0.5)'  // Orange
          ],
          borderColor: [
            '#3b82f6', '#10b981', '#8b5cf6', '#f97316'
          ],
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: { display: false }
        },
        scales: {
          y: { beginAtZero: true }
        }
      }
    });
  }



  exportData() {
    if (this.registrations.length === 0) {
      alert('Aucune donnée à exporter.');
      return;
    }

    let csvContent = 'data:text/csv;charset=utf-8,';
    csvContent += 'ID,Nom,Prenom,Email,Evenement,Date Inscription\n';

    this.registrations.forEach(row => {
      const dataString = [
        row.id,
        row.lastName,
        row.firstName,
        row.email,
        row.eventTitle,
        row.registrationDate
      ].join(',');
      csvContent += dataString + '\n';
    });

    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', 'inscriptions_export.csv');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  openAnnouncementModal() {
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
    this.announcementMessage = '';
  }

  sendAnnouncement() {
    if (!this.announcementMessage.trim()) return;
    this.sending = true;

    // Broadcast to ALL users
    let completed = 0;
    const total = this.usersList.length;

    if (total === 0) {
      alert('Aucun utilisateur trouvé.');
      this.sending = false;
      return;
    }

    this.usersList.forEach(user => {
      this.notifService.sendNotification(user.id, this.announcementMessage).subscribe({
        next: () => {
          completed++;
          if (completed === total) this.finishSending();
        },
        error: () => {
          completed++;
          if (completed === total) this.finishSending();
        }
      });
    });
  }

  finishSending() {
    this.sending = false;
    alert('Annonce envoyée avec succès !');
    this.closeModal();
  }

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
