import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';


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
    // Remove the default 'Content-Type' header to let Angular set it automatically as 'multipart/form-data'
    // This will ensure that the request is sent as a multipart request.
    headers.delete('Content-Type');
    console.log("formData: ", formData);

    return this.http.post<any>(this.baseUrl + "/upload", formData, { headers });
  }
}
