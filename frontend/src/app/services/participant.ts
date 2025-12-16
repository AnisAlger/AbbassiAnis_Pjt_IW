import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ParticipantService {

  private apiUrl = 'http://localhost:8080/api/participants';

  constructor(private http: HttpClient) { }

  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token') || '';
    return new HttpHeaders({
      'Content-Type': 'application/json',
      Authorization: token ? `Bearer ${token}` : ''
    });
  }

  getAllParticipants(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { headers: this.getAuthHeaders() });
  }

  getParticipants(userId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/user/${userId}`, { headers: this.getAuthHeaders() });
  }

  getAllUsersParticipants(): Observable<any[]> {
    return this.http.get<any[]>('http://localhost:8080/api/users/participants', { headers: this.getAuthHeaders() });
  }

  // ✔ Ajout de eventTitle
  assignToEvent(userId: string, eventId: string, firstName: string, lastName: string, email: string, eventTitle: string): Observable<any> {
    const payload = { userId, eventId, firstName, lastName, email, eventTitle };
    return this.http.post(this.apiUrl, payload, { headers: this.getAuthHeaders() });
  }

  delete(participantId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${participantId}`, { headers: this.getAuthHeaders() });
  }

  getByEvent(eventId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/event/${eventId}`, { headers: this.getAuthHeaders() });
  }
}
