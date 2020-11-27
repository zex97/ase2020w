import {Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {FlashcardService} from '../../services/flashcard.service';
import {UserService} from '../../services/user.service';
import {Deck} from "../../dtos/deck"
import {User} from '../../dtos/user';


@Component({
  selector: 'app-flashcard-manager',
  templateUrl: './flashcard-manager.component.html',
  styleUrls: ['./flashcard-manager.component.scss']
})

export class FlashcardManagerComponent implements OnInit {

  deckForm: FormGroup;
  error: boolean = false;
  errorMessage: string = '';
  user: User;

  constructor(private formBuilder: FormBuilder, private flashcardService: FlashcardService, private userService: UserService) {
    this.deckForm = this.formBuilder.group({
      title: ['']
    })
   }

  ngOnInit(): void {
  }

  /**
   * Builds a deck dto and sends a creation request.
   * If the procedure was successful, the form will be cleared.
   */
  createDeck() {
  //dto for testing purposes, will be replaced - name and userId
    const date = new Date();
    date.setHours(date.getHours() - date.getTimezoneOffset() / 60);
    const dateString = date.toISOString();
    this.userService.getUserById(3).subscribe(res => {
       this.user = res;
       const deck = new Deck(0, this.deckForm.controls.title.value, 0, dateString, dateString, res);
           this.flashcardService.createDeck(deck).subscribe(
                () => {
                       console.log("Back");
                         //TO-DO: back to Deck View
                       },
                       error => {
                         this.defaultErrorHandling(error);
                       }
                     );
    });

    //this.clearForm();
  }

  private defaultErrorHandling(error: any) {
      console.log(error);
      this.error = true;
      this.errorMessage = '';
      this.errorMessage = error.error.message;
    }

}
