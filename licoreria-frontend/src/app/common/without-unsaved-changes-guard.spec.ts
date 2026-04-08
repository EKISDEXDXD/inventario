
import { CanDeactivateFn } from '@angular/router';

export interface HasUnsavedChanges {
  hasUnsavedChanges: () => boolean;
}
export const withoutUnsavedChangesGuard: CanDeactivateFn<HasUnsavedChanges> = (component,) => {
  if (component.hasUnsavedChanges()) {
    return confirm('¿Tienes cambios sin guardar? Si sales, perderás esos cambios. ¿Quieres continuar?');
  }
  
  return true;
  
};