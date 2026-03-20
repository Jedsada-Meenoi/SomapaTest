import { Component, Input } from '@angular/core'; // Input — allows receiving data from parent component
import { CommonModule } from '@angular/common'; //NgFor

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './table.html',
  styleUrl: './table.css'
})
export class Table {
  @Input() passengers: any[] = [];
}