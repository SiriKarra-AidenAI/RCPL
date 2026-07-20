import { ChangeDetectionStrategy, Component, input, signal } from '@angular/core'

// Renders the Reliance Consumer Products logo from public/rcpl-logo.png.
// Falls back to the styled "R" letter mark if that file hasn't been added yet,
// so the app never shows a broken image.
@Component({
  selector: 'app-brand-mark',
  standalone: true,
  templateUrl: './BrandMark.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BrandMarkComponent {
  readonly variant = input<'mark' | 'full'>('mark')

  protected readonly failed = signal(false)

  protected onError(): void {
    this.failed.set(true)
  }
}
