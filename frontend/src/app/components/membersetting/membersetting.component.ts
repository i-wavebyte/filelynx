import { Component } from '@angular/core';

@Component({
  selector: 'app-membersetting',
  templateUrl: './membersetting.component.html',
  styleUrls: ['./membersetting.component.css']
})
export class MembersettingComponent {

    collaborateur: any = {
    name: 'John Doe',
    active: true
  };
}
