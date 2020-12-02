import {Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {UserService} from '../../services/user.service';
import {SpaceService} from '../../services/space.service';
import {User} from '../../dtos/user';
import {Space} from '../../dtos/space';

@Component({
  selector: 'app-document-space',
  templateUrl: './document-space.component.html',
  styleUrls: ['./document-space.component.scss']
})

export class DocumentSpaceComponent implements OnInit {

  spaceForm: FormGroup;
  nameEditForm: FormGroup;
  error: boolean = false;
  errorMessage: string = '';
  /**TODO: think what happens when the user deletes space with id 1*/
  spaceId: number = 1;
  private spaces: Space[];


  constructor(private formBuilder: FormBuilder, private spaceService: SpaceService, private userService: UserService) {
   this.spaceForm = this.formBuilder.group({
         name: ['']
       })
   this.nameEditForm = this.formBuilder.group({
            name: ['']
          })
   }

  ngOnInit(): void {
    this.loadAllSpaces();
  }

  /**
  * Sends a request to load all spaces belonging to the currently logged-in user.
  */
  loadAllSpaces() {
    this.spaceService.getSpaces(localStorage.getItem('currentUser')).subscribe(
         (spaceList : Space[]) => {
                      this.spaces = spaceList;
                      },
                      error => {
                            this.defaultErrorHandling(error);
                      }
                  );
   }

  getSpaces() {
    return this.spaces;
  }

  /**
  * Builds a space dto and sends a creation request.
  */
  createSpace() {
    this.userService.getUserByUsername(localStorage.getItem('currentUser')).subscribe(res => {
       const space = new Space(0, this.spaceForm.controls.name.value, res);
           this.spaceService.createSpace(space, localStorage.getItem('currentUser')).subscribe(
                () => {
                       this.loadAllSpaces();
                       },
                       error => {
                         this.defaultErrorHandling(error);
                       }
                     );
    });
  }

  /**
  * Sends a request to delete a specific space.
  */
  deleteSpace(id: number) {
    this.spaceService.deleteSpace(id, localStorage.getItem('currentUser')).subscribe(
                () => {
                       this.loadAllSpaces();
                       },
                       error => {
                         this.defaultErrorHandling(error);
                       });
  }

  /**
  * Sends a put request to change a specific space.
  */
  saveEdits(space: Space) {
        //send edits to backend
        this.userService.getUserByUsername(localStorage.getItem('currentUser')).subscribe(res => {
               space.name = this.nameEditForm.controls.name.value;
                   this.spaceService.editSpace(space, localStorage.getItem('currentUser')).subscribe(
                        () => {
                               this.loadAllSpaces();
                               location.reload();
                               },
                               error => {
                                 this.defaultErrorHandling(error);
                               }
                             );
         });
  }


  /**
  * Saves the id of the space user clicked on, to connect it with document component.
  */
  loadSpaceDetails(id: number) {
    this.spaceId = id;
   }

  private defaultErrorHandling(error: any) {
      console.log(error);
      this.error = true;
      this.errorMessage = '';
      this.errorMessage = error.error.message;
    }

}
