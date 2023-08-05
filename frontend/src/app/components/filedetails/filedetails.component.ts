import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CompagnieService } from 'src/app/_services/compagnie.service';

@Component({
  selector: 'app-filedetails',
  templateUrl: './filedetails.component.html',
  styleUrls: ['./filedetails.component.css']
})
export class FiledetailsComponent {

  labels!: string[];
  categories!: string[];
  fileId!: number;

  constructor(private compagnieService: CompagnieService,private route: ActivatedRoute  ){}

  ngOnInit(){
    this.route.params.subscribe(params => {
      this.fileId = params['fileId']; // Here 'id' is the route parameter name defined in the routerLink
      console.log(this.fileId);
     })
  }

}
