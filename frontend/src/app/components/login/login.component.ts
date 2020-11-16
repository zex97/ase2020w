import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {AuthRequest} from '../../dtos/auth-request';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;
  registerForm: FormGroup;
  registrationOn: boolean = false;
  // After first submission attempt, form validation will start
  submitted: boolean = false;
  // Error flag
  error: boolean = false;
  errorMessage: string = '';

  constructor(private formBuilder: FormBuilder, private authService: AuthService, private router: Router) {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
    this.registerForm = this.formBuilder.group({});
  }

  /**
   * Form validation will start after the method is called, additionally an AuthRequest will be sent
   */
  loginUser() {
    this.submitted = true;
    if (this.loginForm.valid) {
      const authRequest: AuthRequest = new AuthRequest(this.loginForm.controls.username.value, this.loginForm.controls.password.value);
      this.authenticateUser(authRequest);
    } else {
      console.log('Invalid input');
    }
  }

  /**
   * Send authentication data to the authService. If the authentication was successfully, the user will be forwarded to the message page
   * @param authRequest authentication data from the user login form
   */
  authenticateUser(authRequest: AuthRequest) {
    console.log('Try to authenticate user: ' + authRequest.username);
    this.authService.loginUser(authRequest).subscribe(
      () => {
        console.log('Successfully logged in user: ' + authRequest.username);
        this.router.navigate(['']);
      },
      error => {
        console.log('Could not log in due to: ' + error.message);
        this.error = true;
        this.errorMessage = error.error.message;
      }
    );
    this.router.navigate(['/message']);
  }

  /**
   * Error flag will be deactivated, which clears the error message
   */
  vanishError() {
    this.error = false;
  }

  ngOnInit() {
  }

  openRegistration() {
    this.registrationOn = true;
  }

  closeRegistration() {
    this.registrationOn = false;
  }
}
