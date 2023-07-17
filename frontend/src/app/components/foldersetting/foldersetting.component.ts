import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-foldersetting',
  templateUrl: './foldersetting.component.html',
  styleUrls: ['./foldersetting.component.css']
})
export class FoldersettingComponent {
  checkboxes = [
    { id: 1, description: 'Visibilité' , checked: true},
    { id: 2, description: 'Télechargement (up)', checked: false },
    { id: 3, description: 'Suppression' , checked: true},
    { id: 4, description: 'Partage', checked: false },
    { id: 5, description: 'Creation de dossiers', checked: true },
    { id: 6, description: 'Modification', checked: true },
    { id: 7, description: 'Télechargement (down)', checked: false },
    // Add more checkboxes as needed
  ];

  constructor(private router: Router, private route: ActivatedRoute) {}
  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const folderId = params['folderId'];
      console.log(folderId);
      // Use the folderId as needed
    });

  }
  
  closePopup() {

  }
  
  
  
  
  
  
}
