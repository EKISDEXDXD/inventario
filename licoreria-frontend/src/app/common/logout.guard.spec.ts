import { CanDeactivateFn } from '@angular/router';

export const LogoutGuard: CanDeactivateFn<any> = (
  component,
  currentRoute,
  currentState,
  nextState
) => {
  // Siempre preguntar si sales de home a login
  if (currentState.url === '/' && nextState?.url === '/login') {
    return confirm('¿Seguro quieres salir de tu cuenta?');
  }
  
  // Para otras navegaciones desde home, permitir sin preguntar
  return true;
};