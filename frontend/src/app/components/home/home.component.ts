import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  constructor(public authService: AuthService) { }

  toggled = false;
  selectedPage: string = 'Spaces';

  isToggled() {
    return this.toggled;
  }

  ngOnInit() {
    let url = window.location.href;
    console.log(url);
    if(url.includes('(view:flashcards)')) {
      this.selectedPage = 'Flashcards';
    } else {
      this.selectedPage = 'Spaces';
    }
  }

  onToggle() {
    this.toggled = !this.toggled;
  }

}
