import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms'; //NgModel
import { CommonModule } from '@angular/common'; //NgIf, NgFor
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [FormsModule, CommonModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})

export class Home {

  //store input values
  flightNo = '';
  selectFile: File | null = null;
  fileNameDisplay = '';
  errors = { flightNo: '', file: '' }; //Error for each field
  serverErrors: string[] = []; // Server error


  constructor(private http: HttpClient, private router: Router) {}
  private readonly MAX_FILE_SIZE = 1 * 1024 * 1024; //1 MB
  private readonly API_URL = 'http://localhost:8080/excel/upload'; //API URL

//Handle file input change
  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return; 

    //Check file type
    if (!file.name.endsWith('.xlsx')) {
      alert('Only .xlsx files are allowed.');
      input.value = ''; 
      return;
    }

    //Check file size
    if (file.size > this.MAX_FILE_SIZE) {
      alert('File size must be less than 1 MB');
      input.value = '';
      return;
    }

    this.selectFile = file;
    this.fileNameDisplay = file.name;
    this.errors.file = '';
  }

  //Handle Save button
  onSave(): void {

    //Check flight no
    this.errors.flightNo = !this.flightNo.trim() ? 'This field is required' : 
      (!/^[A-Z0-9]{2}\d{2,4}$/.test(this.flightNo.trim()) ? 'Invalid format' : '');
    
    this.errors.file = !this.selectFile ? 'This field is required' : '';

    if (this.errors.flightNo || this.errors.file) return;
    
    console.log('Saving...', { flightNo: this.flightNo, file: this.selectFile });

    //File upload
    const formData = new FormData();
    formData.append('file', this.selectFile!);

    //Call API
    this.http.post(`${this.API_URL}`, formData).subscribe({
      next: (res) => {
        this.router.navigate(['/result'], { state: { passengers: res, fileName: this.selectFile!.name, file: this.selectFile } }); 
      },
      error: (err) => {
        this.serverErrors = err.error.split(' | '); //Not work
      }
    });

  }

  //Reset form when Cancel button
  onCancel(): void {
    this.flightNo = '';
    this.selectFile = null;
    this.fileNameDisplay = '';
    this.errors = { flightNo: '', file: '' };
  }
}