export class JwtHelper {
  private readonly RADIX = 10;

  /**
   * Decode the JWT token and extract the payload
   */
  decodeToken(token: string): Record<string, unknown> | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) {
        throw new Error('Invalid token');
      }

      const decoded = this.base64UrlDecode(parts[1]);
      return JSON.parse(decoded) as Record<string, unknown>;
    } catch {
      console.error('Error decoding token');
      return null;
    }
  }

  /**
   * Decode the JWT token and extract the payload
   */
  decodeToken(token: string): Record<string, unknown> | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) {
        throw new Error('Invalid token');
      }

      const decoded = this.base64UrlDecode(parts[1]);
      return JSON.parse(decoded);
    } catch {
      console.error('Error decoding token');
      return null;
    }
  }

  /**
   * Get the userId from the JWT token
   */
  getUserId(token: string): number | null {
    const payload = this.decodeToken(token);
    if (payload && typeof payload.userId !== 'undefined') {
      const userId = payload.userId as string | number;
      return typeof userId === 'string' ? parseInt(userId, this.RADIX) : userId;
    }
    return null;
  }

  /**
   * Get the username from the JWT token
   */
  getUsername(token: string): string | null {
    const payload = this.decodeToken(token);
    if (payload && typeof payload.sub === 'string') {
      return payload.sub;
    }
    return null;
  }

  /**
   * Get the role from the JWT token
   */
  getRole(token: string): string | null {
    const payload = this.decodeToken(token);
    if (payload && typeof payload.role === 'string') {
      return payload.role;
    }
    return null;
  }

  /**
   * Base64URL decode implementation
   */
  private base64UrlDecode(str: string): string {
    let output = str.replace(/-/g, '+').replace(/_/g, '/');
    switch (output.length % 4) {
      case 0:
        break;
      case 2:
        output += '==';
        break;
      case 3:
        output += '=';
        break;
      default:
        throw new Error('Invalid base64url string');
    }

    try {
      return decodeURIComponent(atob(output).split('').map((c: string) => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
    } catch (e) {
      return atob(output);
    }
  }
}
