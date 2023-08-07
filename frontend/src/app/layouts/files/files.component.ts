import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';
import { FolderService } from 'src/app/_services/folder.service';
import { HelperService } from 'src/app/_services/helper.service';
import { TokenStorageService } from 'src/app/_services/token-storage.service';
import Dossier from 'src/app/domain/Dossier';
import Fichier from 'src/app/domain/Fichier';

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

  constructor(private folderService:FolderService, private tokenStorage: TokenStorageService, private router: Router, private route: ActivatedRoute, private location: Location, private _helper: HelperService,
    private cdRef: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadFolders();
    this.route.queryParams.subscribe(params => {
      if (params['reload']) {
        this.loadFolders();
      }
    });
  }

  loadFolders(): void{
    
    if(this.currentFolder == null){
      this.folderService.getRootFolderAsAdmin().subscribe(
        (data) => {
          this.currentFolder = data;
          console.log(data);
        },
        (err) => {
          console.log(err);
        }
      );
    }
    else{
      this.onFolderClick(this.currentFolder);
    }
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
          this.location.replaceState(`/files/${folder.id}`);
        }
      );
      }

     deletePopup(folder: Dossier): void
    {
      var newFolder: Dossier;
      var f: Dossier;
      var message1 = "êtes-vous sûr de vouloir supprimer le dossier "+ folder.nom+" ?";
      var message2 = "êtes-vous sûr de vouloir supprimer "+ folder.nom+ " ?"+" (Dossier non vide)! "
      this.folderService.getFolderByIdAsAdmin(folder?.id).subscribe(
        (data) => {
          newFolder = data;
          f=newFolder.racine;
          console.log(f);
          console.log("dossiers: ", newFolder.dossiers.length);
          this._helper.show("", newFolder.dossiers.length > 0 ? message2: message1,"", folder.id, 0).then((result) =>{
            console.log(this.currentFolder)
            if (result == 0)
            {
                this.folderService.getFolderByIdAsAdmin(f.id).subscribe((data) =>{
                this.currentFolder = data;
              })
            }
            // this.cdRef.detectChanges();
          });
        }
      );
    }

    openPopup(folder: Dossier){
      this.router.navigate(['files/folderdetails'], { queryParams: { folderId: folder.id } });
    }

    hideModal(): void {
      this.showModal = false;
    }

    onFileClick(_t47: Fichier) {
      throw new Error('Method not implemented.');
      }

    deleteFilePopup(fichier: Fichier) {
      throw new Error('Method not implemented.');
    }

    openFilePopup(fichier: Fichier) {
      this.router.navigate(['files/filedetails'], { queryParams: { fileId: fichier.id } });
    }
}
