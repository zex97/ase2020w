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
  getDecks(userId: number): Observable<Deck[]> {
    console.log('Searching for decks.')
    return this.httpClient.get<Deck[]>(this.flashcardBaseUri + '/' + userId);
  }

  /**
   * Loads specific deck from the backend
   * @param id of deck to load
   */
  getDeckById(id: number): Observable<Deck> {
    console.log('Load deck with id ' + id);
    return this.httpClient.get<Deck>(this.flashcardBaseUri + '/3' + '/deck' + id);
  }

  /**
   * Persists deck to the backend
   * @param deck to persist
   */
  createDeck(deck: Deck): Observable<Deck> {
    console.log('Create deck with name ' + deck.name);
    console.log(deck);
    return this.httpClient.post<Deck>(this.flashcardBaseUri + '/3', deck);
  }

  getUsers(): Observable<User[]> {
      console.log('Searching for users.');
      return this.httpClient.get<User[]>(this.globals.backendUri + '/api/user');
    }
}
