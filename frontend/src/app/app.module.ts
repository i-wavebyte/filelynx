import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { HomeComponent } from './components/home/home.component';
import { ProfileComponent } from './components/profile/profile.component';
import { BoardAdminComponent } from './components/board-admin/board-admin.component';
import { BoardModeratorComponent } from './components/board-moderator/board-moderator.component';
import { BoardUserComponent } from './components/board-user/board-user.component';

import { authInterceptorProviders } from './_helpers/auth.interceptor';
import { EventBusService } from './_shared/event-bus.service';
import { DashboardComponent } from './layouts/dashboard/dashboard.component';
import { NgToastModule } from 'ng-angular-popup';
import { DashboardHeaderComponent } from './components/dashboard-header/dashboard-header.component';
import { UsersComponent } from './layouts/users/users.component';
import { MembresListComponent } from './components/membres-list/membres-list.component';
import { GroupeListComponent } from './components/groupe-list/groupe-list.component';
import { GroupesComponent } from './layouts/groupes/groupes.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    HomeComponent,
    ProfileComponent,
    BoardAdminComponent,
    BoardModeratorComponent,
    BoardUserComponent,
    DashboardComponent,
    DashboardHeaderComponent,
    UsersComponent,
    MembresListComponent,
    GroupeListComponent,
    GroupesComponent
  ],
  imports: [
    NgToastModule,
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [authInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule { }
