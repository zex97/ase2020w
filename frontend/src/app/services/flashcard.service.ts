import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {AuthService} from './auth.service';
import {Deck} from '../dtos/deck';
import {Flashcard} from '../dtos/flashcard';
import {User} from '../dtos/user';


@Injectable({
  providedIn: 'root'
})
export class FlashcardService {

  private flashcardBaseUri: string = this.globals.backendUri + '/api/flashcards';

  constructor(private httpClient: HttpClient, private globals: Globals, private authService: AuthService) {
  }

  /**
   * Loads all decks from the backend
   */
  getDecks(username: string): Observable<Deck[]> {
    console.log('Searching for decks.');
    return this.httpClient.get<Deck[]>(this.flashcardBaseUri + '/' + username);
  }

  /**
   * Loads a deck from the backend
   */
  getDeckById(deckId: number): Observable<Deck> {
    console.log('Searching for a deck.');
    return this.httpClient.get<Deck>(this.flashcardBaseUri + '/deck' + deckId);
  }

  /**
   * Persists deck to the backend
   * @param deck to persist
   */
  createDeck(deck: Deck): Observable<Deck> {
    console.log('Create deck with name ' + deck.name);
    return this.httpClient.post<Deck>(this.flashcardBaseUri, deck);
  }

   /**
    * Change deck name in the backend
    * @param deck to make changes to
    */
  editDeck(deck: Deck): Observable<Deck> {
     console.log('Change the deck name to ' + deck.name);
    return this.httpClient.put<Deck>(this.flashcardBaseUri, deck);
  }

   /**
    * Loads all flashcards belonging to a deck from the backend
    * @param deckId of the deck flashcards belong to
    */
  getFlashcards(deckId: number): Observable<Flashcard[]> {
      console.log('Searching for flashcards.');
      return this.httpClient.get<Flashcard[]>(this.flashcardBaseUri + '/' + deckId + '/flashcards');

  }

   /**
    * Persists flashcard to the backend
    * @param flashcard to persist
    */
  createFlashcard(flashcard: Flashcard, deckId: number): Observable<Flashcard> {
      console.log('Create flashcard with question ' + flashcard.question);
      return this.httpClient.post<Flashcard>(this.flashcardBaseUri + '/' + deckId, flashcard);
  }

  /**
   * Change flashcard question, answer or rating in the backend
   * @param flashcard to make changes to
   */
  editFlashcard(flashcard: Flashcard, deckId: number): Observable<Flashcard> {
        console.log('Edit flashcard - question ' + flashcard.question);
        return this.httpClient.put<Flashcard>(this.flashcardBaseUri + '/' + deckId + '/flashcard' + flashcard.id, flashcard);
  }

  /**
   * Sends a revision method call to the backend
   * @param size of question set for the revision
   * @param deckId of the deck whose flashcards are going to be revised
   */
  revise(size: number, deckId: number): Observable<Flashcard[]> {
        console.log('Getting flashcards for revision.');
        return this.httpClient.get<Flashcard[]>(this.flashcardBaseUri + '/' + deckId + '/flashcards/' + size);
  }

  /**
   * Loads all users from the backend
   */
  getUsers(): Observable<User[]> {
      console.log('Searching for users.');
      return this.httpClient.get<User[]>(this.globals.backendUri + '/api/user');
  }

  /**
   * Delete a deck from the backend
   * @param deckId of the deck to be deleted
   */
  deleteDeck(deckId: number): Observable<Deck> {
    console.log('Delete a deck');
    return this.httpClient.delete<Deck>(this.flashcardBaseUri + '/' + deckId);
  }

  /**
   * Delete a flashcard from the backend
   * @param flashcardId of the flashcard to be deleted
   * @param deckId of the deck in which the flashcard is
   */
  deleteFlashcard(flashcardId: number, deckId: number): Observable<Flashcard> {
    console.log('Delete a flashcard');
    return this.httpClient.delete<Flashcard>(this.flashcardBaseUri + '/' + deckId + '/' + flashcardId);
  }
}
