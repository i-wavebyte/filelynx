import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';

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
import { GroupeListComponent } from './components/groupe-list/groupe-list.component';
import { GroupesComponent } from './layouts/groupes/groupes.component';
import { AddGroupeComponent } from './components/add-groupe/add-groupe.component';
import { AddMembreComponent } from './components/add-membre/add-membre.component';
import { MembreListComponent } from './components/membre-list/membre-list.component';
import { LogsComponent } from './layouts/logs/logs.component';
import { LogListComponent } from './components/log-list/log-list.component';
import { FilesComponent } from './layouts/files/files.component';
import { AddFileComponent } from './components/add-file/add-file.component';

import { AuthGuard } from './_services/authguard.service';
import { UserDashboardComponent } from './layouts/user-dashboard/user-dashboard.component';

import { MetadataComponent } from './layouts/metadata/metadata.component';
import { LabelListComponent } from './components/label-list/label-list.component';
import { CategoryListComponent } from './components/category-list/category-list.component';
import { AddLabelComponent } from './components/add-label/add-label.component';
import { AddCategorieComponent } from './components/add-categorie/add-categorie.component';
import { DeleteFolderComponent } from './components/delete-folder/delete-folder.component';
import { HelperService } from './_services/helper.service';
import { MembersettingComponent } from './components/membersetting/membersetting.component';



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
    GroupeListComponent,
    GroupesComponent,
    AddGroupeComponent,
    AddMembreComponent,
    MembreListComponent,
    LogsComponent,
    LogListComponent,
    FilesComponent,
    AddFileComponent,

    UserDashboardComponent,
    MetadataComponent,
    LabelListComponent,
    CategoryListComponent,
    AddLabelComponent,
    AddCategorieComponent,
    DeleteFolderComponent,
    MembersettingComponent
  ],
  imports: [
    NgToastModule,
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    CommonModule
  ],
  providers: [authInterceptorProviders, AuthGuard, HelperService],
  bootstrap: [AppComponent]
})
export class AppModule { }
