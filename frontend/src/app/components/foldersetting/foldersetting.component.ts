import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import { FolderService } from 'src/app/_services/folder.service';
import Dossier from 'src/app/domain/Dossier';
import Groupe from 'src/app/domain/Groupe';
import Membre from 'src/app/domain/Membre';

@Component({
  selector: 'app-foldersetting',
  templateUrl: './foldersetting.component.html',
  styleUrls: ['./foldersetting.component.css']
})
export class FoldersettingComponent {
  checkboxeUser = [
    { id: 1, description: 'Visibilité' , checked: true},
    { id: 2, description: 'Chargement', checked: false },
    { id: 3, description: 'Suppression' , checked: true},
    { id: 4, description: 'Partage', checked: false },
    { id: 5, description: 'Creation de dossiers', checked: true },
    { id: 6, description: 'Modification', checked: true },
    { id: 7, description: 'Téléchargement', checked: false },
    // Add more checkboxes as needed
  ];

  checkboxeGroupe = [
    { id: 1, description: 'Visibilité' , checked: true},
    { id: 2, description: 'Chargement', checked: false },
    { id: 3, description: 'Suppression' , checked: true},
    { id: 4, description: 'Partage', checked: false },
    { id: 5, description: 'Creation de dossiers', checked: true },
    { id: 6, description: 'Modification', checked: true },
    { id: 7, description: 'Téléchargement', checked: false },
    // Add more checkboxes as needed
  ];

  membres!: Membre[];
  folderId!: number;
  dossier!:Dossier;
  groupe!:Groupe;
  constructor(private router: Router, private route: ActivatedRoute, private folderService:FolderService,private compagnieService:CompagnieService) {}
  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.folderId = params['folderId'];
      console.log(this.folderId);
      this.folderService.getFolderByIdAsAdmin(this.folderId).subscribe(data => {
        this.dossier = data;
        this.groupe = this.dossier.groupe;
        this.compagnieService.getMembresByGroup(this.dossier.groupe.id).subscribe(data => {
          this.membres = data;
          console.log(this.membres);
        });
        console.log(this.dossier);
      }
      );

    });

  }

  openPopup() {

  }
}
