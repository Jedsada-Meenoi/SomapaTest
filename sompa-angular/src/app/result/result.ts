import { Component} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Form } from './form/form';
import { Table } from "./table/table";
@Component({
  selector: 'app-result',
  imports: [CommonModule,Form, Table],
  templateUrl: './result.html',
  styleUrl: './result.css',
})
export class Result {
  
  passengers: any[] = []; //Store the data passed from API
  fileName: string = '';
  downloadUrl: string = '';

  //Get the data passed from API
  constructor(private router: Router, private route: ActivatedRoute) {}

  ngOnInit(): void {
    const state = history.state; 
    this.passengers = state?.passengers ?? [];
    this.fileName = state?.fileName ?? '';

    const file = state?.['file'];
    if (file) {
      this.downloadUrl = URL.createObjectURL(file); // Create a URL for download file
    }
  }

  //Back to home page
onBack(): void {
    this.router.navigate(['/']);
  }

}
