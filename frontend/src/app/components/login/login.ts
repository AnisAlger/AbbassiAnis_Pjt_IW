import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class Login {
  email = '';
  password = '';

  constructor(private auth: AuthService, private router: Router) {}

  login() {
    this.auth.login({ email: this.email, passwordHash: this.password })
      .subscribe({
        next: (res: any) => {
          console.log('Login response:', res);

          // Stocker le token
          this.auth.saveToken(res.token);

          // Stocker les infos utilisateur
          const user = res.user || res;
          localStorage.setItem('userId', user?.id || user?._id || '');
          localStorage.setItem('firstName', user?.firstName || '');
          localStorage.setItem('lastName', user?.lastName || '');
          localStorage.setItem('email', user?.email || '');
          localStorage.setItem('token', res.token);

          // Redirection selon rôle
          const role = this.auth.getRoleFromToken(res.token);
          if (role === 'organizer') {
            this.router.navigate(['/dashboard-organizer']);
          } else if (role === 'participant') {
            this.router.navigate(['/dashboard-participant']);
          }
        },
        error: (err) => console.error('Erreur login:', err)
      });
  }
}
