import { ChangeDetectionStrategy, Component, Input , OnInit} from '@angular/core';
import { animate, state, style, transition, trigger } from '@angular/animations';


type PaneType = 'left' | 'right';

@Component({
  selector: 'app-space-document-slide-panel',
  templateUrl: './space-document-slide-panel.component.html',
  styleUrls: ['./space-document-slide-panel.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  animations: [
    trigger('slide', [
      state('left', style({ transform: 'translateX(0)' })),
      state('right', style({ transform: 'translateX(-50%)' })),
      transition('* => *', animate(300))
    ])
  ]
})
export class SpaceDocumentSlidePanelComponent implements OnInit {

  @Input() activePane: PaneType = 'left';

  // Left panel is the default...
  panelToggle = true;

  constructor() { }

  togglePane() {
    this.panelToggle = !this.panelToggle;
    this.activePane = this.panelToggle ? 'left' : 'right';
  }
  ngOnInit() {}

}
