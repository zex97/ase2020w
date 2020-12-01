import {Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {SpaceService} from '../../services/space.service';
import {User} from '../../dtos/user';
import {Space} from '../../dtos/space';


@Component({
  selector: 'app-flashcard-manager',
  templateUrl: './document-space.component.html',
  styleUrls: ['./document-space.component.scss']
})

export class DocumentSpaceComponent implements OnInit {

  spaceForm: FormGroup;
  error: boolean = false;
  errorMessage: string = '';


  constructor(private formBuilder: FormBuilder, private spaceService: SpaceService, private userService: UserService) {
   this.spaceForm = this.formBuilder.group({
         name: ['']
       })
   }

  ngOnInit(): void {
  }

  /**
     * Builds a space dto and sends a creation request.
     */
    createSpace() {
      this.userService.getUserByUsername(localStorage.getItem('currentUser')).subscribe(res => {
         const space = new Space(0, this.spaceForm.controls.name.value, res);
             this.spaceService.createSpace(space, localStorage.getItem('currentUser')).subscribe(
                  () => {
                         //load all spaces
                         },
                         error => {
                           this.defaultErrorHandling(error);
                         }
                       );
      });
    }

  private defaultErrorHandling(error: any) {
      console.log(error);
      this.error = true;
      this.errorMessage = '';
      this.errorMessage = error.error.message;
    }

}
