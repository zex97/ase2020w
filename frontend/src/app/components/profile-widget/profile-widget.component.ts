import { Component, OnInit } from '@angular/core';
import {User} from '../../dtos/user';
import {UserService} from '../../services/user.service';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-profile-widget',
  templateUrl: './profile-widget.component.html',
  styleUrls: ['./profile-widget.component.scss']
})
export class ProfileWidgetComponent implements OnInit {

  public showInitials = true;
  public initials: string = 'S';
  public circleColor: string = '#468547';
  // dummy user until the actual user is fetched from backend
  currentUser: User = new User(1, 'username', 'dummy', 'email@email.com', 1);

  constructor(private userService: UserService, private authService: AuthService) { }

  ngOnInit(): void {
    this.getCurrentUser();
  }

  getCurrentUser() {
    console.log('Get current user');
    this.userService.getUserByUsername(this.authService.getUsername()).subscribe(
      (user: User) => {
        this.currentUser = user;
        this.createInititals();
        console.log('Current user is: ' + user.username);
      },
    );
  }

  private createInititals(): void {
    let initials = '';
    const name = this.currentUser.username;
    if (name.split( ' ').length >= 2) {
      for (let i = 0; i < name.length; i++) {
        if (name.charAt(i) === ' ') {
          continue;
        }

        if (name.charAt(i) === name.charAt(i).toUpperCase()) {
          initials += name.charAt(i);

          if (initials.length === 2) {
            break;
          }
        }
      }
    } else {
      initials = name.charAt(0).toUpperCase() + name.charAt(1).toUpperCase();
    }
    this.initials = initials;
  }

}
