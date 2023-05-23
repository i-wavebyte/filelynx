import { Component, OnInit } from '@angular/core';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import Log from 'src/app/domain/Log';
import Quota from 'src/app/domain/Quota';

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



  constructor(private compagnieService: CompagnieService) {}
  ngOnInit(): void {
    this.compagnieService.getQuotaStatus().subscribe(
      (data) => {
        this.quota = data;
        console.log(data);
      },
      (err) => {
        console.log(err);
      }
    );

    this.compagnieService.getCompagnieLogs().subscribe(
      (data) => {
        //get the 5 last logs
        this.logs = data.slice(Math.max(data.length - 5, 0));
        console.log(data);
      },
      (err) => {
        console.log(err);
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

  openPopup(log: Log): void
  {
      this.selectedLog = log;
      this.showModal = true; // Open the modal
      // alert(this.selectedLog.date.toString().slice(0, 10));
      this.popupClass = 'popup open-popup'; // Add or remove CSS class as needed

    }

  hideModal(): void {
    this.showModal = false;
  }

}
