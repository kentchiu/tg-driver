export type AuthStage = 'PHONE_NUMBER' | 'AUTHENTICATED' | 'AUTH_CODE' | undefined;

export type AuthState = {
  stage: AuthStage;
  phoneNumber?: string;
};
