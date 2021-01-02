import {Deck} from './deck';
import {Document} from './document';

export class Flashcard {
  constructor(
    public id: number,
    public question: string,
    public answer: string,
    public confidenceLevel: number,
    public deckDTOs: Deck[],
    public documentReferences: Document[]) {
  }
}
