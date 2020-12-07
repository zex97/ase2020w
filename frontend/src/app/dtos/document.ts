import {Space} from './space';

export class Document {
  constructor(public id: number,
              public needsTranscription: boolean,
              public transcription: String,
              public name: String,
              public space: Space,
              public filePath: String) {
  }

}
