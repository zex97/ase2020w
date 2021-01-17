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
    this.token = this.route.snapshot.queryParamMap.get('token');
    if (this.token != null) {
      this.userService.verifyToken(this.token).subscribe((data) => {
        if (data.token !== this.token) {
          this.openSnackbar('Token is invalid or expired!', 'warning-snackbar');
          this.navigateToLogin();
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
      this.userService.changePasswordWithToken(this.token, user).subscribe(() => {
        this.openSnackbar('Password successfully changed!', 'success-snackbar');
        this.navigateToLogin();
      },
        error => {
          this.defaultErrorHandling(error);
        });
    } else {
      console.log('Invalid input');
    }
  }

  navigateToLogin() {
    this.router.navigate(['/login']);
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

    if (error.status == 400) {
      this.openSnackbar('Token is invalid or expired!', 'warning-snackbar');
      this.error = false;
      this.navigateToLogin();
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
