import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class UserService {

    private apiUrl = 'http://localhost:8080/api/users';

    constructor(private http: HttpClient) { }

    private getAuthHeaders(): HttpHeaders {
        const token = localStorage.getItem('token') || '';
        return new HttpHeaders({
            'Content-Type': 'application/json',
            Authorization: token ? `Bearer ${token}` : ''
        });
    }

    delete(userId: string): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${userId}`, { headers: this.getAuthHeaders() });
    }
}
