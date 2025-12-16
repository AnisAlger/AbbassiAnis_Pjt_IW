import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class EventService {
  private apiUrl = 'http://localhost:8080/api/events';

  constructor(private http: HttpClient) { }

  createWithImage(eventData: FormData): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = {
      Authorization: `Bearer ${token}`
      // Content-Type laissé à Angular
    };
    return this.http.post(this.apiUrl, eventData, { headers });
  }

  update(eventId: string, event: any): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.put(`${this.apiUrl}/${eventId}`, event, { headers });
  }

  delete(eventId: string): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.delete(`${this.apiUrl}/${eventId}`, { headers });
  }

  updateWithImage(eventId: string, eventData: FormData): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = { Authorization: `Bearer ${token}` };
    return this.http.put(`${this.apiUrl}/${eventId}`, eventData, { headers });
  }


  getAll(): Observable<any> {
    return this.http.get(this.apiUrl);
  }

  getById(id: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }
}
