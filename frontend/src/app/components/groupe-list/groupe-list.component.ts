import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PageResponse } from 'src/app/domain/PageRespone';
import Groupe from 'src/app/domain/Groupe';
import { CompagnieService } from 'src/app/_services/compagnie.service';

@Component({
  selector: 'app-groupe-list',
  templateUrl: './groupe-list.component.html',
  styleUrls: ['./groupe-list.component.css'],
})
export class GroupeListComponent implements OnInit {
  groups: Groupe[] = [];
  filteredGroups: Groupe[] = [];
  searchValue: string = '';
  nameOrder: string = '';
  page: number = 0;
  pageSize: number = 10;
  totalGroups!: number;

  constructor(
    private compagnieService: CompagnieService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadGroupes();
  }

  loadGroupes(): void {
    this.compagnieService
      .getGroupesPage(
        this.page,
        this.pageSize,
        'nom',
        this.nameOrder,
        this.searchValue
      )
      .subscribe(
        (response: PageResponse<Groupe>) => {
          this.groups = response.content;
          this.filteredGroups = response.content;
          this.totalGroups = response.totalElements;
        },
        (error) => {
          console.error('Error fetching professors:', error);
        }
      );
  }

  onNameOrderChange(order: string): void {
    this.nameOrder = order;
    this.page=0;
    this.loadGroupes();

  }


  onSubjectFilterChange(subject: string): void {

    this.page=0;
    this.loadGroupes();
  }

  onSearch(): void {
    this.page=0;
    this.loadGroupes();
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadGroupes();
    }
  }

  nextPage(): void {
    console.log('nextPage');

    if ((this.page + 1) * this.pageSize < this.totalGroups) {
      this.page++;
      this.loadGroupes();
    }
  }
}
