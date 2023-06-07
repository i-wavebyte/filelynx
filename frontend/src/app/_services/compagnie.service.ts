import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import Quota from '../domain/Quota';
import { Observable } from 'rxjs/internal/Observable';
import Log from '../domain/Log';
import UserRegister from '../domain/UserRegister';
import { PageResponse } from '../domain/PageRespone';
import Groupe from '../domain/Groupe';
import Membre from '../domain/Membre';
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

  getCompagnieLogs(): Observable<Log[]> {
    return this.http.get<Log[]>(this.baseUrl + "/getCompagnieLogs", httpOptions);
  }

  addMembre(user: UserRegister): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/RegisterMembre`, user, httpOptions);
  }

  changeMemberGroup(username: string, groupe: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/ChangeMemberGroup/${username}/${groupe}`, httpOptions);
  }

  createGroup(groupe: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/createGroup/${groupe}`, httpOptions);
  }

  deleteGroupe(groupe: string): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/deleteGroupe/${groupe}`, httpOptions);
  }

  updateGroupe(groupeId: number,newName: string): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/updateGroupe/${groupeId}/${newName}`, httpOptions);
  }

  updateMembre(membre: Membre): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/updateMembre`, membre, httpOptions);
  }

  deleteMembre(membreId: number, username: string): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/deleteMembre/${membreId}/${username}`, httpOptions);
  }

  getGroupesPage(
    page: number,
    size: number,
    sortBy: string,
    sortOrder: string,
    searchQuery: string
  ): Observable<PageResponse<Groupe>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortOrder', sortOrder);

    if (searchQuery) {
      params = params.set('searchQuery', searchQuery);
    }

    return this.http.get<PageResponse<Groupe>>(
      `${this.baseUrl}/getGroups`,
      { params }
    );
  }

  getMembresPage(
    page: number,
    size: number,
    sortBy: string,
    sortOrder: string,
    searchQuery: string,
    groupFilter: string
  ): Observable<PageResponse<Membre>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortOrder', sortOrder);

    if (searchQuery) {
      params = params.set('searchQuery', searchQuery);
    }

    if (groupFilter) {
      params = params.set('groupFilter', groupFilter);
    }

    return this.http.get<PageResponse<Membre>>(
      `${this.baseUrl}/getUsers`,
      { params }
    );
  }

  getAllUniqueGroups(): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseUrl}/distinctGroups`);
  }
}
