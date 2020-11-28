import {User} from "./user"

export class Deck {
  constructor(
    public id: number,
    public name: string,
    public size: number,
    public creationDate: string,
    public lastTimeUsed: string,
    public userDTO: User) {
  }
}
