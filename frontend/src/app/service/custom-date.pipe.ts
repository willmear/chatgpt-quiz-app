import { Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';

@Pipe({
  name: 'customDate',
})
export class CustomDatePipe implements PipeTransform {
  transform(value: string | Date, format: string = 'd MMM'): string {
    // Format the date using Angular's built-in DatePipe
    const dateString = value instanceof Date ? value.toISOString() : value;

    const datePipe = new DatePipe('en-US');
    return datePipe.transform(dateString, format) || '';
  }
}
