import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {FlashcardService} from '../../services/flashcard.service';
import {UserService} from '../../services/user.service';
import {Deck} from '../../dtos/deck';
import {Flashcard} from '../../dtos/flashcard';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-flashcard-manager',
  templateUrl: './flashcard-manager.component.html',
  styleUrls: ['./flashcard-manager.component.scss']
})

export class FlashcardManagerComponent implements OnInit {

  deckForm: FormGroup;
  flashcardForm: FormGroup;
  revisionSizeForm: FormGroup;
  error: boolean = false;
  errorMessage: string = '';
  viewAll: boolean = true;
  showAnswer: boolean = false;
  selectedDeck: Deck;
  selectedDecksIds: number[];
  selectedFlashcard: Flashcard;
  showFlashcardId: number;
  chooseSize: boolean = true;
  revisionCounter: number = 0;
  currentlyRevisedCard: Flashcard;
  sizeError: boolean = false;
  chosenOption: number;
  private decks: Deck[];
  private flashcards: Flashcard[];
  private selectedDecks: number[];
  revisionFlashcards: Flashcard[];
  dueDateFlashcards: Flashcard[];
  deleteFlash: boolean = false;
  confidenceError: boolean = false;
  optionError: boolean = false;
  currentRate = 0;


  constructor(private formBuilder: FormBuilder, private flashcardService: FlashcardService,
              private userService: UserService, private snackBar: MatSnackBar) {
    this.deckForm = this.formBuilder.group({
      title: ['', [
        Validators.required,
        Validators.minLength(1)
      ]]
    });
    this.flashcardForm = this.formBuilder.group({
      question: ['', [
        Validators.required,
        Validators.minLength(1)
      ]],
      answer: ['', [
        Validators.required,
        Validators.minLength(1)
      ]]
    });
    this.revisionSizeForm = this.formBuilder.group({
      revisionSize: [1]
    });
  }

  ngOnInit(): void {
    this.loadAllDecks();
  }

  /**
   * Get a list of all decks belonging to the logged-in user from backend
   */
  loadAllDecks() {
    this.deckForm.patchValue({
           title: ""
    });
    this.resetFlashcardForm();
    this.flashcardService.getDecks(localStorage.getItem('currentUser')).subscribe(
      (decksList: Deck[]) => {
        this.decks = decksList;
      },
      error => {
        this.defaultErrorHandling(error);
      }
    );
  }

  /**
   * @return all decks belonging to the logged-in user
   */
  getDecks() {
    return this.decks;
  }

  /**
   * Builds a deck dto and sends a creation request.
   */
  createDeck() {
    const date = new Date();
    date.setHours(date.getHours() - date.getTimezoneOffset() / 60);
    const dateString = date.toISOString();
    this.userService.getUserByUsername(localStorage.getItem('currentUser')).subscribe(res => {
      const deck = new Deck(0, this.deckForm.controls.title.value, 0, dateString, dateString, res);
      this.flashcardService.createDeck(deck).subscribe(
        () => {
          this.openSnackbar('You successfully created a deck with the title ' + deck.name + `!`, 'success-snackbar');
          this.loadAllDecks();
        },
        error => {
          this.error = true;
          this.errorMessage = 'Could not create a deck!';
          this.openSnackbar(this.errorMessage, 'warning-snackbar');
        }
      );
    });
  }

  /**
   * Save changes to deck dto and sends an edition request.
   */
  saveEdits(deck: Deck) {
      this.userService.getUserByUsername(localStorage.getItem('currentUser')).subscribe(res => {
             deck.name = this.deckForm.controls.title.value;
                 this.flashcardService.editDeck(deck).subscribe(
                      () => {
                             this.openSnackbar('You successfully edited a deck!', 'success-snackbar');
                             },
                             error => {
                               this.error = true;
                               this.errorMessage = 'Could not edit the deck!';
                               this.openSnackbar(this.errorMessage, 'warning-snackbar');
                             }
                           );
       });
  }


  /**
   * Get a list of all flashcards belonging to a deck from backend
   */
  loadFlashcards(deck: Deck) {
    this.selectedDeck = deck;
    this.flashcardService.getDeckById(deck.id).subscribe(
        (deckRefreshed: Deck) => {
            this.selectedDeck = deckRefreshed;
          },
          error => {
            this.defaultErrorHandling(error);
          }
    )
    this.selectedDecksIds = [deck.id];
    this.flashcardService.getFlashcards(deck.id).subscribe(
      (flashcards: Flashcard[]) => {
        this.flashcards = flashcards;
        this.chooseSize = true;
      },
      error => {
        this.defaultErrorHandling(error);
      }
    );
    this.flashcardService.revise(1, deck.id, 1).subscribe(
      (flashcards: Flashcard[]) => {
         this.dueDateFlashcards = flashcards;
       },
       error => {
         this.defaultErrorHandling(error);
    });
    this.deckForm.patchValue({
      title: deck.name
    });
    this.resetFlashcardForm();

  }

  /**
   * @return all flashcards belonging to the logged-in user
   */
  getFlashcards() {
    return this.flashcards;
  }

  /**
   * Builds a flashcard dto and sends a creation request.
   */
  createFlashcard() {
       const flashcard = new Flashcard(0, this.flashcardForm.controls.question.value, this.flashcardForm.controls.answer.value);
       this.flashcardService.createFlashcard(flashcard).subscribe(
                       (flashcardCreated: Flashcard) => {
                              this.openSnackbar('You successfully created a flashcard with the question ' + flashcard.question + `!`, 'success-snackbar');
                              this.flashcardService.assignFlashcard(flashcardCreated.id, this.selectedDecks).subscribe(
                                                         () => {
                                                                  if(this.selectedDeck != undefined) {
                                                                    this.loadFlashcards(this.selectedDeck);
                                                                  }
                                                                  this.loadAllDecks();
                                                               },
                                                               error => {
                                                                      this.error = true;
                                                                      this.errorMessage = 'Could not assign the flashcard!';
                                                                      this.openSnackbar(this.errorMessage, 'warning-snackbar');
                                                               });

                              },
                              error => {
                                this.error = true;
                                this.errorMessage = 'Could not create a flashcard!';
                                this.openSnackbar(this.errorMessage, 'warning-snackbar');

                              });
  }

  /**
   * Save changes to flashcard dto and sends an edition request.
   */
  saveFlashcardEdits(flashcard: Flashcard) {
              let question = this.flashcardForm.controls.question.value;
              let answer = this.flashcardForm.controls.answer.value;
              if(question != null && question != "") {
                flashcard.question = question;
              }
              if(answer != null && answer != "") {
                flashcard.answer = answer;
              }
              this.flashcardService.editFlashcard(flashcard).subscribe(
                    () => {
                           this.openSnackbar('You successfully edited a flashcard!', 'success-snackbar');
                           this.loadFlashcards(this.selectedDeck);
                           },
                           error => {
                             this.error = true;
                             this.errorMessage = 'Could not edit the flashcard!';
                             this.openSnackbar(this.errorMessage, 'warning-snackbar');
              });
    }


  /**
   * Sends a revision request based on chosen revision size.
   */
  revise() {
      console.log("Option: " + this.chosenOption)
      this.sizeError = false;
      this.optionError = false;
      let size = this.revisionSizeForm.controls.revisionSize.value;
      if (size <= 0 || size > this.selectedDeck.size) {
          this.sizeError = true;
      } else if(this.chosenOption == undefined) {
          this.optionError = true;
      } else {
            this.chooseSize = false;
            this.revisionCounter = 0;
            this.flashcardService.revise(size, this.selectedDeck.id, this.chosenOption).subscribe(
                (flashcards: Flashcard[]) => {
                             this.revisionFlashcards = flashcards;
                             this.getRevisionFlashcard();
                             },
                             error => {
                                   this.defaultErrorHandling(error);
                             }
                         );

      }

  }

  /**
   * @return next flashcard for the revision
   */
   getRevisionFlashcard() {
     this.showAnswer = false;
     this.currentlyRevisedCard = this.revisionFlashcards[this.revisionCounter];
     if (this.revisionCounter < this.revisionFlashcards.length) {
        this.revisionCounter = this.revisionCounter + 1;
     }
   }


  /**
   * Sends a request to delete a specific deck.
   */
  deleteDeck(id: number) {
    this.flashcardService.deleteDeck(id).subscribe(
      () => {
        this.openSnackbar('You successfully deleted the deck!', 'success-snackbar');
        this.loadAllDecks();
        this.viewAll = true;
      },
      error => {
        this.defaultErrorHandling(error);
      });
  }

  /**
   * Sends a request to delete a specific flashcard.
   */
  deleteFlashcard(flashcardId: number, deckId: number) {
    this.flashcardService.deleteFlashcard(flashcardId, deckId).subscribe(
      () => {
        this.openSnackbar('You successfully deleted the flashcard!', 'success-snackbar');
        this.loadAllDecks();
        this.loadFlashcards(this.selectedDeck);
      },
      error => {
        this.defaultErrorHandling(error);
      });
  }

  /**
   * Sends a request to rate a specific flashcard.
   */
  rateFlashcard(flashcard: Flashcard, rate: number) {
    console.log(flashcard);
    if (rate != null && (rate < 1 || rate > 5)){
      this.error = true;
      this.errorMessage = 'Could not rate the flashcard! Please choose the value between 1 and 5.';
      this.openSnackbar(this.errorMessage, 'warning-snackbar');
    } else {
      this.flashcardService.rateFlashcard(flashcard, rate).subscribe(
          () => {
            this.openSnackbar('You successfully rated a flashcard!', 'success-snackbar');
            this.loadFlashcards(this.selectedDeck);
          },
          error => {
            this.confidenceError = true;
            this.error = true;
            this.errorMessage = 'Could not rate the flashcard! Please choose the value between 1 and 5.';
            this.openSnackbar(this.errorMessage, 'warning-snackbar');
          });
      }
  }


  updateDeckList(select : number){
    console.log("deck: " + select);
    if(this.selectedDecks != undefined) {
      let index = this.selectedDecks.indexOf(select);
      if(index > -1) {
          this.selectedDecks.splice(index, 1)
      } else {
          this.selectedDecks.push(select);
      }
    } else {
      this.selectedDecks = [select];
    }
  }

  resetDecks() {
    this.selectedDecks = [];
    this.resetFlashcardForm();
  }

  flashcardClicked(select: Flashcard, del: boolean) {
     console.log(select);
     this.selectedFlashcard = select;
     this.showFlashcardId = select.id;
     this.deleteFlash = del;
     //this.currentRate = this.selectedFlashcard.confidenceLevel;
     console.log(this.showFlashcardId);
     this.flashcardForm.patchValue({
        question: select.question,
        answer: select.answer
     });
   }

   resetDeckForm() {
    this.deckForm.reset();
   }

   resetFlashcardForm() {
    this.flashcardForm.reset();
   }

   resetRevisionSizeForm() {
    this.revisionSizeForm.patchValue({
      revisionSize: 1
    });
    this.chosenOption = undefined;
    this.optionError = false;
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
  }

}
