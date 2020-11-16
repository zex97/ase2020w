export class AuthResponse {
  constructor(
    public currentToken: string,
    public futureToken: string
  ) {}
}
