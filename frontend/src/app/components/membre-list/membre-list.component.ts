import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgToastService } from 'ng-angular-popup';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import Membre from 'src/app/domain/Membre';
import { PageResponse } from 'src/app/domain/PageRespone';

@Component({
  selector: 'app-membre-list',
  templateUrl: './membre-list.component.html',
  styleUrls: ['./membre-list.component.css']
})
export class MembreListComponent implements OnInit{

  membres: Membre[] = [];
  filteredMembres: Membre[] = [];
  groups: string[] = [];
  searchValue: string = '';
  selectedGroupe: string = '';
  nameOrder: string = '';
  page: number = 0;
  pageSize: number = 10;
  totalMembres!: number;

    
    constructor(private compagnieService: CompagnieService, private router: Router, private route: ActivatedRoute, private toast: NgToastService ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['reload']) {
        
        console.log('reload');
        this.loadMembres();

          this.loadGroupes();
      }
    });
    this.loadMembres();

    this.loadGroupes();
  }

  loadMembres(): void {
    this.compagnieService
        .getMembresPage(
            this.page,
            this.pageSize,
            'nom',
            this.nameOrder,
            this.searchValue,
            this.selectedGroupe
        )
        .subscribe(
            (response: PageResponse<Membre>) => {
                this.membres = response.content;
                this.filteredMembres = response.content;
                this.totalMembres = response.totalElements;
                console.log(response);
            },
            (error) => {
                console.error('Error fetching professors:', error);
            }
        );
}

loadGroupes() {
  this.compagnieService.getAllUniqueGroups().subscribe((groupes) => {
    this.groups = groupes;
  });
}

onNameOrderChange(order: string): void {
  this.nameOrder = order;
  this.page=0;
  this.loadMembres();

}


onSubjectFilterChange(subject: string): void {
  this.selectedGroupe = subject;
  this.page=0;
  this.loadMembres();
}

onSearch(): void {
  this.page=0;
  this.loadMembres();
}

prevPage(): void {
  if (this.page > 0) {
    this.page--;
    this.loadMembres();
  }
}

onInfoProfessor(profId: number): void {
  console.log(profId);

  this.router.navigate(['users/details/', profId]);
}

nextPage(): void {
  console.log('nextPage');

  if ((this.page + 1) * this.pageSize < this.totalMembres) {
    this.page++;
    this.loadMembres();
  }
}

onUpdateMembre(membre: Membre)
{
  this.compagnieService.updateMembre(membre).subscribe((data) => {
    this.toast.success({detail:"Message de réussite", summary: data.message, duration: 3000});
    console.log(data);
    this.loadMembres();
  },
  (err) => {
    this.toast.error({detail:"Message d'erreur", summary:"Erreur lors de la tentative de mise à jour de Membre", duration: 3000});
  })
}


onDeleteMembre(membreId: number, username: string)
{
  this.compagnieService.deleteMembre(membreId, username).subscribe((response) => {
    this.toast.success({detail:"Message de réussite", summary: response.message, duration: 3000});
    console.log(response);
    this.loadMembres();
  },
  (err) => {
    this.toast.error({detail:"Message d'erreur", summary:"Erreur lors de la tentative de suppression du membre", duration: 3000});

  })
}
// onEditProfessor(membre: Membre): void {
//   this.compagnieService.updateProfessor(membre.id, membre).subscribe(
//     (updatedProfessor) => {
//       console.log('Professor updated:', updatedProfessor);
//     },
//     (error) => {
//       console.error('Error updating professor:', error);
//     }
//   );
// }

// onDeleteProfessor(profId: number): void {
//   this.professorService.deleteProfessor(profId).subscribe(
//     () => {
//       this.professors = this.professors.filter(
//         (professor) => professor.id !== profId
//       );
//       this.filteredProfessors = this.filteredProfessors.filter(
//         (professor) => professor.id !== profId
//       );
//       console.log('Professor deleted:', profId);
//       this.loadProfessors();
//     },
//     (error) => {
//       console.error('Error deleting professor:', error);
//     }
//   );
// }
}
