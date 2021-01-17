import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';
import {User} from '../../dtos/user';
import {AuthService} from '../../services/auth.service';
import {UserService} from '../../services/user.service';

@Component({
  selector: 'app-password-reset',
  templateUrl: './password-reset.component.html',
  styleUrls: ['./password-reset.component.scss']
})
export class PasswordResetComponent implements OnInit {

  emailCheckForm: FormGroup;
  submitted: boolean = false;
  // Error flag
  error: boolean = false;
  errorMessage: string = '';

  constructor(private formBuilder: FormBuilder, private userService: UserService, private router: Router, private snackBar: MatSnackBar) {
    this.emailCheckForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {

  }

  /**
   * Sends request to backend to check email address and send recovery email.
   */
  checkEmailAndRecover() {
    this.submitted = true;
    if (this.emailCheckForm.valid) {
      this.userService.checkEmailAndRecover(this.emailCheckForm.controls.email.value).subscribe(() => {
        this.emailCheckForm.reset();
        this.openSnackbar('Recovery email has been sent.', 'success-snackbar');
        this.router.navigate(['/login']);
      },
          error => {
        this.defaultErrorHandling(error);
      }
        );
    } else {
      console.log('Invalid input.');
    }
  }

  navigateToRegister() {
    this.router.navigate(['/register']);
  }

  openSnackbar(message: string, type: string) {
    this.snackBar.open(message, 'close', {
      duration: 4000,
      panelClass: [type]
    });
  }

  private defaultErrorHandling(error: any) {
    console.log(error);
    this.error = true;
    this.errorMessage = '';
    this.errorMessage = error.error.message;

    if (error.status == 404) {
      this.openSnackbar('Email is not connected to any user!', 'warning-snackbar');
      this.error = false;
    }
  }

}
