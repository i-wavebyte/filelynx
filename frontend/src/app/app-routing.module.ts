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
import { FilesComponent } from './layouts/files/files.component';
import { AddFileComponent } from './components/add-file/add-file.component';
import { AuthGuard } from './_services/authguard.service';
import { UserDashboardComponent } from './layouts/user-dashboard/user-dashboard.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'user', component: BoardUserComponent },
  { path: 'mod', component: BoardModeratorComponent },
  { path: 'admin', component: BoardAdminComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard]  },
  { path: 'userdashboard', component: UserDashboardComponent,  },

  { path: 'log', component: LogsComponent, canActivate: [AuthGuard] },
  { path: 'groups', component: GroupesComponent , canActivate: [AuthGuard] ,
  children: [{ path: 'add-groupe', component: AddGroupeComponent }],},
  { path: 'users', component: UsersComponent , canActivate: [AuthGuard] ,
  children: [{ path: 'add-collaborateur', component: AddMembreComponent }],},
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  {path : "files", component : FilesComponent, canActivate: [AuthGuard] ,
  children: [{ path: "add-folder/:parentId", component: AddFileComponent }]}

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
