import { Component } from '@angular/core';

@Component({
  selector: 'app-add-membre',
  templateUrl: './add-membre.component.html',
  styleUrls: ['./add-membre.component.css']
})
export class AddMembreComponent {

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
