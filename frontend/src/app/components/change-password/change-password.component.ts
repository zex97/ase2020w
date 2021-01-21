import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../services/user.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {User} from '../../dtos/user';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {

  token: string;
  passwordChangeForm: FormGroup;
  submitted: boolean = false;
  // Error flag
  error: boolean = false;
  errorMessage: string = '';
  urlString: string;

  constructor(private formBuilder: FormBuilder, private route: ActivatedRoute, private userService: UserService, private router: Router,
              private snackBar: MatSnackBar) {
    this.passwordChangeForm = this.formBuilder.group({
        password: ['', [Validators.required, Validators.minLength(8), Validators.pattern('.*[0-9].*')]],
        passwordConfirm: ['', Validators.required]}
      , {
        validator: this.checkPasswordMatch('password', 'passwordConfirm')
      });
  }

  ngOnInit(): void {
    this.urlString = this.router.url;
    this.token = this.route.snapshot.queryParamMap.get('token');
    if ((this.urlString === '/changePassword' || this.urlString.includes('/changePassword?')) && this.token != null) {
      this.userService.verifyToken(this.token).subscribe((data) => {
        if (data.token !== this.token) {
          this.openSnackbar('Token is invalid or expired!', 'warning-snackbar');
          this.navigateToLoginOrHome();
        }
      });
    }
  }

  /**
   * Sends change password request.
   */
  changePassword() {
    this.submitted = true;
    if (this.passwordChangeForm.valid &&
      (this.passwordChangeForm.controls.password.value === this.passwordChangeForm.controls.passwordConfirm.value)) {
      const user: User = new User(
        null,
        null,
        this.passwordChangeForm.controls.password.value,
        null,
        0
      );
      this.userService.changePassword(this.token, user, this.urlString).subscribe(() => {
          this.openSnackbar('Password successfully changed!', 'success-snackbar');
          this.navigateToLoginOrHome();
        },
        error => {
          this.defaultErrorHandling(error);
        });
    } else {
      console.log('Invalid input');
    }
  }

  navigateToLoginOrHome() {
    if (this.urlString === '/changePassword' || this.urlString.includes('/changePassword?')) {
      this.router.navigate(['/login']);
    } else if (this.urlString === '/changePasswordHome') {
      this.router.navigate(['/home']);
    }
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
    this.errorMessage = error.message;

    if ((this.urlString === '/changePassword' || this.urlString.includes('/changePassword?')) && error.status === 400) {
      this.openSnackbar('Token is invalid or expired!', 'warning-snackbar');
      this.error = false;
      this.navigateToLoginOrHome();
    }
    if (this.urlString === '/changePasswordHome' && error.status === 404) {
      this.openSnackbar('User does not exist!', 'warning-snackbar');
      this.error = false;
    }
  }

  get f() {
    return this.passwordChangeForm.controls;
  }

  checkPasswordMatch(password: string, confirmation: string) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[password];
      const matchingControl = formGroup.controls[confirmation];

      if (matchingControl.errors && !matchingControl.errors.mustMatch) {
        return;
      }

      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ passwordsDifferent: true });
      } else {
        matchingControl.setErrors(null);
      }
    };
  }
}
