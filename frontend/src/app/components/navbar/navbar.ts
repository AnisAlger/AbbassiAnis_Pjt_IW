import { Component } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { NotificationService, Notification } from '../../services/notification';
import { interval } from 'rxjs';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, DatePipe],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.scss']
})
export class NavbarComponent {

  notifications: Notification[] = [];
  showList = false;
  showNavbar = false; // ← navbar cachée par défaut

  constructor(private notifService: NotificationService, private router: Router) {}

  ngOnInit() {
    // Détecter la route pour afficher la navbar seulement sur certains dashboards
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      const url = event.url;
      this.showNavbar = url.startsWith('/dashboard-organizer') || url.startsWith('/events') || url.startsWith('/dashboard-participant') || url.startsWith('/event-form') || url.startsWith('/register') || url.startsWith('/participants');     

      // Charger notifications seulement si la navbar est visible
      if (this.showNavbar) {
        this.loadNotifications();
      }
    });

    // Rafraîchissement toutes les 10s si navbar visible
    interval(10000).subscribe(() => {
      if (this.showNavbar) this.loadNotifications();
    });
  }

  loadNotifications() {
    this.notifService.getMyNotifications().subscribe({
      next: (res) => this.notifications = res,
      error: (err) => console.error('Erreur chargement notifications', err)
    });
  }

  get unreadCount(): number {
    return this.notifications.filter(n => !n.read).length;
  }

  markAsRead(notification: Notification) {
    if (notification.read) return;

    this.notifService.markAsRead(notification.id).subscribe({
      next: () => notification.read = true,
      error: (err) => console.error('Erreur marquer notification comme lue', err)
    });
  }
}
