import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import Groupe from 'src/app/domain/Groupe';
import Membre from 'src/app/domain/Membre';

@Component({
  selector: 'app-membersetting',
  templateUrl: './membersetting.component.html',
  styleUrls: ['./membersetting.component.css']
})
export class MembersettingComponent {

  collaborateur!: Membre;
  groupes!: string[];
  constructor(private router: Router, private route: ActivatedRoute, private compagnieService: CompagnieService) {}

  ngOnInit(): void {

    // Access the data passed during navigation
    this.route.queryParams.subscribe(params => {
      const membreData = JSON.parse(params['membreData'] || '{}');
      if (membreData) {
        console.log(membreData.groupe.nom);
        this.collaborateur = membreData;
        // Use the data as needed
      }
    });
    this.compagnieService.getAllUniqueGroups().subscribe((data) => {
      this.groupes = data;
      const indexToDelete = this.groupes.findIndex(name => name === this.collaborateur.groupe.nom);
      if (indexToDelete !== -1) {
        // Delete the element at the found index
        this.groupes.splice(indexToDelete, 1);
      }
      console.log(this.groupes);
    });
  }
}
