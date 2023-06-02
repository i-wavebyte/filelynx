import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgToastService } from 'ng-angular-popup';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import Membre from 'src/app/domain/Membre';
import UserRegister from 'src/app/domain/UserRegister';

@Component({
  selector: 'app-add-membre',
  templateUrl: './add-membre.component.html',
  styleUrls: ['./add-membre.component.css']
})
export class AddMembreComponent implements OnInit{
  addMemberForm!: FormGroup;

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
    this.addMemberForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', Validators.required],
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      group: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.addMemberForm.valid) {
      const newMember: UserRegister = this.addMemberForm.value;
      this.compagnieService.addMembre(newMember).subscribe((data) => {
        console.log('Member added successfully', data);
        this.addMemberForm.reset();
        this.router.navigate(['/users']);
      });
    }
  }
}

// import { Component, OnInit } from '@angular/core';
// import { FormGroup, FormBuilder, Validators } from '@angular/forms';
// import { CompagnieService } from 'src/app/_services/compagnie.service';
// import Membre from 'src/app/domain/Membre';

// @Component({
//   selector: 'app-add-member',
//   templateUrl: './add-member.component.html',
//   styleUrls: ['./add-member.component.css']
// })
// export class AddMemberComponent implements OnInit {
//   addMemberForm!: FormGroup;

//   constructor(
//     private fb: FormBuilder,
//     private compagnieService: CompagnieService
//   ) {
//     this.createForm();
//   }

//   ngOnInit(): void {}

//   createForm() {
//     this.addMemberForm = this.fb.group({
//       id: ['', Validators.required],
//       username: ['', Validators.required],
//       email: ['', Validators.required],
//       nom: ['', Validators.required],
//       prenom: ['', Validators.required]
//     });
//   }

//   onSubmit() {
//     if (this.addMemberForm.valid) {
//       const newMember: Membre = this.addMemberForm.value;
//       this.compagnieService.addMembre(newMember).subscribe((data) => {
//         console.log('Member added successfully', data);
//         this.addMemberForm.reset();
//       });
//     }
//   }
// }
