import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import {FormBuilder} from '@angular/forms';
import {AuthService} from './auth.service';
import {Deck} from "../dtos/deck"


@Injectable({
  providedIn: 'root'
})
export class FlashcardService {

  private flashcardBaseUri: string = this.globals.backendUri + '/flashcards';

  constructor(private httpClient: HttpClient, private globals: Globals, private authService: AuthService) {
  }

  /**
   * Loads all decks from the backend
   */
  getDecks(): Observable<Deck[]> {
    return this.httpClient.get<Deck[]>(this.flashcardBaseUri);
  }

  /**
   * Loads specific deck from the backend
   * @param id of deck to load
   */
  getDeckById(id: number): Observable<Deck> {
    console.log('Load deck with id ' + id);
    return this.httpClient.get<Deck>(this.flashcardBaseUri + '/' + this.authService.getToken() + '/' + id);
  }

  /**
   * Persists deck to the backend
   * @param deck to persist
   */
  createDeck(deck: Deck): Observable<Deck> {
    console.log('Create deck with name ' + deck.name);
    return this.httpClient.post<Deck>(this.flashcardBaseUri, deck);
  }
}
