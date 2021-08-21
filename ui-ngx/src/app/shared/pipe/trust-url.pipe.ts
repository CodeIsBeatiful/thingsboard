import {Pipe, PipeTransform} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';

@Pipe({name: 'trustUrl'})
export class TrustUrlPipe implements PipeTransform {
  constructor(private sanitizer: DomSanitizer) {}

  transform(url: any, args?: any): any {
    // can pass unsafe url
    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  }
}
