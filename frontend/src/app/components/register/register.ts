import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrls: ['./register.scss']
})
export class Register {
  user = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    role: 'participant'
  };

  constructor(private auth: AuthService, private router: Router) {}

  register() {
    this.auth.register({
      firstName: this.user.firstName,
      lastName: this.user.lastName,
      email: this.user.email,
      passwordHash: this.user.password, // le backend attend toujours passwordHash
      role: this.user.role
    }).subscribe(() => {
      this.router.navigate(['/dashboard-organizer']);
    });
  }
}
