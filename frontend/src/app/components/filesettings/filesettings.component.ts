import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import { FolderService } from 'src/app/_services/folder.service';
import Categorie from 'src/app/domain/Categorie';
import Label from 'src/app/domain/Label';

@Component({
  selector: 'app-filesettings',
  templateUrl: './filesettings.component.html',
  styleUrls: ['./filesettings.component.css']
})
export class FilesettingsComponent {

  categories: String[] = [];
  labels: String[] = [];
  groupe: string = "";
  constructor(private compagnieService: CompagnieService, private route: ActivatedRoute, private folderService: FolderService) {}
  ngOnInit() {
    this.route.params.subscribe(params => {
      const folderId = params['parentId']; // Here 'id' is the route parameter name defined in the routerLink
      this.folderService.getFolderByIdAsAdmin(folderId).subscribe((data) => {
        console.log(data);
        this.groupe = data.nom;
      })
  
      // Now you can use the folderId as needed in your component
    });
    this.compagnieService.getAllLabels().subscribe((data) => {
      this.labels = data;
      console.log(this.labels);
    })

    this.compagnieService.getAllCategories().subscribe((data) => {
      this.categories = data;
      console.log(this.categories);
    })
      // Use the folderId as needed
    };

  }

