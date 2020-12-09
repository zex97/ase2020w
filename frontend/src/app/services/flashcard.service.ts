import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {FormBuilder} from '@angular/forms';
import {AuthService} from './auth.service';
import {Deck} from "../dtos/deck"
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
    console.log('Searching for decks.')
    return this.httpClient.get<Deck[]>(this.flashcardBaseUri + '/' + username);
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
  editDeck(deck: Deck,): Observable<Deck> {
     console.log('Change the deck name to ' + deck.name);
    return this.httpClient.put<Deck>(this.flashcardBaseUri, deck);
  }

  getUsers(): Observable<User[]> {
      console.log('Searching for users.');
      return this.httpClient.get<User[]>(this.globals.backendUri + '/api/user');
    }
}
