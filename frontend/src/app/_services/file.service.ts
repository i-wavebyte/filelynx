import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import Fichier from '../domain/Fichier';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class FileService {
  private baseUrl = 'http://localhost:8080/api/v1/fichier';
  constructor(private http: HttpClient) { }

 
  upload(formData: FormData): Observable<any> {
    const headers = new HttpHeaders();
    headers.delete('Content-Type');
    return this.http.post<any>(this.baseUrl + "/upload",formData, { headers });
  }

  // getFileById(fileId: number): Observable<Fichier>
  // {

  // }
}
