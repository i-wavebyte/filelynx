import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { RegisterComponent } from './components/register/register.component';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { ProfileComponent } from './components/profile/profile.component';
import { BoardUserComponent } from './components/board-user/board-user.component';
import { BoardModeratorComponent } from './components/board-moderator/board-moderator.component';
import { BoardAdminComponent } from './components/board-admin/board-admin.component';
import { DashboardComponent } from './layouts/dashboard/dashboard.component';
import { UsersComponent } from './layouts/users/users.component';
import { GroupesComponent } from './layouts/groupes/groupes.component';
import { AddGroupeComponent } from './components/add-groupe/add-groupe.component';
import { AddMembreComponent } from './components/add-membre/add-membre.component';
import { LogsComponent } from './layouts/logs/logs.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'user', component: BoardUserComponent },
  { path: 'mod', component: BoardModeratorComponent },
  { path: 'admin', component: BoardAdminComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'log', component: LogsComponent },
  // { path: 'logs', component: DashboardComponent },
  // { path: 'userdashboard', component: UserDashboardComponent },
  // { path: 'users', component: UsersComponent },
  { path: 'groups', component: GroupesComponent ,
  children: [{ path: 'add-groupe', component: AddGroupeComponent }],},
  { path: 'users', component: UsersComponent ,
  children: [{ path: 'add-collaborateur', component: AddMembreComponent }],},
  { path: '', redirectTo: 'home', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
