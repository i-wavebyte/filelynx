import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import Groupe from 'src/app/domain/Groupe';

@Component({
  selector: 'app-add-groupe',
  templateUrl: './add-groupe.component.html',
  styleUrls: ['./add-groupe.component.css']
})
export class AddGroupeComponent implements OnInit {
  addGroupeForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private compagnieService: CompagnieService,
    private router:Router
  ) {
    this.createForm();
  }

  ngOnInit(): void {}

  createForm() {
    this.addGroupeForm = this.fb.group({
      nom: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.addGroupeForm.valid) {
      const newGroupe: Groupe = this.addGroupeForm.value;
      console.log("here i am \n");
      this.compagnieService.createGroup(newGroupe.nom).subscribe((data) => {
        console.log('Groupe added successfully', data);
        this.addGroupeForm.reset();
        this.router.navigate(['/groups'], { replaceUrl: true, queryParams: { reload: true } });
      });
    }
  }
}
