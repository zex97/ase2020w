import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators, FormControl} from '@angular/forms';
import {FlashcardService} from '../../services/flashcard.service';
import {UserService} from '../../services/user.service';
import {SpaceService} from '../../services/space.service';
import {FileUploadService} from '../../services/file-upload.service';
import {Deck} from '../../dtos/deck';
import {Flashcard} from '../../dtos/flashcard';
import {Space} from '../../dtos/space';
import {Document} from '../../dtos/document';
import {MatSnackBar} from '@angular/material/snack-bar';
import {DocumentDialogComponent} from '../document-dialog/document-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {DomSanitizer} from '@angular/platform-browser';


@Component({
  selector: 'app-flashcard-manager',
  templateUrl: './flashcard-manager.component.html',
  styleUrls: ['./flashcard-manager.component.scss']
})

export class FlashcardManagerComponent implements OnInit {

  deckForm: FormGroup;
  deckEditForm: FormGroup;
  flashcardForm: FormGroup;
  flashcardEditForm: FormGroup;
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
  private unassignedDecks: Deck[] = [];
  private spaces: Space[];
  documents: Map<number, Document[]> = new Map<number, Document[]>();
  selectedDocuments: number[];
  revisionFlashcards: Flashcard[];
  dueDateFlashcards: Flashcard[];
  deleteFlash: boolean = false;
  editRef: boolean = false;
  confidenceError: boolean = false;
  optionError: boolean = false;
  currentRate = 0;


  constructor(private formBuilder: FormBuilder, private flashcardService: FlashcardService,
              private userService: UserService, private spaceService: SpaceService,
              private fileUploadService: FileUploadService, private snackBar: MatSnackBar,
              private sanitizer: DomSanitizer, private dialog: MatDialog) {
    this.deckForm = this.formBuilder.group({
      title: ['', [
        Validators.required,
        Validators.minLength(1)
      ]]
    });
    this.deckEditForm = this.formBuilder.group({
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
     this.flashcardEditForm = this.formBuilder.group({
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
    this.resetDeckForm();
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
  loadDeckDetails(deck: Deck) {
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
               }
       );
    this.deckEditForm.patchValue({
      title: deck.name
    });
  }

  /**
   * @return all flashcards belonging to the logged-in user
   */
  getFlashcards() {
    return this.flashcards;
  }


  prepareFlashcardCreation() {
    this.resetDecks();
    this.loadAllSpaces();
    this.resetFlashcardForm();
    this.selectedDocuments = [];
  }

  loadAllSpaces() {
      this.spaceService.getSpaces(localStorage.getItem('currentUser')).subscribe(
        (spaceList: Space[]) => {
          this.spaces = spaceList;
          for(let i=0; i<this.spaces.length; i++) {
            this.loadDocuments(this.spaces[i]);
          }
        },
        error => {
          this.defaultErrorHandling(error);
        }
      );
    }

    getSpaces() {
      return this.spaces;
    }

    loadDocuments(space: Space) {
      this.spaceService.getAllDocuments(localStorage.getItem('currentUser'), space.id).subscribe(
            (documentList: Document[]) => {
              /*for(let i=0; i<documentList.length; i++) {
                documentList[i].space = space;
              }*/
              this.documents.set(space.id, documentList);
            },
            error => {
              this.defaultErrorHandling(error);
            }
          );
    }

  /**
   * Builds a flashcard dto and sends a creation request.
   */
  createFlashcard() {
       let documentReferences = this.getReferences();
       const flashcard = new Flashcard(0, this.flashcardForm.controls.question.value, this.flashcardForm.controls.answer.value, 0, documentReferences);
       console.log(flashcard);
       this.flashcardService.createFlashcard(flashcard).subscribe(
                       (flashcardCreated: Flashcard) => {
                              console.log(flashcardCreated);
                              this.openSnackbar('You successfully created a flashcard with the question ' + flashcard.question + `!`, 'success-snackbar');
                              this.flashcardService.assignFlashcard(flashcardCreated, this.selectedDecks).subscribe(
                                                         () => {
                                                                  if(this.selectedDeck != undefined) {
                                                                    this.loadDeckDetails(this.selectedDeck);
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

  getReferences() {
     let documentReferences = [];
     console.log("L: " + this.selectedDocuments.length);
     for(let i=0; i< this.spaces.length; i++) {
        let documentObjects = this.documents.get(this.spaces[i].id);
        for(let j=0; j< documentObjects.length; j++) {
          for(let k=0; k < this.selectedDocuments.length; k++) {
            if(documentObjects[j].id == this.selectedDocuments[k]) {
              documentReferences.push(documentObjects[j]);
            }
          }
        }
     }
     return documentReferences;
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
                           this.loadDeckDetails(this.selectedDeck);
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
        this.openSnackbar('You successfully removed the flashcard!', 'success-snackbar');
        this.loadAllDecks();
        this.loadDeckDetails(this.selectedDeck);
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
    if (rate != null) {
      flashcard.confidenceLevel = rate;
    }
    if (this.currentRate < 1 || this.currentRate > 5) {
      this.error = true;
      this.errorMessage = 'Could not rate the flashcard! Please choose the value between 1 and 5.';
      this.openSnackbar(this.errorMessage, 'warning-snackbar');
    } else {
      this.flashcardService.rateFlashcard(flashcard).subscribe(
          () => {
            this.openSnackbar('You successfully rated a flashcard!', 'success-snackbar');
            this.loadDeckDetails(this.selectedDeck);
          },
          error => {
            this.confidenceError = true;
            this.error = true;
            this.errorMessage = 'Could not rate the flashcard! Please choose the value between 1 and 5.';
            this.openSnackbar(this.errorMessage, 'warning-snackbar');
          });
      }
  }

  getUnassignedDecks() {
    return this.unassignedDecks;
  }

  copyFlashcard(flashcard: Flashcard) {
    this.flashcardService.assignFlashcard(flashcard, this.selectedDecks).subscribe(
          () => {
                this.openSnackbar('Flashcard successfully copied or moved', 'success-snackbar');
                this.loadDeckDetails(this.selectedDeck);
          },
          error => {
                  this.error = true;
                  this.errorMessage = 'Could not copy the flashcard!';
                  this.openSnackbar(this.errorMessage, 'warning-snackbar');
          });
  }

  moveFlashcard(flashcard: Flashcard) {
    this.deleteFlashcard(flashcard.id, this.selectedDeck.id);
    this.copyFlashcard(flashcard);
  }

  /**
   * Sends a request to rate a specific flashcard while in revision mode.
   */
  rateFlashcardInRevision(flashcard: Flashcard, rate: number) {
    console.log(flashcard);
    if (rate != null) {
      flashcard.confidenceLevel = rate;
    }
    if (this.currentRate < 1 || this.currentRate > 5) {
      this.error = true;
      this.errorMessage = 'Could not rate the flashcard! Please choose the value between 1 and 5.';
      this.openSnackbar(this.errorMessage, 'warning-snackbar');
    } else {
      this.flashcardService.rateFlashcard(flashcard).subscribe(
        () => {
          this.openSnackbar('You successfully rated a flashcard!', 'success-snackbar');
        },
        error => {
          this.confidenceError = true;
          this.error = true;
          this.errorMessage = 'Could not rate the flashcard! Please choose the value between 1 and 5.';
          this.openSnackbar(this.errorMessage, 'warning-snackbar');
        });
    }
  }

   loadFile(document: Document) {
      this.fileUploadService.getFile(document.space, document.name).subscribe(
        (res) => {
             let fileObject: Blob;
             let blobUrl: any;
             if (document.name.includes('.mp3') || document.name.includes('.mp4')) {
               fileObject = res;
               blobUrl = URL.createObjectURL(fileObject);
             } else if (document.name.includes('.pdf')) {
               fileObject = new Blob([res], {type: 'application/pdf'});
               blobUrl = URL.createObjectURL(fileObject);
             } else {
               fileObject = res;
               blobUrl = this.sanitizer.bypassSecurityTrustResourceUrl(URL.createObjectURL(fileObject));
             }
          this.dialog.open(DocumentDialogComponent, {
            data: {
              currentDocument: document,
              blobUrl: blobUrl
            }
          });
        },
        error => {
          this.defaultErrorHandling(error);
        }
      );
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

  updateReferenceList(select : number){
      console.log("document: " + select);
      if(this.selectedDocuments != undefined) {
        let index = this.selectedDocuments.indexOf(select);
        if(index > -1) {
            this.selectedDocuments.splice(index, 1)
        } else {
            this.selectedDocuments.push(select);
        }
      } else {
        this.selectedDocuments = [select];
      }
   }

   editReferences(flashcard: Flashcard) {
      flashcard.documentReferences = this.getReferences();
      console.log(flashcard.documentReferences);
      this.flashcardService.editFlashcard(flashcard).subscribe(
            () => {
                   this.openSnackbar('You successfully edited flashcard references!', 'success-snackbar');
                   this.loadDeckDetails(this.selectedDeck);
                   },
                   error => {
                     this.error = true;
                     this.errorMessage = 'Could not edit the flashcard references!';
                     this.openSnackbar(this.errorMessage, 'warning-snackbar');
      });
   }

  resetDecks() {
    this.selectedDecks = [];
    this.resetFlashcardForm();
  }

  flashcardClicked(select: Flashcard, del: boolean) {
     console.log(select);
     this.flashcardEditForm.patchValue({
             question: select.question,
             answer: select.answer
      });
     this.selectedFlashcard = select;
     this.showFlashcardId = select.id;
     this.deleteFlash = del;
     this.editRef = false;
     this.selectedDocuments = this.selectedFlashcard.documentReferences.map(({ id }) => id);
     this.currentRate = this.selectedFlashcard.confidenceLevel;
     console.log(this.showFlashcardId);
     //get all decks a flashcard belongs to
     this.flashcardService.getFlashcardAssignments(select.id).subscribe(
         (assignedDecks: number[]) => {
                this.loadAllDecks();
                this.unassignedDecks = [];
                this.decks.forEach(val => this.unassignedDecks.push(Object.assign({}, val)));
                for(let i=0; i< this.decks.length; i++) {
                    let index = assignedDecks.indexOf(this.decks[i].id);
                    if(index > -1) {
                      for(let j=0; j< this.unassignedDecks.length; j++) {
                        if(this.unassignedDecks[j].id==this.decks[i].id) {
                          this.unassignedDecks.splice(j, 1);
                        }
                      }
                    }
                }
         },
         error => {
             this.defaultErrorHandling(error);
          }
     );
     this.selectedDecks = undefined;
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

   backToAll() {
     this.viewAll=true;
     this.resetDeckForm();
     this.loadAllDecks();
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
