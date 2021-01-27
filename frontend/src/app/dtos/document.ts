import {Space} from './space';

export class Document {
  constructor(public id: number,
              public needsTranscription: boolean,
              public transcription: string,
              public name: string,
              public spaceDTO: Space,
              public filePath: String,
              public tags: string[]) {
  }

}
