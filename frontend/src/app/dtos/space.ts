import {User} from "./user"

export class Space {
  constructor(
    public id: number,
    public name: string,
    public description: string,
    public creationDate: string,
    public userDTO: User) {
  }
}
