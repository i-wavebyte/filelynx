import { DOCUMENT } from '@angular/common';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import { FileService } from 'src/app/_services/file.service';

@Component({
  selector: 'app-filedetails',
  templateUrl: './filedetails.component.html',
  styleUrls: ['./filedetails.component.css']
})
export class FiledetailsComponent {

  labels!: string[];
  categorie!: string;
  fileId!: number;
  extension!: string;
  fileName!: string;
  groupe!: string;
  size!: string;
  imageUrl!: any;

  constructor(private compagnieService: CompagnieService,private route: ActivatedRoute, private fichierService: FileService  ){}

  ngOnInit(){
    this.extension = '..';
    this.route.params.subscribe(params => {
      this.fileId = params['fileId']; // Here 'id' is the route parameter name defined in the routerLink
      this.fichierService.getFileById(this.fileId).subscribe((data) => {
        this.extension = data.extension;
        this.labels = data.labels.map(label => label.nom);
        this.fileName = data.nom;
        this.size = this.tailleToBestUnit(data.taille, true,2) ;
        if (data.categorie)
        this.categorie = data.categorie.nom;
        if(this.isImage()){

          this.fichierService.getImageById(this.fileId).subscribe((response) => {
            const contentType = response.headers.get('Content-Type');
            const blob = new Blob([response.body], { type: contentType });
            console.log(blob);
            // Create a temporary URL for the downloaded image
            this.imageUrl = URL.createObjectURL(blob);
            console.log(this.imageUrl);
          })
        }

    })
  })

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

onSave() {

}

isImage(): boolean {
  let imageExtensions = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'ico' , 'tif', 'tiff' , 'jfif' , 'pjpeg' , 'pjp', "avif"];
  return imageExtensions.includes(this.extension);
}

}
