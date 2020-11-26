import { Component, OnInit } from '@angular/core';
import {FlashcardService} from '../../services/flashcard.service';
import {Deck} from "../../dtos/deck"

@Component({
  selector: 'app-flashcard-manager',
  templateUrl: './flashcard-manager.component.html',
  styleUrls: ['./flashcard-manager.component.scss']
})

export class FlashcardManagerComponent implements OnInit {

  constructor(private flashcardService: FlashcardService) { }

  ngOnInit(): void {
  }

  /**
   * Builds a deck dto and sends a creation request.
   * If the procedure was successful, the form will be cleared.
   */
  createDeck() {
    const deck : Deck = new Deck(null, "test", new Date().toISOString(), new Date().toISOString());
    this.flashcardService.createDeck(deck);
    //this.clearForm();
  }

}
