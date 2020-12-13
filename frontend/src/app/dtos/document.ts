import {Space} from './space';

export class Document {
  constructor(public id: number,
              public needsTranscription: boolean,
              public transcription: string,
              public name: string,
              public space: Space,
              public filePath: String) {
  }

}
