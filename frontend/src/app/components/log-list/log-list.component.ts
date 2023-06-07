import { Component, OnInit } from '@angular/core';
import { CompagnieService } from 'src/app/_services/compagnie.service';
import Log from 'src/app/domain/Log';


@Component({
  selector: 'app-log-list',
  templateUrl: './log-list.component.html',
  styleUrls: ['./log-list.component.css']
})
export class LogListComponent implements OnInit {

logs: Log[] = [];
  
constructor(private compagnieService: CompagnieService){}
  ngOnInit(): void {
  console.log("hello world");  
  this.loadLogs();
}

loadLogs(): void{
  this.compagnieService.getCompagnieLogs().subscribe((response) => {
    console.table(response);
    this.logs=response;
  });

}
}
