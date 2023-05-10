import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import Quota from '../domain/Quota';
import { Observable } from 'rxjs/internal/Observable';
const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})


export class CompagnieService {

  private baseUrl = 'http://localhost:8080/api/v1/compagnie';
  constructor(private http: HttpClient) {}

  getQuotaStatus():Observable<Quota> {
    return this.http.get<Quota>(this.baseUrl + "/getQuotaStatus", httpOptions);
  }



}