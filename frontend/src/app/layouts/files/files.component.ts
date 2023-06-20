import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FolderService } from 'src/app/_services/folder.service';
import { HelperService } from 'src/app/_services/helper.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import Dossier from 'src/app/domain/Dossier';

@Component({
  selector: 'app-files',
  templateUrl: './files.component.html',
  styleUrls: ['./files.component.css']
})
export class FilesComponent implements OnInit{

  currentFolder:Dossier | null = null;
  roles: string[] = [];
  selectedFolder!: Dossier;
  showModal= false;
  popupClass= 'popup';

  constructor(private folderService:FolderService, private tokenStorage: TokenStorageService, private router: Router, private route: ActivatedRoute, private _helper: HelperService) {}

  ngOnInit(): void {
    this.loadFolders();
    this.route.queryParams.subscribe(params => {
      if (params['reload']) {
          console.log('reload');
          this.loadFolders();
      }
    });
  }

  loadFolders(): void{
    // if(this.currentFolder == null){
      this.folderService.getRootFolderAsAdmin().subscribe(
        (data) => {
          this.currentFolder = data;
          console.log(data);
        },
        (err) => {
          console.log(err);
        }
      );
    // }
  }

  onFolderClick(folder:Dossier){
    this.folderService.getFolderByIdAsAdmin(folder.id).subscribe(
      (data) => {
        this.currentFolder = data;
        console.log(data);
      }
    );
  }

  returnToRoot(root:Dossier | undefined){
    this.folderService.getFolderByIdAsAdmin(root?.id).subscribe(
      (data) => {
        this.currentFolder = data;
        console.log(data);
      }
    );
    }

    OnDeleteIconClique(folder: Dossier) {
      var completeFolder: Dossier;
      this.folderService.getFolderByIdAsAdmin(folder?.id).subscribe(
        (data) => {
          completeFolder = data;
          console.log(completeFolder);
        }
      );
      }
    deletePopup(folder: Dossier): void
    {

      var newFolder: Dossier;
      var message1 = "êtes-vous sûr de vouloir supprimer ce dossier ?";
      var message2 = "êtes-vous sûr de vouloir supprimer ce dossier ?\n (Dossier non vide)! "
      this.folderService.getFolderByIdAsAdmin(folder?.id).subscribe(
        (data) => {
          newFolder = data;
          console.log("dossiers: ", newFolder.dossiers.length);
          this._helper.show("", newFolder.dossiers.length > 0 ? message2: message1, folder.id);
        }
      );
    }

    openPopup(folder: Dossier){
      this.selectedFolder = folder;
      this.showModal = true; // Open the modal
      this.popupClass = 'popup open-popup'; // Add or remove CSS class as needed
    }

    hideModal(): void {
      this.showModal = false;
    }
}
