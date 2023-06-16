import { Component, OnInit } from '@angular/core';
import { FolderService } from 'src/app/_services/folder.service';
import Dossier from 'src/app/domain/Dossier';

@Component({
  selector: 'app-files',
  templateUrl: './files.component.html',
  styleUrls: ['./files.component.css']
})
export class FilesComponent implements OnInit{
  currentFolder:Dossier | null = null;

  constructor(private folderService:FolderService) {}

  ngOnInit(): void {
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

}
