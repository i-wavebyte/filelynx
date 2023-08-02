import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgToastService } from 'ng-angular-popup';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import Groupe from 'src/app/domain/Groupe';

@Component({
  selector: 'app-add-folder-collab',
  templateUrl: './add-folder-collab.component.html',
  styleUrls: ['./add-folder-collab.component.css']
})
export class AddFolderCollabComponent implements OnInit {
  addGroupeForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private compagnieService: CompagnieService,
    private router:Router,
    private toast: NgToastService
  ) {
    this.createForm();
  }

  ngOnInit(): void {}

  createForm() {
    console.log("here's the add groupForm variable ",this.addGroupeForm);
    this.addGroupeForm = this.fb.group({
      nom: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.addGroupeForm.valid) {
      const newGroupe: Groupe = this.addGroupeForm.value;
      this.compagnieService.createGroup(newGroupe.nom).subscribe((data) => {
        this.toast.success({detail:"Message de réussite", summary: data.message, duration: 3000})
        this.addGroupeForm.reset();
        this.router.navigate(['/groups'], { replaceUrl: true, queryParams: { reload: true } });
      },
      (err) => {
        this.toast.error({detail:"Message d'erreur", summary:err.error, duration:3000});
        this.addGroupeForm.reset();
        this.router.navigate(['/groups'], { replaceUrl: true, queryParams: { reload: true } });
      }
      );
    }
  }
}

