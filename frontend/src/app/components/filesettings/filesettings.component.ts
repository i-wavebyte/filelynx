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
  selectedFile: File | null = null;
  selectedFileName: string = '';
  selectedFileNameWithoutExtension: string = '';
  extension: string = '';



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

    onFileSelected(event: Event) {
      const inputElement = event.target as HTMLInputElement;
      if (inputElement.files && inputElement.files.length > 0) {
        this.selectedFile = inputElement.files[0];
        this.selectedFileName = this.selectedFile.name;
        this.selectedFileNameWithoutExtension = this.getFileDisplayName(this.selectedFile.name);
        this.extension = this.getFileExtension(this.selectedFile.name);

        // Use the selected file here or save it in a variable for later use
        console.log('Selected file:', this.selectedFile);
      }
    }

    onFileNameChanged(event: Event) {
      // Handle changes to the file name input field
      this.selectedFileNameWithoutExtension = (event.target as HTMLInputElement).value;
    }

    getFileDisplayName(fileName: string): string {
      const dotIndex = fileName.lastIndexOf('.');
      return dotIndex !== -1 ? fileName.substring(0, dotIndex) : fileName;
    }

    getFileExtension(fileName: string): string {
      const dotIndex = fileName.lastIndexOf('.');
      return dotIndex !== -1 ? fileName.substring(dotIndex) : '';
    }

  }

