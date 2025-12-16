import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { EventService } from '../../services/event';
import { ParticipantService } from '../../services/participant';
import { Chart, registerables } from 'chart.js';
import { NavbarComponent } from '../navbar/navbar';

Chart.register(...registerables);

@Component({
  selector: 'app-organizer-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    NavbarComponent
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

  constructor(
    private router: Router,
    private eventService: EventService,
    private participantService: ParticipantService
  ) { }

  ngOnInit(): void {
    this.firstName = localStorage.getItem('firstName') || '';
    this.lastName = localStorage.getItem('lastName') || '';

    this.initials = (this.firstName.charAt(0) + this.lastName.charAt(0)).toUpperCase();

    // Chargement des stats réelles
    this.loadStats();
  }

  recentActivities: any[] = [];
  chart: any;

  // ... constructor ...

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
        this.totalRegistrations = registrations.length;
        this.totalParticipants = new Set(registrations.map(r => r.userId)).size;

        // Mock Recent Activity from registrations (taking last 5)
        // Assuming registration has some ID or we just take top 5 if API returns sorted
        this.recentActivities = registrations.slice(-5).reverse().map(r => ({
          type: 'inscription',
          message: `${r.firstName} ${r.lastName} s'est inscrit à ${r.eventTitle}`,
          time: 'Récemment' // No date in current endpoint result shown previously
        }));

        this.updateChartData();
      },
      error: err => console.error(err)
    });

    this.participantService.getAllUsersParticipants().subscribe({
      next: (users: any[]) => {
        this.totalUsers = users.length;
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
    // Ensure element exists (simple check or timeout)
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

  logout() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
