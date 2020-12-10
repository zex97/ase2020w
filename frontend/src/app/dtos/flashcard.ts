import {Deck} from "./deck"

export class Flashcard {
  constructor(
    public id: number,
    public question: string,
    public answer: string,
    public confidence_level: number,
    public deckDTO: Deck) {
  }
}
