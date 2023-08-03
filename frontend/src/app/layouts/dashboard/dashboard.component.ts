import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import EntitesCount from 'src/app/domain/EntitiesCount';
import Log from 'src/app/domain/Log';
import Quota from 'src/app/domain/Quota';
import QuotaUsedToday from 'src/app/domain/QuotaUsedToday';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  quota!: Quota;
  logs!: Log[];
  selectedLog!: Log;
  showModal = false; // Flag to control the visibility of the modal
  popupClass = 'popup';
  popupBackgroundColor = '';
  roles: string[] = [];
  entitiesCount!:EntitesCount;
  quotaUsedToday!:QuotaUsedToday;
  totalAllocatedQuota!:number;

  constructor(private compagnieService: CompagnieService, private tokenStorage: TokenStorageService, private router: Router) {}
  ngOnInit(): void {
    this.compagnieService.getQuotaStatus().subscribe(
      (data) => {
        console.log("quota",data);

        this.quota = data;
      },
      (err) => {
        console.log(err);
      }
    );

    this.compagnieService.getTotalAllocatedQuota().subscribe(
      (data) => {
        console.log("total allocated quota",data);
        this.totalAllocatedQuota = data;
      },
      (err) => {
        console.log(err);
      }
    );

    this.compagnieService.getQuotaUsedToday().subscribe(
      (data) => {
        console.log("quota used today",data);
        this.quotaUsedToday = data;
      },
      (err) => {
        console.log(err);
      }
    );

    this.compagnieService.getCompagnieLogs().subscribe(
      (data) => {
        //get the 5 last logs
        this.logs = data.slice(Math.max(data.length - 4, 0));
        console.log(data);
      },
      (err) => {
        console.log(err);
      }
    );

    this.compagnieService.getEntitesCount().subscribe(
      (data) => {
        this.entitiesCount = data;
      }
    );


  }

  tailleToUnit(taille: number, showUnit: boolean = true, unit: string = 'Go', precision:number): string {
    let tailleInUnit = taille;
    if (unit === 'Ko') {
      tailleInUnit /= 1024;
    } else if (unit === 'Mo') {
      tailleInUnit /= (1024 * 1024);
    } else if (unit === 'Go') {
      tailleInUnit /= (1024 * 1024 * 1024);
    } else if (unit === 'To') {
      tailleInUnit /= (1024 * 1024 * 1024 * 1024);
    }
    const formattedTaille = tailleInUnit.toFixed(precision);
    if (showUnit) {
      return `${formattedTaille} ${unit}`;
    } else {
      return formattedTaille;
    }
  }

  tailleToBestUnit(taille: number, showUnit: boolean = true, precision: number): string {
    let unit = 'Go'; // Start with Gigabytes as the default unit
    let tailleInUnit = taille;

    if (taille >= 1024 * 1024 * 1024 * 1024) {
      unit = 'To';
      tailleInUnit /= (1024 * 1024 * 1024 * 1024);
    } else if (taille >= 1024 * 1024 * 1024) {
      unit = 'Go';
      tailleInUnit /= (1024 * 1024 * 1024);
    } else if (taille >= 1024 * 1024) {
      unit = 'Mo';
      tailleInUnit /= (1024 * 1024);
    } else if (taille >= 1024) {
      unit = 'Ko';
      tailleInUnit /= 1024;
    } else {
      unit = 'B'; // Add Bytes as the smallest unit
    }

    const formattedTaille = tailleInUnit.toFixed(precision);

    if (showUnit) {
      return `${formattedTaille} ${unit}`;
    } else {
      return formattedTaille;
    }
  }

  openPopup(log: Log): void
  {
      this.selectedLog = log;
      this.showModal = true; // Open the modal

      this.popupClass = 'popup open-popup'; // Add or remove CSS class as needed

    }

  hideModal(): void {
    this.showModal = false;
  }

  getLogType(logType: string): string
  {
    switch(logType)
    {
      case 'CRÉER':
        return 'blue-background';
      case 'MODIFIER':
        return 'green-background';
      case 'SUPPRIMER':
          return 'red-background';
      default:
        return logType;
    }
  }

}
